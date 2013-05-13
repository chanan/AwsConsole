package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.CreateInstance;
import models.InstanceViewModel;
import models.LocalInstance;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.Amazon;
import services.AmazonImpl;
import services.LocalStore;
import services.LocalStoreImpl;
import akka.actor.TypedActor;
import akka.actor.TypedProps;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;

public class Application extends Controller {
	private final static Amazon amazon = TypedActor.get(Akka.system()).typedActorOf(new TypedProps<AmazonImpl>(Amazon.class, AmazonImpl.class));
	private final static LocalStore localStore = TypedActor.get(Akka.system()).typedActorOf(new TypedProps<LocalStoreImpl>(LocalStore.class, LocalStoreImpl.class));
	
	@SecureSocial.SecuredAction
	public static Result index() {
		return redirect(routes.Application.instances());
	}
	
	@SecureSocial.SecuredAction
	public static Result createInstance() {
		final List<F.Promise<? extends Map<String, String>>> list = new ArrayList<F.Promise<? extends Map<String, String>>>();
		list.add(Akka.asPromise(amazon.getImages()));
		list.add(Akka.asPromise(amazon.getKeysMap()));
		list.add(Akka.asPromise(amazon.getSecurityGroupsMap()));
		final F.Promise<List<Map<String, String>>> promise = F.Promise.sequence(list);
		final CreateInstance defaults = new CreateInstance();
		defaults.powerSaveMode = true;
		defaults.type = InstanceType.T1Micro.toString();
		defaults.key = "DevOps";
		final Form<CreateInstance> createInstanceForm = form(CreateInstance.class).fill(defaults);
		final Map<String, String> types = CreateInstance.getInstanceTypes();
		return async(promise.map(
			new Function<List<Map<String, String>>, Result>() {
				@Override
				public Result apply(List<Map<String, String>> list) throws Throwable {
					return ok(views.html.createInstance.render(list.get(0), list.get(1), list.get(2), types, createInstanceForm));
				}
				
			})
		);
	}
	
	@SecureSocial.SecuredAction
	public static Result doCreateInstance() {
		final Form<CreateInstance> createInstanceForm = form(CreateInstance.class).bindFromRequest();
		if(createInstanceForm.hasErrors()) {
			final List<F.Promise<? extends Map<String, String>>> list = new ArrayList<F.Promise<? extends Map<String, String>>>();
			list.add(Akka.asPromise(amazon.getImages()));
			list.add(Akka.asPromise(amazon.getKeysMap()));
			list.add(Akka.asPromise(amazon.getSecurityGroupsMap()));
			final F.Promise<List<Map<String, String>>> promise = F.Promise.sequence(list);
			final Map<String, String> types = CreateInstance.getInstanceTypes();
			return async(promise.map(
				new Function<List<Map<String, String>>, Result>() {
					@Override
					public Result apply(List<Map<String, String>> list) throws Throwable {
						return badRequest(views.html.createInstance.render(list.get(0), list.get(1), list.get(2), types, createInstanceForm));
					}
					
				})
			);
		} else {
			amazon.createInstance(createInstanceForm.get());
			return redirect(routes.Application.instances()); 
		}
	}
	
	@SecureSocial.SecuredAction
    public static Result instances() {
		return async(
			Akka.asPromise(amazon.listInstances()).flatMap(
				new Function<List<Instance>, Promise<Result>> () {
					@Override
					public Promise<Result> apply(final List<Instance> instances) throws Throwable {
						return Akka.asPromise(localStore.listLocalInstances(instances)).map(
							new Function<List<Tuple<Instance, LocalInstance>>, Result>() {
								@Override
								public Result apply(final List<Tuple<Instance, LocalInstance>> pairs) throws Throwable {
									final List<InstanceViewModel> viewModels = getViewModels(pairs);
									return ok(views.html.instances.render(viewModels));
								}	
							}
						);
					}
				}
			)
		);
    }
    
	private static List<InstanceViewModel> getViewModels(List<Tuple<Instance, LocalInstance>> pairs) {
		final List<InstanceViewModel> list = new ArrayList<InstanceViewModel>();
		for(final Tuple<Instance, LocalInstance> pair : pairs) {
			list.add(new InstanceViewModel(pair._1, pair._2));
		}
		return list;
	}

	@SecureSocial.SecuredAction
    public static Result startInstance() {
    	DynamicForm requestData = new DynamicForm().bindFromRequest();
    	String instanceId = requestData.get("instanceId");
    	amazon.startInstance(instanceId);
    	return redirect(routes.Application.instances());
    }
    
	@SecureSocial.SecuredAction
    public static Result stopInstance() {
    	DynamicForm requestData = new DynamicForm().bindFromRequest();
    	String instanceId = requestData.get("instanceId");
    	amazon.stopInstance(instanceId);
    	return redirect(routes.Application.instances());
    }
    
	@SecureSocial.SecuredAction
    public static Result changePowerSaveMode() {
    	DynamicForm requestData = new DynamicForm().bindFromRequest();
    	String instanceId = requestData.get("instanceId");
    	boolean powerSave = requestData.get("powerSave") != null;
    	localStore.changePowerSaveState(instanceId, powerSave);
    	return redirect(routes.Application.instances());
    }
	
	public static Result healthCheck() {
		return ok("I am healthy!");
	}
  
}

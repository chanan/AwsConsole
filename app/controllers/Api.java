package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.CreateInstance;
import models.InstanceViewModel;
import models.LocalInstance;

import com.amazonaws.services.ec2.model.Instance;

import akka.actor.TypedActor;
import akka.actor.TypedProps;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Future;
import securesocial.core.java.SecureSocial;
import services.Amazon;
import services.AmazonImpl;
import services.LocalStore;
import services.LocalStoreImpl;

public class Api extends Controller {
	private final static Amazon amazon = TypedActor.get(Akka.system()).typedActorOf(new TypedProps<AmazonImpl>(Amazon.class, AmazonImpl.class));
	private final static LocalStore localStore = TypedActor.get(Akka.system()).typedActorOf(new TypedProps<LocalStoreImpl>(LocalStore.class, LocalStoreImpl.class));
	
	
	@SecureSocial.SecuredAction(ajaxCall = true)
	public static Result instances() {
		return async(
			Akka.asPromise(amazon.listInstances())
			    .flatMap(withLocalInstances())
			    .map(toViewModels())
			    .map(getListResult())
		);
    }
	
	 private static Function<List<Instance>, Promise<List<Tuple<Instance, LocalInstance>>>> withLocalInstances() {
		 return new Function<List<Instance>, Promise<List<Tuple<Instance, LocalInstance>>>> () {
			 @Override
			 public Promise<List<Tuple<Instance, LocalInstance>>> apply(final List<Instance> instances) throws Throwable {
				 return Akka.asPromise(localStore.listLocalInstances(instances));
            }
        };
	 }
	 
	 private static Function<List<Instance>, Promise<List<Tuple<Instance, LocalInstance>>>> withLocalInstance() {
		 return new Function<List<Instance>, Promise<List<Tuple<Instance, LocalInstance>>>> () {
			 @Override
			 public Promise<List<Tuple<Instance, LocalInstance>>> apply(final List<Instance> instances) throws Throwable {
				 return Akka.asPromise(localStore.listLocalInstance(instances));
            }
        };
	 }

    private static Function<List<Tuple<Instance, LocalInstance>>, List<InstanceViewModel>> toViewModels() {
        return new Function<List<Tuple<Instance, LocalInstance>>, List<InstanceViewModel>>() {
            @Override
            public List<InstanceViewModel> apply(final List<Tuple<Instance, LocalInstance>> pairs) throws Throwable {
                final List<InstanceViewModel> list = new ArrayList<InstanceViewModel>();
                for(final Tuple<Instance, LocalInstance> pair : pairs) {
                    list.add(new InstanceViewModel(pair._1, pair._2));
                }
                return list;
            }
        };
    }

    private static Function<List<InstanceViewModel>, Result> getListResult() {
        return new Function<List<InstanceViewModel>, Result>() {
            @Override
            public Result apply(List<InstanceViewModel> viewModels) throws Throwable {
                return ok(Json.toJson(viewModels));
            }
        };
    }
    
    private static Function<List<InstanceViewModel>, Result> getResult() {
        return new Function<List<InstanceViewModel>, Result>() {
            @Override
            public Result apply(List<InstanceViewModel> viewModels) throws Throwable {
                return ok(Json.toJson(viewModels.get(0)));
            }
        };
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result startInstance(String instanceId) {
    	amazon.startInstance(instanceId);
    	return noContent();
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result stopInstance(String instanceId) {
    	amazon.stopInstance(instanceId);
    	return noContent();
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result instance(String instanceId) {
    	return async(
			Akka.asPromise(amazon.listInstances(instanceId))
			    .flatMap(withLocalInstance())
			    .map(toViewModels())
			    .map(getResult())
		);
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result changePowerSave(String instanceId) {
    	localStore.changePowerSaveState(instanceId);
    	return noContent();
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result images() {
    	return convertFutureMapToResult(amazon.getImages());
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result keys() {
    	return convertFutureMapToResult(amazon.getKeysMap());
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result securityGroups() {
    	return convertFutureMapToResult(amazon.getSecurityGroupsMap());
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
    public static Result types() {
    	return ok(Json.toJson(CreateInstance.getInstanceTypes()));
    }
    
    private static AsyncResult convertFutureMapToResult(Future<Map<String, String>> futureMap) {
    	return async(
    		Akka.asPromise(futureMap).map(
    			new Function<Map<String, String>, Result>() {
					@Override
					public Result apply(Map<String, String> map) throws Throwable {
						return ok(Json.toJson(map));
					}
    			}
			)
		);
    }
    
    @SecureSocial.SecuredAction(ajaxCall = true)
	public static Result createInstance() {
		final Form<CreateInstance> createInstanceForm = form(CreateInstance.class).bindFromRequest();
		if(createInstanceForm.hasErrors()) {
			return badRequest(createInstanceForm.errorsAsJson());
		} else {
			amazon.createInstance(createInstanceForm.get());
			return noContent(); 
		}
	}
}

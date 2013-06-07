package controllers;

import java.util.ArrayList;
import java.util.List;

import models.InstanceViewModel;
import models.LocalInstance;

import com.amazonaws.services.ec2.model.Instance;

import akka.actor.TypedActor;
import akka.actor.TypedProps;
import play.data.DynamicForm;
import play.libs.Akka;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
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

}

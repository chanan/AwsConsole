package services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import models.LocalInstance;
import play.libs.Akka;
import scala.concurrent.Future;
import akka.dispatch.Futures;

import com.amazonaws.services.ec2.model.Instance;

public class LocalStoreImpl implements LocalStore {

	@Override
	public Future<Map<String, LocalInstance>> listLocalInstances(final List<Instance> instances) {
		return Futures.future(new Callable<Map<String, LocalInstance>>() {
			@Override
			public Map<String, LocalInstance> call() throws Exception {
				final Map<String, LocalInstance> returnMap = new HashMap<String, LocalInstance>();
				final List<LocalInstance> dbList = LocalInstance.find.all();
				final Map<String, LocalInstance> dbMap = createLocalInstanceMap(dbList);
				final Map<String, Instance> instanceMap = createInstanceMap(instances);
				for(final Instance instance : instances) {
					if(!dbMap.containsKey(instance.getInstanceId())) {
						final LocalInstance localInstance = new LocalInstance(instance.getInstanceId());
						localInstance.save();
						returnMap.put(localInstance.instanceId, localInstance);
					}
				}
				for(final LocalInstance localInstance : dbList) {
					if(!instanceMap.containsKey(localInstance.instanceId)) {
						localInstance.delete();
					} else {
						returnMap.put(localInstance.instanceId, localInstance);
					}
				}
				return returnMap;
			}
			
		}, Akka.system().dispatcher());
	}
	
	@Override
	public void changePowerSaveState(String instanceId, boolean powerSave) {
		LocalInstance localInstance = LocalInstance.find.byId(instanceId);
		if(localInstance != null) {
			localInstance.powerSave = powerSave;
			localInstance.update();
		}
	}
	
	private Map<String, LocalInstance> createLocalInstanceMap(List<LocalInstance> dbList) {
		final Map<String, LocalInstance> map = new HashMap<String, LocalInstance>();
		for(final LocalInstance localInstance : dbList) {
			map.put(localInstance.instanceId, localInstance);
		}
		return map;
	}
	
	private Map<String, Instance> createInstanceMap(List<Instance> instanceList) {
		final Map<String, Instance> map = new HashMap<String, Instance>();
		for(final Instance instance : instanceList) {
			map.put(instance.getInstanceId(), instance);
		}
		return map;
	}
}

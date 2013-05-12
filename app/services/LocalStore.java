package services;

import java.util.List;
import java.util.Map;

import scala.concurrent.Future;

import com.amazonaws.services.ec2.model.Instance;

import models.LocalInstance;

public interface LocalStore {
	public Future<Map<String, LocalInstance>> listLocalInstances(List<Instance> instances);
	public void changePowerSaveState(String instanceId, boolean powerSave);
}

package services;

import java.util.List;

import models.LocalInstance;

import org.javatuples.Pair;

import scala.concurrent.Future;

import com.amazonaws.services.ec2.model.Instance;

public interface LocalStore {
	public Future<List<Pair<Instance, LocalInstance>>> listLocalInstances(List<Instance> instances);
	public void changePowerSaveState(String instanceId, boolean powerSave);
}

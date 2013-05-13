package services;

import java.util.List;

import models.LocalInstance;
import play.libs.F.Tuple;
import scala.concurrent.Future;

import com.amazonaws.services.ec2.model.Instance;

public interface LocalStore {
	public Future<List<Tuple<Instance, LocalInstance>>> listLocalInstances(List<Instance> instances);
	public void changePowerSaveState(String instanceId, boolean powerSave);
}

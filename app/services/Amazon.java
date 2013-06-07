package services;

import java.util.List;
import java.util.Map;

import scala.concurrent.Future;

import models.CreateInstance;

import com.amazonaws.services.ec2.model.Instance;

public interface Amazon {
	public Future<List<Instance>> listInstances();
	public Future<List<Instance>> listInstances(String instanceId);
	public void startInstance(String instanceId);
	public void stopInstance(String instanceId);
	public Future<Map<String, String>> getImages();
	public Future<Map<String, String>> getKeysMap();
	public Future<Map<String, String>> getSecurityGroupsMap();
	public void createInstance(CreateInstance createInstance);
}

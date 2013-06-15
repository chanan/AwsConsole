package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.CreateInstance;
import models.LocalInstance;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import akka.dispatch.Futures;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.typesafe.config.ConfigFactory;

public class AmazonImpl implements Amazon {
	private final BasicAWSCredentials credentials = new BasicAWSCredentials(ConfigFactory.load().getString("aws.accesskey"), ConfigFactory.load().getString("aws.secretkey"));
	private final AmazonEC2AsyncClient client = new AmazonEC2AsyncClient(credentials);
	private final String owner = ConfigFactory.load().getString("aws.owner");
	
	@Override
	public Future<List<Instance>> listInstances() {
		return listInstances(null);
	}
	
	@Override
	public Future<List<Instance>> listInstances(String instanceId) {
		final Promise<List<Instance>> promise = Futures.promise();
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		if(instanceId != null) request.withInstanceIds(instanceId);
		client.describeInstancesAsync(request, new AsyncHandler<DescribeInstancesRequest,DescribeInstancesResult>() {
			@Override
			public void onError(Exception ex) {
				promise.failure(ex);
			}
			@Override
			public void onSuccess(DescribeInstancesRequest request, DescribeInstancesResult result) {
				final List<Instance> list = new ArrayList<Instance>();
				for (final Reservation reservation : result.getReservations()) {
					list.addAll(reservation.getInstances());
				}
				promise.success(list);
			}
		});
		return promise.future();
	}

	@Override
	public void startInstance(String instanceId) {
		final DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceId);
		final DescribeInstancesResult describeInstanceResult = client.describeInstances(request);
		final Instance instance = describeInstanceResult.getReservations().get(0).getInstances().get(0);
		if(instance.getState().getName().equalsIgnoreCase("stopped")) {
			final StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(instanceId);
			client.startInstances(startRequest);
		}
	}

	@Override
	public void stopInstance(String instanceId) {
		final DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceId);
		final DescribeInstancesResult describeInstanceResult = client.describeInstances(request);
		final Instance instance = describeInstanceResult.getReservations().get(0).getInstances().get(0);
		if(instance.getState().getName().equalsIgnoreCase("running")) {
			final StopInstancesRequest stopRequest = new StopInstancesRequest().withInstanceIds(instanceId);
			client.stopInstances(stopRequest);
		}
	}

	@Override
	public Future<Map<String, String>> getImages() {
		final DescribeImagesRequest request = new DescribeImagesRequest().withOwners(owner);
		final Promise<Map<String, String>> promise = Futures.promise();
		client.describeImagesAsync(request, new AsyncHandler<DescribeImagesRequest, DescribeImagesResult>() {
			@Override
			public void onError(Exception ex) {
				promise.failure(ex);
			}

			@Override
			public void onSuccess(DescribeImagesRequest request, DescribeImagesResult result) {
				final Map<String, String> map = new HashMap<String, String>();
				for(final Image image : result.getImages()) {
					map.put(image.getImageId(), image.getName());
				}
				promise.success(map);
			}
		});
		return promise.future();
	}

	@Override
	public Future<Map<String, String>> getKeysMap() {
		final Promise<Map<String, String>> promise = Futures.promise();
		client.describeKeyPairsAsync(new DescribeKeyPairsRequest(), new AsyncHandler<DescribeKeyPairsRequest, DescribeKeyPairsResult>() {
			@Override
			public void onError(Exception ex) {
				promise.failure(ex);
			}

			@Override
			public void onSuccess(DescribeKeyPairsRequest request, DescribeKeyPairsResult result) {
				final Map<String, String> map = new HashMap<String, String>();
				for(final KeyPairInfo info : result.getKeyPairs()) {
					map.put(info.getKeyName(), info.getKeyName());
				}
				promise.success(map);
			}
		});
		return promise.future();
	}

	@Override
	public Future<Map<String, String>> getSecurityGroupsMap() {
		final Promise<Map<String, String>> promise = Futures.promise();
		 client.describeSecurityGroupsAsync(new DescribeSecurityGroupsRequest(), new AsyncHandler<DescribeSecurityGroupsRequest, DescribeSecurityGroupsResult>() {
			@Override
			public void onError(Exception ex) {
				promise.failure(ex);
			}

			@Override
			public void onSuccess(DescribeSecurityGroupsRequest request, DescribeSecurityGroupsResult result) {
				final Map<String, String> map = new HashMap<String, String>();
				for(final SecurityGroup group : result.getSecurityGroups()) {
					map.put(group.getGroupId(), group.getGroupName());
				}
				promise.success(map);
			}
			 
		 });
		return promise.future();
	}

	@Override
	public void createInstance(CreateInstance createInstance) {
		final RunInstancesRequest request = new RunInstancesRequest(createInstance.getImageId(), 1, 1)
			.withSecurityGroupIds(createInstance.getGroup()).withKeyName(createInstance.getKey())
			.withInstanceType(createInstance.getType());
		final RunInstancesResult result = client.runInstances(request);
		final Instance instance = result.getReservation().getInstances().get(0);
		final Tag tag = new Tag("Name", createInstance.getName());
		final CreateTagsRequest tagsRequest = new CreateTagsRequest()
			.withResources(instance.getInstanceId()).withTags(tag);
		client.createTags(tagsRequest);
		if(createInstance.isPowerSaveMode()) {
			LocalInstance localInstance = new LocalInstance(instance.getInstanceId());
			localInstance.powerSave = true;
			localInstance.save();
		}
	}
}

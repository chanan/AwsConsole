package models;

import services.AmazonUtils;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;

public class InstanceViewModel {
	private final String instanceId;
	private final String name;
	private final String type;
	private final String zone;
	private final String key;
	private final String securityGroup;
	private final String lastLaunchTime;
	private final String dns;
	private final String state;
	private final boolean powerSaveMode;
	private final boolean disallowPowerSave;
	
	public InstanceViewModel(Instance instance, LocalInstance localInstance) {
		instanceId = instance.getInstanceId();
		name = AmazonUtils.getTagValue(instance.getTags(), "name");
		type = instance.getInstanceType();
		zone = instance.getPlacement().getAvailabilityZone();
		key = instance.getKeyName();
		securityGroup = getSecurityGroups(instance);
		lastLaunchTime = AmazonUtils.getEstTime(instance.getLaunchTime());
		dns = instance.getPublicDnsName();
		state = instance.getState().getName();
		powerSaveMode = localInstance.powerSave;
		disallowPowerSave = Boolean.parseBoolean(AmazonUtils.getTagValue(instance.getTags(), "Disallow Power Save Mode"));
	}
	
	private String getSecurityGroups(Instance instance) {
		StringBuffer sb = new StringBuffer();
		for(final GroupIdentifier group : instance.getSecurityGroups()) {
			sb.append(group.getGroupName()).append(", ");
		}
		return sb.toString().substring(0, sb.toString().length() - 2);
	}

	public final String getInstanceId() {
		return instanceId;
	}

	public final String getName() {
		return name;
	}

	public final String getType() {
		return type;
	}

	public final String getZone() {
		return zone;
	}

	public final String getKey() {
		return key;
	}

	public final String getSecurityGroup() {
		return securityGroup;
	}

	public final String getLastLaunchTime() {
		return lastLaunchTime;
	}

	public final String getDns() {
		return dns;
	}

	public final String getState() {
		return state;
	}

	public final boolean isPowerSaveMode() {
		return powerSaveMode;
	}

	public final boolean isDisallowPowerSave() {
		return disallowPowerSave;
	}
}

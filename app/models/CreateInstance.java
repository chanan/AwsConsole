package models;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.ec2.model.InstanceType;

import play.data.validation.Constraints.Required;

public class CreateInstance {
	@Required
	private String name;
	@Required
	private String imageId;
	@Required
	private String key;
	@Required
	private String group;
	@Required
	private String type;
	private boolean powerSaveMode;
	
	
	public static Map<String, String> getInstanceTypes() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put(InstanceType.T1Micro.toString(), InstanceType.T1Micro.toString());
		map.put(InstanceType.M1Small.toString(), InstanceType.M1Small.toString());
		map.put(InstanceType.M1Medium.toString(), InstanceType.M1Medium.toString());
		map.put(InstanceType.M1Large.toString(), InstanceType.M1Large.toString());
		map.put(InstanceType.M1Xlarge.toString(), InstanceType.M1Xlarge.toString());
		return map;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getImageId() {
		return imageId;
	}


	public void setImageId(String imageId) {
		this.imageId = imageId;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getGroup() {
		return group;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isPowerSaveMode() {
		return powerSaveMode;
	}


	public void setPowerSaveMode(boolean powerSaveMode) {
		this.powerSaveMode = powerSaveMode;
	}
}

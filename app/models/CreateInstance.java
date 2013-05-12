package models;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.ec2.model.InstanceType;

import play.data.validation.Constraints.Required;

public class CreateInstance {
	@Required
	public String name;
	@Required
	public String imageId;
	@Required
	public String key;
	@Required
	public String group;
	@Required
	public String type;
	public boolean powerSaveMode;
	
	
	public static Map<String, String> getInstanceTypes() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put(InstanceType.T1Micro.toString(), InstanceType.T1Micro.toString());
		map.put(InstanceType.M1Small.toString(), InstanceType.M1Small.toString());
		map.put(InstanceType.M1Medium.toString(), InstanceType.M1Medium.toString());
		map.put(InstanceType.M1Large.toString(), InstanceType.M1Large.toString());
		map.put(InstanceType.M1Xlarge.toString(), InstanceType.M1Xlarge.toString());
		return map;
	}
}

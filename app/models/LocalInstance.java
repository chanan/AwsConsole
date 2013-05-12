package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class LocalInstance extends Model {
	private static final long serialVersionUID = 8516159997614324918L;
	
	public LocalInstance() { }
	
	public LocalInstance(String instanceId) {
		this.instanceId = instanceId;
		powerSave = false;
	}
	
	@Id
	@Required
	public String instanceId;
	
	public boolean powerSave;
	
	public static Finder<String, LocalInstance> find = new Finder<String, LocalInstance>(
			String.class, LocalInstance.class
    );
}

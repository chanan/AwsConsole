package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.db.ebean.Model;


@Entity
public class LocalUser extends Model  {

	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;

	@JsonIgnore
	public String provider;

	public String firstName;

	public String lastName;

	public String email;

	@JsonIgnore
	public String password;
	
	public static Finder<String, LocalUser> find = new Finder<String, LocalUser>(
			String.class, LocalUser.class
    );

	@Override
	public String toString() {
		return this.id + " - " + this.firstName;
	}
	
	

}

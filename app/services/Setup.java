package services;

import com.typesafe.config.ConfigFactory;

import models.LocalUser;
import scala.Option;
import securesocial.core.Registry;
import securesocial.core.providers.utils.PasswordHasher;
import akka.actor.UntypedActor;

public class Setup extends UntypedActor {
	private final String email = ConfigFactory.load().getString("application.firstemail");
	private final String firstname = ConfigFactory.load().getString("application.firstfirstname");
	private final String lastname = ConfigFactory.load().getString("application.firstlastname");
	private final String password = ConfigFactory.load().getString("application.firstpassword");

	@Override
	public void onReceive(Object arg0) throws Exception {
		int size = LocalUser.find.all().size();
		if(size == 0) {
			createOneUser();
		}
	}
	
	private void createOneUser() {
		Option<PasswordHasher> option = Registry.hashers().get("bcrypt");
		PasswordHasher hasher = option.get();
		String hashed = hasher.hash(password).password();
		LocalUser localUser = new LocalUser();
		localUser.id = email;
		localUser.provider = "userpass";
		localUser.firstName = firstname;
		localUser.lastName = lastname;
		localUser.email = email;
		localUser.password = hashed;
		localUser.save();
	}
}

package controllers;

import java.util.List;

import models.LocalUser;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.*;
import securesocial.core.java.SecureSocial;
import static play.data.Form.*;

public class Users extends Controller {
	
	@SecureSocial.SecuredAction
	public static Result index() {
		List<LocalUser> list = LocalUser.find.all();
		Form<String> formEmail = form(String.class);
		return ok(views.html.users.index.render(list, formEmail));
	}
}

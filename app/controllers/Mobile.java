package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Mobile extends Controller {
	
	public static Result index() {
		return ok(views.html.mobile.index.render());
	}

}

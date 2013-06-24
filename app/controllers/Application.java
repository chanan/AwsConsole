package controllers;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
	public static Result index() {
		return ok(views.html.index.render());
	}
	
	public static Result router() throws Exception {
        return ok(Routes.javascriptRouter("routes",
                routes.javascript.Api.instances(),
                routes.javascript.Api.users(),
                routes.javascript.Api.changePowerSave(),
                routes.javascript.Api.startInstance(),
                routes.javascript.Api.stopInstance(),
                routes.javascript.Api.instance(),
                routes.javascript.Api.images(),
                routes.javascript.Api.keys(),
                routes.javascript.Api.securityGroups(),
                routes.javascript.Api.types(),
                routes.javascript.Api.createInstance()
        )).as("text/javascript");
    }
	
	public static Result healthCheck() {
		return ok("I am healthy!");
	}
}

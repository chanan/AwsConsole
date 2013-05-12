package controllers;

import play.api.Application;
import play.api.data.Form;
import play.api.mvc.Request;
import play.api.templates.Html;
import scala.Option;
import scala.Tuple2;
import securesocial.controllers.DefaultTemplatesPlugin;
import securesocial.controllers.Registration.RegistrationInfo;

public class CustomTemplatesPlugin extends DefaultTemplatesPlugin {

	public CustomTemplatesPlugin(Application application) {
		super(application);
	}

	@Override
	public <A> Html getLoginPage(Request<A> request, Form<Tuple2<String, String>> form, Option<String> msg) {
		return views.html.secure.login.render(request, form, msg);
	}

	@Override
	public <A> Html getSignUpPage(Request<A> request, Form<RegistrationInfo> form, String token) {
		return views.html.secure.signUp.render(request, form, token);
	}

	@Override
	public <A> Html getStartSignUpPage(Request<A> request, Form<String> form) {
		return views.html.secure.startSignUp.render(request, form);
	}

	@Override
	public <A> Html getResetPasswordPage(Request<A> request, Form<Tuple2<String, String>> form, String token) {
		return views.html.secure.resetPasswordPage.render(request, form, token);
	}

	@Override
	public <A> Html getStartResetPasswordPage(Request<A> request, Form<String> form) {
		return views.html.secure.startResetPassword.render(request, form);
	}
}

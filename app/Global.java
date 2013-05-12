import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Props;

import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import services.PowerManager;
import services.Setup;


public class Global extends GlobalSettings {

	@Override
	public void onStart(Application arg0) {
		final ActorRef myActor = Akka.system().actorOf(new Props(PowerManager.class));
		Akka.system().scheduler().schedule(
			Duration.create(30, TimeUnit.SECONDS),
			Duration.create(1, TimeUnit.MINUTES),
			myActor, 
			"tick",
			Akka.system().dispatcher()
		);
		final ActorRef setup = Akka.system().actorOf(new Props(Setup.class));
		Akka.system().scheduler().scheduleOnce(
			Duration.create(1, TimeUnit.SECONDS),
			new Runnable() {
			    public void run() {
			    	setup.tell("tick", null);
			    }
			}
			, 
			Akka.system().dispatcher());
	}

}

package services;

import java.util.List;

import models.LocalInstance;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

import com.typesafe.config.ConfigFactory;

import play.libs.Akka;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.actor.UntypedActor;

public class PowerManager extends UntypedActor {
	private final String timezone = ConfigFactory.load().getString("application.timezone");

	@Override
	public void onReceive(Object arg0) throws Exception {
		DateTimeZone zone = DateTimeZone.forID(timezone);
		DateTime now = DateTime.now(zone);
		if(now.getDayOfWeek() != DateTimeConstants.SATURDAY && now.getDayOfWeek() != DateTimeConstants.SUNDAY) {
			if(now.getHourOfDay() == 8 && now.getMinuteOfHour() < 6) {
				List<LocalInstance> instances = LocalInstance.find.where().eq("powerSave", true).findList();
				for(final LocalInstance instance : instances) {
					ActorRef myActor = Akka.system().actorOf(new Props(PowerOnWorker.class));
					myActor.tell(instance.instanceId, self());
				}
			}
			if(now.getHourOfDay() == 19 && now.getMinuteOfHour() < 6) {
				List<LocalInstance> instances = LocalInstance.find.where().eq("powerSave", true).findList();
				for(final LocalInstance instance : instances) {
					ActorRef myActor = Akka.system().actorOf(new Props(PowerOffWorker.class));
					myActor.tell(instance.instanceId, self());
				}
			}
		}
	}
	
	static class PowerOnWorker extends UntypedActor {
		private final Amazon amazon = TypedActor.get(Akka.system()).typedActorOf(new TypedProps<AmazonImpl>(Amazon.class, AmazonImpl.class));
		
		@Override
		public void onReceive(Object messgae) throws Exception {
			String instanceId = messgae.toString();
			amazon.startInstance(instanceId);
		}
	}
	
	static class PowerOffWorker extends UntypedActor {
		private final Amazon amazon = TypedActor.get(Akka.system()).typedActorOf(new TypedProps<AmazonImpl>(Amazon.class, AmazonImpl.class));
		
		@Override
		public void onReceive(Object messgae) throws Exception {
			String instanceId = messgae.toString();
			amazon.stopInstance(instanceId);
		}
	}
}

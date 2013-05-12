package services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.amazonaws.services.ec2.model.Tag;

public class AmazonUtils {
	
	public static String getTagValue(List<Tag> tags, String key) {
		String value = null;
		for (final Tag tag : tags) {
			if (tag.getKey().equalsIgnoreCase(key)) {
				value = tag.getValue();
			}
		}
		return value;
	}
	
	public static String getEstTime(Date dbTime) {
		SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm");
		DateTimeZone zone = DateTimeZone.forID("America/New_York");
		DateTime utcTime = new DateTime(dbTime);
		DateTime estTime = utcTime.toDateTime(zone);
		return df.format(estTime.toDate());
	}
}

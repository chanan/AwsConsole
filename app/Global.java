import play.Application;
import play.GlobalSettings;
import services.PowerManager;
import services.Setup;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application arg0) {
		PowerManager.init();
		Setup.init();
	}
}

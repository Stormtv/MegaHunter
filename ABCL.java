package scripts.MegaHunter;

import org.tribot.api.General;
import org.tribot.api.Timing;

public class ABCL {
	public void waitNewOrSwitchDelay(final long last_busy_time) {
		if (Timing.timeFromMark(last_busy_time) >= General.random(8000, 12000)) {
			General.sleep(MegaHunt.Status.getABCUtil().DELAY_TRACKER.NEW_OBJECT.next());
			MegaHunt.Status.getABCUtil().DELAY_TRACKER.NEW_OBJECT.reset();
		}
	}
}

package scripts.MegaHunter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.tribot.api2007.Objects;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;

import scripts.MegaHunter.Trap.trapState;

public class Painter {

	public static void paint(Graphics g) {
		if (MegaHunt.Status != null) {
			List<Trap> traps = new ArrayList<Trap>(MegaHunt.Status.getTraps());
			if (traps != null && traps.size() > 0) {
				for (Trap t : traps) {
					trapState state = t.getState();
					if (state.equals(trapState.ANIMATION)) {
						g.setColor(Color.ORANGE);
					} else if (state.equals(trapState.IDLE)) {
						g.setColor(Color.BLUE);
					} else if (state.equals(trapState.BROKEN)) {
						g.setColor(Color.RED);
					} else if (state.equals(trapState.CAPTURED)) {
						g.setColor(Color.GREEN);
					} else if (state.equals(trapState.NULL)) {
						g.setColor(Color.PINK);
					}
					for (RSObject o : Objects.getAt(t.getLocation())) {
						RSObjectDefinition def = o.getDefinition();
						if (def != null && def.getName().equals(t.getName())) {
							Point p = o.getModel().getCentrePoint();
							g.drawString(
									"Time: "
											+ (System.currentTimeMillis() - t
													.getTime()) / 1000, p.x,
									p.y);
						}
					}
					Polygon pt = Projection.getTileBoundsPoly(t.getLocation(),
							0);
					if (pt != null) {
						g.drawPolygon(pt);
					}
				}
			}
			g.setColor(Color.DARK_GRAY);
			Point[] p = MegaHunt.Status.coolPoints;
			if (p != null && p.length > 0) {
				for (Point p1 : p) {
					if (Projection.isInViewport(p1)) {
						g.fillRect(p1.x, p1.y, 1, 1);
					}
				}
			}
			g.drawString("MegaHunt v1.0", 277, 360);
			long runtime = (System.currentTimeMillis() - MegaHunt.Status
					.getStartTime());
			g.drawString("Time ran: " + format(runtime), 277, 380);
			int xpGained = Skills.getXP(Skills.SKILLS.HUNTER)
					- MegaHunt.Status.getStartXp();
			g.drawString("Xp Gained: " + xpGained, 277, 440);
			int caught = xpGained / MegaHunt.Status.getMethod().getXp();
			g.drawString("Animals Caught: " + caught, 277, 400);
			int caughtPerHour = (int) ((3600000.0 / (double) runtime) * caught);
			g.drawString("Caught/Hr: " + caughtPerHour, 277, 420);
			int xpPerHour = (int) ((3600000.0 / (double) runtime) * xpGained);
			g.drawString("Xp/Hr: " + xpPerHour, 277, 460);
		}
	}

	private static String format(final long time) {
		final StringBuilder t = new StringBuilder();
		final long total_secs = time / 1000;
		final long total_mins = total_secs / 60;
		final long total_hrs = total_mins / 60;
		final int secs = (int) total_secs % 60;
		final int mins = (int) total_mins % 60;
		final int hrs = (int) total_hrs % 60;
		if (hrs < 10) {
			t.append("0");
		}
		t.append(hrs);
		t.append(":");
		if (mins < 10) {
			t.append("0");
		}
		t.append(mins);
		t.append(":");
		if (secs < 10) {
			t.append("0");
		}
		t.append(secs);
		return t.toString();
	}
}
package scripts.MegaHunter;

import java.awt.Point;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSModel;

public class MouseMovementThread extends Thread {

	private RSModel model;
	private boolean clicked = false;
	private String action;
	private Positionable p = null;
	private boolean rightClick = false;

	public MouseMovementThread(RSModel model, String action, boolean rightClick) {
		this.model = model;
		this.action = action;
		this.p = null;
		this.rightClick = rightClick;
	}

	public MouseMovementThread(RSModel model, Positionable p, String action, boolean rightClick) {
		this.model = model;
		this.action = action;
		this.p = p;
		this.rightClick = rightClick;
	}

	public MouseMovementThread(Positionable p, String action, boolean rightClick) {
		this.model = null;
		this.action = action;
		this.p = p;
		this.rightClick = rightClick;
	}

	public boolean execute() {
		int initial_speed = Mouse.getSpeed();
		Mouse.setSpeed(initial_speed + 2);
		start();
		while (isAlive())
			General.sleep(100);
		Mouse.setSpeed(initial_speed);
		return clicked;
	}

	@Override
	public void run() {
		focus(0);
		return;
	}

	private boolean focus(int attempt) {
		Point[] visible = null;
		if (p != null) {
			if (model != null) {
				visible = Clicking.getResultantPoints(model, p);
			} else {
				visible =  Clicking.getPointsFromPolygon(Projection.getTileBoundsPoly(p, 0));
			}
		} else {
			visible = this.model.getVisiblePoints();
		}
		if (visible != null && visible.length > 0) {
			Point[] points = Clicking.standardDeviation(visible);
			MegaHunt.Status.coolPoints = points;
			if (points == null || points.length == 0)
				return false;
			Point click = points[General.random(0, points.length - 1)];
			if (canClick()) {
				click();
			} else {
				Mouse.move(click);
				for (int fSafe = 0; fSafe < 20
						&& !Game.getUptext().toLowerCase()
								.contains(action.toLowerCase()); fSafe++)
					General.sleep(10, 15);
				if (canClick())
					click();
				else {
					if (attempt > 10)
						return false;
					Mouse.setSpeed(Mouse.getSpeed() + 2);
					focus(++attempt);
				}
			}
			MegaHunt.Status.coolPoints = null;
			return clicked;
		} else {
			General.println("no visible points");
			return false;
		}
	}

	private void click() {
		if (p != null) {
			if (ChooseOption.isOpen()) ChooseOption.close();
			if (Game.getUptext().toLowerCase()
					.contains(action.toLowerCase())
					&& Projection.getTileBoundsPoly(p, 0).contains(
							Mouse.getPos())) {
				General.sleep(50, 150);
				if (Game.getUptext().toLowerCase()
						.contains(action.toLowerCase())
						&& Projection.getTileBoundsPoly(p, 0).contains(
								Mouse.getPos())) {
					Mouse.click(1);
					if (Timing.waitCrosshair(General.random(600, 800)) == 1) {
						this.clicked = true;
					}
					return;
				}
			}
		} else {
			if (ChooseOption.isOpen()) ChooseOption.close();
			if (Game.getUptext().toLowerCase()
					.contains(action.toLowerCase())) {
				General.sleep(50, 150);
				if (Game.getUptext().toLowerCase()
						.contains(action.toLowerCase())) {
					Mouse.click(1);
					if (Timing.waitCrosshair(General.random(600, 800)) == 2) {
						this.clicked = true;
					}
					return;
				}
			}
		}
		if (this.rightClick) {
			if (p != null) {
				if (Projection.getTileBoundsPoly(p, 0).contains(Mouse.getPos())) {
					Mouse.click(3);
					if (ChooseOption.isOptionValid(action)) {
						this.clicked = ChooseOption.select(action);
					} else {
						ChooseOption.close();
					}
				}
			} else {
				if (model.getEnclosedArea().contains(Mouse.getPos())) {
					Mouse.click(3);
					if (ChooseOption.isOptionValid(action)) {
						this.clicked = ChooseOption.select(action);
					} else {
						ChooseOption.close();
					}
				}
			}
		}
	}

	private boolean canClick() {
		if (p != null) {
			if (Game.getUptext().toLowerCase().contains(action.toLowerCase())
					&& Projection.getTileBoundsPoly(p, 0).contains(
							Mouse.getPos())) {
				return true;
			}
		} else if (model != null) {
			if (Game.getUptext().toLowerCase().contains(action.toLowerCase())
					&& model.getEnclosedArea().contains(Mouse.getPos())) {
				return true;
			}
		}
		return false;
	}
}

package scripts.MegaHunter;

import java.awt.Point;
import java.awt.Rectangle;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;

public class Trap implements Comparable<Trap> {
	enum trapState {
		CAPTURED, BROKEN, IDLE, NULL, ANIMATION
	}

	private long time;
	private RSTile location;
	private String name;
	private trapState curState;

	public Trap(long time, RSTile location, String name) {
		this.time = time;
		this.location = location;
		this.name = name;
		this.curState = trapState.NULL;
	}

	public trapState getCurState() {
		return curState;
	}

	public void setCurState(trapState curState) {
		this.curState = curState;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public RSTile getLocation() {
		return location;
	}

	public void setLocation(RSTile location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RSModel getModel() {
		for (RSObject o : Objects.getAt(this.getLocation())) {
			if (o != null) {
				RSObjectDefinition def = o.getDefinition();
				if (def != null && def.getName().equals(this.getName())) {
					return o.getModel();
				}
			}
		}
		return null;
	}

	public trapState getState() {
		for (RSObject o : Objects.getAt(this.getLocation())) {
			if (o != null) {
				RSObjectDefinition def = o.getDefinition();
				if (def != null && def.getName().equals(this.getName())) {
					String[] actions = def.getActions();
					if (actions != null && actions.length > 0) {
						if (actions[0].equals("Check")) {
							if (!this.getCurState().equals(trapState.CAPTURED)) {
								this.setCurState(trapState.CAPTURED);
								this.setTime(System.currentTimeMillis());
							}
							return trapState.CAPTURED;
						} else if (actions[0].equals("Dismantle")) {
							if (actions.length > 1) {
								if (actions[1].equals("Investigate")) {
									if (!this.getCurState().equals(
											trapState.IDLE)) {
										this.setCurState(trapState.IDLE);
										this.setTime(System.currentTimeMillis());
									}
									return trapState.IDLE;
								}
							}
							if (!this.getCurState().equals(trapState.BROKEN)) {
								this.setCurState(trapState.BROKEN);
								this.setTime(System.currentTimeMillis());
							}
							return trapState.BROKEN;
						}
					} else {
						if (!this.getCurState().equals(trapState.ANIMATION)) {
							this.setCurState(trapState.ANIMATION);
							this.setTime(System.currentTimeMillis());
						}
						return trapState.ANIMATION;
					}
				}
			}
		}
		if (!this.getCurState().equals(trapState.NULL)) {
			this.setCurState(trapState.NULL);
			this.setTime(System.currentTimeMillis());
		}
		return trapState.NULL;
	}

	public void set() {
		if (isValidTrapLocation(this.getLocation())) {
			RSGroundItem[] g = GroundItems.getAt(this.getLocation());
			if (g.length > 0) {
				for (RSGroundItem item : g) {
					RSItemDefinition def = item.getDefinition();
					if (def != null && def.getName().equals(this.getName())) {
						if (item.click("Lay " + this.getName())) {
							// Wait until moving
							for (int a = 0; a < 20
									&& !Player.isMoving()
									&& !Player.getPosition().equals(
											this.getLocation())
									&& !this.getState().equals(trapState.IDLE); a++) {
								General.sleep(25, 50);
							}
							if (Player.isMoving()) {
								for (int a = 0; a < 20
										&& Player.isMoving()
										&& !Player.getPosition().equals(
												this.getLocation())
										&& !this.getState().equals(
												trapState.IDLE); a++) {
									General.sleep(25, 50);
								}
							}
							for (int a = 0; a < 20
									&& !this.getState().equals(trapState.IDLE); a++) {
								General.sleep(100, 200);
							}
							if (this.getState().equals(trapState.IDLE)) {
								this.setTime(System.currentTimeMillis());
							}
							break;
						}
					}
				}
			}
			if (this.getState().equals(trapState.NULL)) {
				if (!Player.getPosition().equals(this.getLocation())) {
					walkToTrap();
				}
				if (Player.getPosition().equals(this.getLocation())
						|| (Player.isMoving() && Player.getPosition().distanceToDouble(
								this.getLocation()) <= 1)) {
					if (!this.getState().equals(trapState.IDLE)) {
						RSItem[] i = Inventory.find(this.getName());
						if (i.length > 0) {
							if (i[0].click("Lay " + this.getName())) {
								for (int a = 0; a < 20
										&& !this.getState().equals(
												trapState.IDLE); a++) {
									General.sleep(100, 200);
								}
								if (this.getState().equals(trapState.IDLE)) {
									this.setTime(System.currentTimeMillis());
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isValidTrapLocation(RSTile t) {
		int blockerIds[] = { 2732, 4845, 19840, 4818, 19839, 4825, 4820, 19839,
				4823, 2986, 1202 };
		for (RSObject o : Objects.getAt(t)) {
			for (int i : blockerIds) {
				if (i == o.getID()) {
					return false;
				}
			}
		}
		return true;
	}

	private void walkToTrap() {
		if (Clicking.clickTileNotObject(this.getModel(), this.getLocation(),
				"Walk here", false)) {
			// Wait until moving
			for (int a = 0; a < 20 && !Player.isMoving()
					&& !Player.getPosition().equals(this.getLocation()); a++) {
				General.sleep(25, 50);
			}
			General.println("Should be moving now");
			if (Player.isMoving() && Player.getPosition().distanceToDouble(this.getLocation()) > 1) {
				General.println("moving now");
				for (int a = 0; a < 20 && Player.isMoving()
						&& Player.getPosition().distanceToDouble(this.getLocation()) > 1; a++) {
					General.sleep(25, 50);
				}
			}
		}
	}

	public void click() {
		if (!Player.getPosition().equals(this.getLocation())) {
			walkToTrap();
		}
		if (Player.getPosition().equals(this.getLocation())
				|| (Player.isMoving() && Player.getPosition().distanceToDouble(
						this.getLocation()) <= 1)) {
			for (RSObject o : Objects.getAt(this.getLocation())) {
				RSObjectDefinition def = o.getDefinition();
				if (def != null && def.getName().equals(this.getName())) {
					if (this.getState().equals(trapState.CAPTURED)) {
						if (Clicking.click(o, "Check " + this.getName(), false)) {
							boolean hovering = false;
							for (int a = 0; a < 20
									&& this.getState().equals(
											trapState.CAPTURED); a++) {
								if (!hovering
										&& Player.getAnimation() == 5207
										&& Player.getPosition().equals(
												this.getLocation())) {
									Mouse.move(getFirstEmptySlot());
									hovering = true;
								}
								General.sleep(75, 175);
							}
						}
					} else if (this.getState().equals(trapState.BROKEN)) {
						if (Clicking.click(o, "Dismantle " + this.getName(),
								false)) {
							boolean hovering = false;
							for (int a = 0; a < 20
									&& this.getState().equals(trapState.BROKEN); a++) {
								if (!hovering
										&& Player.getAnimation() == 5207
										&& Player.getPosition().equals(
												this.getLocation())) {
									Mouse.move(getFirstEmptySlot());
									hovering = true;
								}
								General.sleep(75, 175);
							}
						}
					}
				}
			}
		}
	}

	private Point getFirstEmptySlot() {
		int firstEmptySlotIndex = getFirstEmptySlotIndex();

		if (firstEmptySlotIndex == -1)
			return null;

		RSItem r = new RSItem(firstEmptySlotIndex, -1, 1, RSItem.TYPE.INVENTORY);
		Rectangle rec = r.getArea();

		return new Point(General.random((int) rec.getMinX(),
				(int) rec.getMaxX()), General.random((int) rec.getMinY(),
				(int) rec.getMaxY()));
	}

	private int getFirstEmptySlotIndex() {
		RSItem[] items = Inventory.getAll();
		int counter = 0;
		int firstEmptySlotIndex = -1;
		for (RSItem item : items) {
			if (item.getIndex() == counter) {
				counter++;
			} else {
				firstEmptySlotIndex = counter;
				break;
			}
		}

		return firstEmptySlotIndex;
	}

	@Override
	public int compareTo(Trap o) {
		RSTile myTile = Player.getPosition();
		double diff = this.getLocation().distanceToDouble(myTile) - o
				.getLocation().distanceToDouble(myTile);
		if (diff > 0) return 1;
		if (diff < 0) return -1;
		return 0;
	}
}

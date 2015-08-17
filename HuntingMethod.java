package scripts.MegaHunter;

import org.tribot.api.General;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSItem;

import scripts.MegaHunter.utils.RSBuddyItem;

public class HuntingMethod {
	private final int trapId;
	private final RSBuddyItem product;
	private final int xp;
	private final String trapName;
	
	HuntingMethod(final int trapId, final int xp, final String trapName, int productId) {
		this.trapId = trapId;
		this.xp = xp;
		this.trapName = trapName;
		this.product = new RSBuddyItem(productId);
	}

	public String getTrapName() {
		return trapName;
	}

	public int getTrapId() {
		return trapId;
	}

	public int getXp() {
		return xp;
	}
	
	public RSBuddyItem getProduct() {
		return product;
	}
	
	public boolean isTrapsSet() {
		int trapsNeeded = 0;
		if (Combat.getWildernessLevel() > 0) {
			trapsNeeded++;
		}
		trapsNeeded += (1+Skills
				.getActualLevel(Skills.SKILLS.HUNTER) / 20);
		int trapsSet = 0;
		for (Trap t : MegaHunt.Status.getTraps()) {
			if (!t.getState().equals(Trap.trapState.NULL)) trapsSet++;
		}
		return trapsSet == trapsNeeded;
	}

	public boolean isSuccessfulTrap() {
		if (MegaHunt.Status.getTraps().size() == 0) {
			return false;
		}
		for (Trap t: MegaHunt.Status.getTraps()){
			if (t.getState().equals(Trap.trapState.CAPTURED)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUnsuccessfulTrap() {
		if (MegaHunt.Status.getTraps().size() == 0) {
			return false;
		}
		for (Trap t: MegaHunt.Status.getTraps()){
			if (t.getState().equals(Trap.trapState.BROKEN)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTrapTimed() {
		if (MegaHunt.Status.getTraps().size() == 0) {
			return false;
		}
		for (Trap t: MegaHunt.Status.getTraps()){
			if (t.getTime()-System.currentTimeMillis() >= 40000) {
				return true;
			}
		}
		return false;
	}

	public void checkTrap(Trap.trapState state) {
		Trap tar = null;
		for (Trap t : MegaHunt.Status.getTraps()) {
			if (t.getState().equals(state)) {
				if (tar == null || tar.getTime() > t.getTime()) {
					tar = t;
				}
			}
		}
		if (tar != null) {
			tar.click();
		}
	}

	public void setTraps() {
		for (Trap t : MegaHunt.Status.getClosestTraps()) {
			if (t.getState().equals(Trap.trapState.NULL)) {
				t.set();
				break;
			}
		}
	}

	public void cleanInventory() {
		RSItem[] bones = Inventory.find(526);
		if (bones.length > 0) {
			for (RSItem bone : bones) {
				int Count = Inventory.getCount(526);
				bone.click("Bury");
				for (int fsafe = 0; Inventory.getCount(526) == Count
						&& fsafe < 30; fsafe++) {
					General.sleep(50, 75);
				}
				// Delay Between Item Interactions
				General.sleep(MegaHunt.Status.getABCUtil().DELAY_TRACKER.ITEM_INTERACTION
						.next());
			}
		}
		if (Inventory.drop(MegaHunt.Status.getTrash()) > 0) {
			General.sleep(MegaHunt.Status.getABCUtil().DELAY_TRACKER.ITEM_INTERACTION
					.next());
		}
	}

	public boolean InventoryFilled() {
		if (this.getTrapName().equals("Bird snare")) {
			return (Inventory.getAll().length >= 27);
		} else if (this.getTrapName().equals("Box trap")) {
			return (Inventory.getAll().length == 28);
		}
		return false;
	}

	public boolean isValidStandingTile() {
		for (Trap t : MegaHunt.Status.getTraps()) {
			if (t.getLocation().equals(Player.getPosition())) {
				return false;
			}
		}
		return true;
	}
}


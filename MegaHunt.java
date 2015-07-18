package scripts.MegaHunter;

import java.awt.Graphics;

import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.Skills;
import org.tribot.script.Script;
import org.tribot.script.interfaces.Painting;

public class MegaHunt extends Script implements Painting {
	public static ScriptStatus Status;
	@Override
	public void run() {
		setup();
		loop();
	}
	
	private void loop() {
		HuntingMethod Strategy = Status.getMethod();
		while (Status.isRunning()) {
			Status.getABCUtil().performTimedActions(Skills.SKILLS.HUNTER);
			if (Strategy.InventoryFilled())
				Strategy.cleanInventory();
			else if (!Strategy.isTrapsSet())
				Strategy.setTraps();
			else if (Strategy.isSuccessfulTrap())
				Strategy.checkTrap(Trap.trapState.CAPTURED);
			else if (Strategy.isUnsuccessfulTrap() || Strategy.isTrapTimed())
				Strategy.checkTrap(Trap.trapState.BROKEN);
			else if (Strategy.isValidStandingTile())
				Strategy.cleanInventory();
		}
	}

	private void setup() {
		while(!Login.getLoginState().equals(Login.STATE.INGAME)) {
			println("Please Login to runescape to continue");
			General.sleep(1000,5000);
		}
		//TODO Set script status to accept parameters of gui options
		Status = new ScriptStatus();
		println("Status Set");
		println("isRunning " + Status.isRunning());
	}

	@Override
	public void onPaint(Graphics g) {
		Painter.paint(g);
	}	
}

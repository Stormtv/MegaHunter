package scripts.MegaHunter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSTile;

public class ScriptStatus {

	private boolean running;
	private ABCUtil ABCUtil;
	private HuntingMethod method;
	private List<Trap> traps;
	private int[] trash;
	private int startXp;
	private long startTime;
	private List<RSTile> preferedTiles;
	public Point[] coolPoints;
	public ScriptStatus() {
		this.setRunning(true);
		this.setABCUtil(new ABCUtil());
		this.method = new HuntingMethod(10006,95,"Bird snare");
		this.traps = new ArrayList<Trap>();
		this.trash = new int[] { 1917, 1454, 1969, 1973, 1971, 2327, 6961,
				6963, 6965, 6962, 464, 9003, 117, 1623, 1619, 1621, 1617, 9978,
				10115, 10125, 10127, 229, 592 };
		//TODO Improve this
		this.startTime = System.currentTimeMillis();
		this.startXp = Skills.getXP(Skills.SKILLS.HUNTER);
		this.setPreferedTiles(new ArrayList<RSTile>());
		RSTile home = Player.getPosition();
		addPreferedTile(new RSTile(home.getX()+1, home.getY()+1));
		addPreferedTile(new RSTile(home.getX()-1, home.getY()-1));
		addPreferedTile(new RSTile(home.getX()-1, home.getY()+1));
		addPreferedTile(new RSTile(home.getX()+1, home.getY()-1));

		for (RSTile t : this.preferedTiles) {
			this.addTraps(new Trap(0, t, "Bird snare"));
		}
	}

	public int getStartXp() {
		return startXp;
	}

	public void setStartXp(int startXp) {
		this.startXp = startXp;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public List<Trap> getTraps() {
		return traps;
	}
	
	public List<Trap> getClosestTraps() {
		Collections.sort(traps);
		return traps;
	}

	public void setTraps(List<Trap> traps) {
		this.traps = traps;
	}
	
	public void addTraps(Trap trap) {
		this.traps.add(trap);
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public ABCUtil getABCUtil() {
		return ABCUtil;
	}

	public void setABCUtil(ABCUtil aBCUtil) {
		ABCUtil = aBCUtil;
	}

	public HuntingMethod getMethod() {
		return method;
	}

	public void setMethod(HuntingMethod HuntingMethod) {
		method = HuntingMethod;
	}

	public int[] getTrash() {
		return trash;
	}

	public void setTrash(int[] trash) {
		this.trash = trash;
	}

	public List<RSTile> getPreferedTiles() {
		return preferedTiles;
	}

	public void setPreferedTiles(List<RSTile> preferedTiles) {
		this.preferedTiles = preferedTiles;
	}
	
	public void addPreferedTile(RSTile t) {
		this.preferedTiles.add(t);
	}

}

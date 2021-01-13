/**
 * Created by Aleksey on 11.07.2017.
 */

package isaac.bastion;

import vg.civcraft.mc.namelayer.core.DefaultPermissionLevel;
import vg.civcraft.mc.namelayer.core.PermissionTracker;
import vg.civcraft.mc.namelayer.core.PermissionType;
import vg.civcraft.mc.namelayer.mc.GroupAPI;

public class BastionPermissionManager {
	
	private static final String BASTION_PEARL = "BASTION_PEARL";
	private static final String BASTION_PLACE = "BASTION_PLACE";
	private static final String BASTION_LIST = "BASTION_LIST";
	
	private PermissionTracker permTracker;
	
	public BastionPermissionManager(PermissionTracker permTracker) {
		this.permTracker = permTracker;
		setup();
	}
	
	private static void setup() {
		GroupAPI.registerPermission(BASTION_PEARL, DefaultPermissionLevel.MEMBER, "Allows a player to throw a pearl into a bastion field.");
		GroupAPI.registerPermission(BASTION_PLACE, DefaultPermissionLevel.MOD, "Allows a player to place blocks within a bastion field.");
		GroupAPI.registerPermission(BASTION_LIST, DefaultPermissionLevel.MOD, "Allows a player to see all bastions under this group.");
	}
	
	public PermissionType getPearlInBastion() {
		return permTracker.getPermission(BASTION_PEARL);
	}
	
	public PermissionType getPlaceInBastion() {
		return permTracker.getPermission(BASTION_PLACE);
	}
	
	public PermissionType getListBastion() {
		return permTracker.getPermission(BASTION_LIST);
	}
}

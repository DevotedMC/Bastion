package isaac.bastion;

import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import isaac.bastion.commands.BastionCommandManager;
import isaac.bastion.commands.ModeChangeCommand;
import isaac.bastion.commands.PlayersStates.Mode;
import isaac.bastion.listeners.BastionBreakListener;
import isaac.bastion.listeners.BastionDamageListener;
import isaac.bastion.listeners.BastionInteractListener;
import isaac.bastion.listeners.CitadelListener;
import isaac.bastion.listeners.ElytraListener;
import isaac.bastion.listeners.ModeListener;
import isaac.bastion.manager.BastionBlockManager;
import isaac.bastion.storage.BastionBlockStorage;
import isaac.bastion.storage.Database;
import isaac.bastion.utils.BastionSettingManager;
import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.dao.ManagedDatasource;
import vg.civcraft.mc.namelayer.mc.NameLayerPlugin;

public final class Bastion extends ACivMod {
	
	private static Bastion plugin;
	private static BastionBlockStorage blockStorage;
	private static BastionBlockManager blockManager;
	private static BastionSettingManager settingManager;
	private static CommonSettings commonSettings;
	private BastionPermissionManager permissionManager;

	@Override
	public void onEnable() 	{
		super.onEnable();
		plugin = this;
		saveDefaultConfig();
		reloadConfig();
		BastionType.loadBastionTypes(getConfig().getConfigurationSection("bastions"));
		commonSettings = CommonSettings.load(getConfig().getConfigurationSection("commonSettings"));
		setupDatabase();
		permissionManager = new BastionPermissionManager(NameLayerPlugin.getInstance().getGroupTracker().getPermissionTracker());
		blockManager = new BastionBlockManager();
		settingManager = new BastionSettingManager();
		
		if(!this.isEnabled()) //check that the plugin was not disabled in setting up any of the static variables
			return;
		
		BastionType.startRegenAndErosionTasks();
		registerListeners();
		setupCommands();
	}
	
	@Override
	public void onDisable() {
		blockStorage.close();
	}
	
	private void registerListeners() {
		getLogger().log(Level.INFO, "Registering Listeners");
		getServer().getPluginManager().registerEvents(new BastionDamageListener(), this);
		getServer().getPluginManager().registerEvents(new BastionInteractListener(), this);
		getServer().getPluginManager().registerEvents(new ElytraListener(), this);
		getServer().getPluginManager().registerEvents(new BastionBreakListener(blockStorage, blockManager), this);
		getServer().getPluginManager().registerEvents(new CitadelListener(), this);
		getServer().getPluginManager().registerEvents(new ModeListener(), this);
	}

	private void setupDatabase() {
		ConfigurationSection config = getConfig().getConfigurationSection("mysql");
		String host = config.getString("host");
		int port = config.getInt("port");
		String user = config.getString("user");
		String pass = config.getString("password");
		String dbname = config.getString("database");
		int poolsize = config.getInt("poolsize");
		long connectionTimeout = config.getLong("connectionTimeout");
		long idleTimeout = config.getLong("idleTimeout");
		long maxLifetime = config.getLong("maxLifetime");
		ManagedDatasource db = null;
		try {
			db = new ManagedDatasource(this, user, pass, host, port, dbname, poolsize, connectionTimeout, idleTimeout, maxLifetime);
			db.getConnection().close();
		} catch(Exception e) {
			warning("Could not connect to database, stopping bastion", e);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Database.registerMigrations(db);
		if(!db.updateDatabase()) {
			warning("Failed to update database, stopping bastion");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		blockStorage = new BastionBlockStorage(db, getLogger());
		blockStorage.loadBastions();
		getLogger().log(Level.INFO, "All Bastions loaded");
	}
	
	//Sets up the command managers
	private void setupCommands(){
		getCommand("Bastion").setExecutor(new BastionCommandManager());
		getCommand("bsi").setExecutor(new ModeChangeCommand(Mode.INFO));
		getCommand("bsd").setExecutor(new ModeChangeCommand(Mode.DELETE));
		getCommand("bso").setExecutor(new ModeChangeCommand(Mode.NORMAL));
		getCommand("bsb").setExecutor(new ModeChangeCommand(Mode.BASTION));
		getCommand("bsf").setExecutor(new ModeChangeCommand(Mode.OFF));
		getCommand("bsm").setExecutor(new ModeChangeCommand(Mode.MATURE));
	}

	public static Bastion getInstance() {
		return plugin;
	}
	
	public static BastionBlockManager getBastionManager() {
		return blockManager;
	}
	
	public static BastionBlockStorage getBastionStorage() {
		return blockStorage;
	}

	public static BastionSettingManager getSettingManager() {
		return settingManager;
	}
	
	public BastionPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public static CommonSettings getCommonSettings() { return commonSettings; }

}

package isaac.bastion;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

public class BastionType {
	
	private static HashMap<String, BastionType> types = new HashMap<String, BastionType>();
	private static int maxRadius;

	private String name;
	private MaterialData material;
	private String lore;
	private boolean square;
	private int effectRadius;
	private int radiusSquared;
	private boolean includeY;
	private int startScaleFactor;
	private double finalScaleFactor;
	private long warmupTime;
	private int erosionPerDay;
	private long placementCooldown;
	private boolean destroyOnRemove;
	private boolean blockPearls;
	private boolean blockMidair;
	private int scaleFactor;
	private boolean requireMaturity;
	private boolean consumeOnBlock;
	private int blocksToErode;
	
	private BastionType(String name, MaterialData material, String lore, boolean square, boolean includeY, int effectRadius,
			int startScaleFactor, double finalScaleFactor, long warmupTime, int erosionPerDay, long placementCooldown,
			boolean destroyOnRemove, boolean blockPearls, boolean blockMidair, int scaleFactor, 
			boolean requireMaturity, boolean consumeOnBlock, int blocksToErode) {
		this.name = name;
		this.material = material;
		this.lore = lore != null ? lore : "";
		if(lore.length() > 42) {
			lore = lore.substring(0, 42);
		}
		this.square = square;
		this.includeY = includeY;
		this.effectRadius = effectRadius;
		this.radiusSquared = effectRadius * effectRadius;
		this.startScaleFactor = startScaleFactor;
		this.finalScaleFactor = finalScaleFactor;
		this.warmupTime = warmupTime;
		this.erosionPerDay = erosionPerDay;
		this.placementCooldown = placementCooldown;
		this.destroyOnRemove = destroyOnRemove;
		this.blockPearls = blockPearls;
		this.blockMidair = blockMidair;
		this.scaleFactor = scaleFactor;
		this.requireMaturity = requireMaturity;
		this.consumeOnBlock = consumeOnBlock;
		this.blocksToErode = blocksToErode;
		maxRadius = effectRadius > maxRadius ? effectRadius : maxRadius;
	}
	
	public MaterialData getMaterial() {
		return material;
	}

	public String getLore() {
		return lore;
	}

	public boolean isSquare() {
		return square;
	}

	public int getEffectRadius() {
		return effectRadius;
	}

	public int getRadiusSquared() {
		return radiusSquared;
	}
	
	public boolean isIncludeY() {
		return includeY;
	}

	public int getStartScaleFactor() {
		return startScaleFactor;
	}

	public double getFinalScaleFactor() {
		return finalScaleFactor;
	}

	public long getWarmupTime() {
		return warmupTime;
	}
	
	public int getErosionPerDay() {
		return erosionPerDay;
	}

	public boolean isDestroyOnRemove() {
		return destroyOnRemove;
	}

	public boolean isBlockPearls() {
		return blockPearls;
	}

	public boolean isBlockMidair() {
		return blockMidair;
	}

	public int getPearlScaleFactor() {
		return scaleFactor;
	}

	public boolean isRequireMaturity() {
		return requireMaturity;
	}

	public boolean isConsumeOnBlock() {
		return consumeOnBlock;
	}
	
	public int getBlocksToErode() {
		return blocksToErode;
	}
	
	public long getPlacementCooldown() {
		return placementCooldown;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof BastionType)) return false;
		BastionType other = (BastionType) obj;
		return other.equals(name);
	}

	public static int getMaxRadius() {
		return maxRadius;
	}

	public static void loadBastionTypes(ConfigurationSection config) {
		for(String key : config.getKeys(false)) {
			types.put(key, getBastionType(config.getConfigurationSection(key)));
		}
	}
	
	public static BastionType getBastionType(String name) {
		return types.get(name);
	}
	
	public static BastionType getBastionType(MaterialData mat, String lore) {
		if(lore == null) lore = "";
		for(BastionType type : types.values()) {
			if(type.material.equals(mat) && type.lore.equals(lore)) return type;
		}
		return null;
	}
	
	public static BastionType getBastionType(ConfigurationSection config) {
		String name = config.getName();
		Material mat = Material.getMaterial(config.getString("block.material"));
		byte data = config.contains("block.durability") ? (byte)config.getInt("block.durability") : 0;
		MaterialData material = new MaterialData(mat, data);
		String lore = config.getString("block.lore");
		boolean square = config.getBoolean("squarefield");
		int effectRadius = config.getInt("effectRadius");
		boolean includeY = config.getBoolean("includeY");
		int startScaleFactor = config.getInt("startScaleFactor");
		double finalScaleFactor = config.getDouble("finalScaleFactor");
		long warmupTime = config.getLong("warmupTime");
		int erosionPerDay = config.getInt("erosionPerDay");
		if(erosionPerDay != 0) {
			erosionPerDay = 1728000 / erosionPerDay;
		}
		long placementCooldown = config.getLong("placementCooldown");
		boolean destroyOnRemove = config.getBoolean("destroyOnRemove");
		boolean blockPearls = config.getBoolean("blockPearls");
		boolean blockMidair = config.getBoolean("blockMidair");
		int scaleFactor = config.getInt("pearlScaleFactor");
		boolean requireMaturity = config.getBoolean("requireMaturity");
		boolean consumeOnBlock = config.getBoolean("consumeOnBlock");
		int blocksToErode = config.getInt("blocksToErode");
		return new BastionType(name, material, lore, square, includeY, effectRadius, startScaleFactor, finalScaleFactor, warmupTime,
				erosionPerDay, placementCooldown, destroyOnRemove, blockPearls, blockMidair, scaleFactor, requireMaturity, consumeOnBlock, blocksToErode);
	}
}

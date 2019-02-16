package party.lemons.bettercompass.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import static net.minecraftforge.common.ForgeConfigSpec.*;

/**
 * Created by Sam on 27/02/2018.
 */
public class Config
{
	public static final ForgeConfigSpec CONFIG_SPEC;
	private static final Config CONFIG;

	public static BooleanValue  showCustomLocationText;
	public static BooleanValue showLocationInfoText;
	public static BooleanValue  allowCompassInAllDimensions;
	public static BooleanValue useHomingCompassInstead;


	static
	{
		Pair<Config,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	Config(ForgeConfigSpec.Builder builder)
	{
		builder.push("general");
		showCustomLocationText = builder.define("showCustomLocationText", true);
		showLocationInfoText = builder.define("showLocationInfoText", false);
		allowCompassInAllDimensions = builder.define("allowCompassInAllDimensions", false);
		useHomingCompassInstead = builder.define("useHomingCompassInstead", false);
		builder.pop();
	}
}

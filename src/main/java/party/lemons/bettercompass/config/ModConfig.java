package party.lemons.bettercompass.config;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import party.lemons.bettercompass.BetterCompass;

/**
 * Created by Sam on 27/02/2018.
 */
public class ModConfig
{
	public static boolean showCustomLocationText = true;
	public static boolean showLocationInfoText = true;
	public static boolean allowCompassInAllDimensions = false;
	public static CompassSetting compassActivateType = CompassSetting.RIGHT_CLICK;

	public static boolean useHomingCompassInstead = false;

	public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(BetterCompass.MODID))
		{
		//	ConfigManager.sync(BetterCompass.MODID, Config.Type.INSTANCE);
		}
	}
}

package party.lemons.bettercompass.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import party.lemons.bettercompass.BetterCompass;

/**
 * Created by Sam on 27/02/2018.
 */
@Config(modid = BetterCompass.MODID)
@Mod.EventBusSubscriber
public class ModConfig
{
	public static boolean showCustomLocationText = true;
	public static boolean showLocationInfoText = false;
	public static boolean allowCompassInAllDimensions = false;
	public static CompassSetting compassActivateType = CompassSetting.RIGHT_CLICK;

	@SubscribeEvent
	public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(BetterCompass.MODID))
		{
			ConfigManager.sync(BetterCompass.MODID, Config.Type.INSTANCE);
		}
	}
}

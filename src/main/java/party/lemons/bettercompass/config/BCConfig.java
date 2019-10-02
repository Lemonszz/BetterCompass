package party.lemons.bettercompass.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import party.lemons.bettercompass.BetterCompass;

/**
 * Created by Sam on 27/02/2018.
 */
@Mod.EventBusSubscriber(modid= BetterCompass.MODID)
public class BCConfig
{
	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static
	{
		final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

	static
	{
		final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	public static boolean showCustomLocationText = true;
	public static boolean showLocationInfoText = false;
	public static boolean allowCompassInAllDimensions = false;
	public static CompassSetting compassActivateType = CompassSetting.RIGHT_CLICK;

	public static class ClientConfig
	{
		public final ForgeConfigSpec.BooleanValue showCustomLocationText;
		public final ForgeConfigSpec.BooleanValue showLocationInfoText;

		ClientConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("Client");
			showCustomLocationText = builder.define("showCustomLocationText", true);
			showLocationInfoText = builder.define("showLocationInfoText", false);

			builder.pop();
		}
	}

	public static class CommonConfig
	{
		public final ForgeConfigSpec.BooleanValue allowCompassInAllDimensions;
	//	public final ForgeConfigSpec.EnumValue<CompassSetting> compassActivateType;

		CommonConfig(ForgeConfigSpec.Builder builder)
		{
			builder.push("Common");
			allowCompassInAllDimensions = builder.define("allowCompassInAllDimensions", false);
		//	compassActivateType = (ForgeConfigSpec.EnumValue<CompassSetting>) builder.define("compassActivateType", CompassSetting.RIGHT_CLICK);

			builder.pop();
		}
	}

	public static void refreshClient()
	{
		showCustomLocationText = CLIENT.showCustomLocationText.get();
		showLocationInfoText = CLIENT.showLocationInfoText.get();
	}

	public static void refreshServer()
	{
		allowCompassInAllDimensions = COMMON.allowCompassInAllDimensions.get();
	//	compassActivateType = COMMON.compassActivateType.get();
	}

	@SubscribeEvent
	public void modConfig(ModConfig.ModConfigEvent event)
	{
		ModConfig config = event.getConfig();
		if (config.getSpec() == CLIENT_SPEC)
			refreshClient();
		else if (config.getSpec() == COMMON_SPEC)
			refreshServer();
	}
}

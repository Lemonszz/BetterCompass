package party.lemons.bettercompass;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import party.lemons.bettercompass.config.BCConfig;

/**
 * Created by Sam on 3/02/2018.
 */
@Mod(BetterCompass.MODID)
public class BetterCompass
{
	public static final String MODID = "bettercompass";
	public BetterCompass()
	{
		ModLoadingContext modLoadingContext = ModLoadingContext.get();

		modLoadingContext.registerConfig(ModConfig.Type.COMMON, BCConfig.COMMON_SPEC);
		modLoadingContext.registerConfig(ModConfig.Type.CLIENT, BCConfig.CLIENT_SPEC);
	}
}
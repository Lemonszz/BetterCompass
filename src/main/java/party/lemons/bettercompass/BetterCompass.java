package party.lemons.bettercompass;

import party.lemons.bettercompass.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Sam on 3/02/2018.
 */
@Mod(modid=BetterCompass.MODID, name=BetterCompass.MODNAME, version=BetterCompass.VERSION)
public class BetterCompass
{
	public static final String MODID = "bettercompass";
	public static final String MODNAME = "Better Compass";
	public static final String VERSION = "1.1.2";

	@Mod.Instance(MODID)
	public static BetterCompass instance;

	@SidedProxy(clientSide = "party.lemons.bettercompass.proxy.ClientProxy", serverSide = "party.lemons.bettercompass.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}
}
package party.lemons.bettercompass.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Sam on 3/02/2018.
 */
@Mod.EventBusSubscriber
public class ModItems
{
	public static Item compass = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		compass = new ItemCustomCompass();
		event.getRegistry().register(compass);
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event)
	{
		registerRender(compass);
	}

	@SideOnly(Side.CLIENT)
	protected static void registerRender(Item item)
	{
		ModelResourceLocation loc = new ModelResourceLocation( item.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(item, 0, loc);
	}
}

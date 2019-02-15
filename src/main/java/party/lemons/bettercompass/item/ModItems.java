package party.lemons.bettercompass.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import party.lemons.bettercompass.BetterCompass;

/**
 * Created by Sam on 3/02/2018.
 */
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = BetterCompass.MODID)
public class ModItems
{
	public static Item compass = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		compass = new ItemCustomCompass();
		event.getRegistry().register(compass);
	}
}

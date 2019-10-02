package party.lemons.bettercompass.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import party.lemons.bettercompass.BetterCompass;

/**
 * Created by Sam on 3/02/2018.
 */
@Mod.EventBusSubscriber(modid = BetterCompass.MODID)
public class ModItems
{
	@ObjectHolder("minecraft:compass")
	public static final Item compass = Items.COMPASS;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(new ItemCustomCompass());
	}
}

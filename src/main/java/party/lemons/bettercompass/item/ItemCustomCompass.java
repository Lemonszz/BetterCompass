package party.lemons.bettercompass.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import party.lemons.bettercompass.config.Config;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Sam on 3/02/2018.
 */
public class ItemCustomCompass extends ItemCompass
{
	private static final Properties PROPS = new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1);

	public ItemCustomCompass()
	{
		super(PROPS);

		this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
			@OnlyIn(Dist.CLIENT)
			private double rotation;
			@OnlyIn(Dist.CLIENT)
			private double rota;
			@OnlyIn(Dist.CLIENT)
			private long lastUpdateTick;

			@OnlyIn(Dist.CLIENT)
			public float call(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if(stack.isOnItemFrame())
					return 0.0F;

				if (entityIn == null)
				{
					return 0.0F;
				}
				else
				{
					boolean flag = entityIn != null;
					NBTTagCompound tags = stack.getTag();
					Entity entity = flag ? entityIn : stack.getItemFrame();

					if (worldIn == null)
					{
						worldIn = entity.world;
					}

					double d0;
					int dim = 0;
					if(tags != null && tags.hasKey("dim"))
						dim = tags.getInt("dim");

					boolean isSameDim = entityIn.dimension.getId() == dim;
					boolean show = isSameDim && (worldIn.dimension.isSurfaceWorld() || Config.allowCompassInAllDimensions.get());

					if (show)
					{
						double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
						d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
						double d2 = this.getSpawnToAngle(worldIn, entity, stack) / (Math.PI * 2D);
						d0 = 0.5D - (d1 - 0.25D - d2);
					}
					else
					{
						if(tags != null && tags.hasKey("rotation"))
							d0 = tags.getDouble("rotation");
						else
							d0 = Math.random();
					}
					return MathHelper.positiveModulo((float)d0, 1.0F);
				}
			}

			@OnlyIn(Dist.CLIENT)
			private double getFrameRotation(EntityItemFrame p_185094_1_) {
				return (double)MathHelper.wrapDegrees(180 + p_185094_1_.facingDirection.getHorizontalIndex() * 90);
			}

			@OnlyIn(Dist.CLIENT)
			private double getSpawnToAngle(World world, Entity entity, ItemStack stack)
			{
				BlockPos pos = ItemCustomCompass.getPositionFromStack(stack, world);
				return Math.atan2((double)pos.getZ() - entity.posZ, (double)pos.getX() - entity.posX);
			}
		});
		this.setRegistryName("minecraft", "compass");
	}

	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		World worldIn = ctx.getWorld();
		EntityPlayer player = ctx.getPlayer();
		ItemStack stack = ctx.getItem();
		BlockPos pos = ctx.getPos();

		if(!Config.allowCompassInAllDimensions.get() && !worldIn.dimension.isSurfaceWorld())
		{
			return EnumActionResult.FAIL;
		}

		NBTTagCompound tags = stack.getTag();
		if(tags == null)
			tags = new NBTTagCompound();

		tags.setTag("pos", NBTUtil.writeBlockPos(pos));
		tags.setInt("dim", player.dimension.getId());

		stack.setTag(tags);
		player.sendStatusMessage(new TextComponentTranslation("bettercompass.message.set"), true);
		return EnumActionResult.SUCCESS;
	}

	public static BlockPos getPositionFromStack(ItemStack stack, World world)
	{
		BlockPos pos = world.getSpawnPoint();
		if(stack.hasTag() && stack.getTag().hasKey("pos"))
		{
			pos = NBTUtil.readBlockPos(stack.getTag().getCompound("pos"));
		}
		return pos;
	}

	public static int getDimensionFromStack(ItemStack stack)
	{
		int dim = 0 ;
		if(stack.hasTag() && stack.getTag().hasKey("dim"))
		{
			return stack.getTag().getInt("dim");
		}
		return dim;
	}

	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{

		if(stack.getTag() != null && stack.getTag().hasKey("pos"))
		{
			if(Config.showCustomLocationText.get())
			{
				TextComponentTranslation txt = new TextComponentTranslation("bettercompass.message.info");
				txt.setStyle(new Style().setColor(TextFormatting.DARK_PURPLE));
				tooltip.add(txt);
			}

			if(Config.showLocationInfoText.get() && worldIn != null)
			{
				BlockPos pos = getPositionFromStack(stack, worldIn);
				int dim = getDimensionFromStack(stack);
				String dimText = String.valueOf(dim);
				switch(dim)
				{
					case 0:
							dimText = "bettercompass.message.info.overworld";
						break;
					case -1:
						dimText = "bettercompass.message.info.nether";
						break;
					case 1:
						dimText = "bettercompass.message.info.end";
						break;
				}

				TextComponentString posText = new TextComponentString("x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ());
				posText.setStyle(new Style().setColor(TextFormatting.GRAY));

				TextComponentString dimString = new TextComponentString(I18n.format("bettercompass.message.info.dimension") + ": " + I18n.format(dimText));
				dimString.setStyle(posText.getStyle());

				tooltip.add(posText);
				tooltip.add(dimString);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(worldIn.isRemote)
		{
			if(stack.hasTag())
			{
				NBTTagCompound tags = stack.getTag();

				long lastUpdateTick = 0;
				double rotation = 0;
				double rota = 0;
				if(tags.hasKey("last_update"))
					lastUpdateTick = tags.getLong("last_update");
				if(tags.hasKey("rotation"))
					rotation = tags.getDouble("rotation");
				if(tags.hasKey("rota"))
					rota = tags.getDouble("rota");


				if (worldIn.getGameTime() != lastUpdateTick)
				{
					lastUpdateTick = worldIn.getGameTime();
					double d0 = Math.random() - rotation;
					d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
					rota += d0 * 0.1D;
					rota *= 0.8D;
					rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);

					tags.setLong("last_update", lastUpdateTick);
					tags.setDouble("rotation", rotation);
					tags.setDouble("rota", rota);
				}
			}
		}
	}
}


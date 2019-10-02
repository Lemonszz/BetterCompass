package party.lemons.bettercompass.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import party.lemons.bettercompass.config.CompassSetting;
import party.lemons.bettercompass.config.BCConfig;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Sam on 3/02/2018.
 */
public class ItemCustomCompass extends CompassItem
{
	private static int time = 0;

	public ItemCustomCompass()
	{
		super(new Properties().group(ItemGroup.TOOLS).maxStackSize(1));

		this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter()
		{

			@OnlyIn(Dist.CLIENT)
			public float call(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
			{
				if(stack.isOnItemFrame()) return 0.0F;

				if(entityIn == null)
				{
					return 0.0F;
				}else
				{
					boolean flag = entityIn != null;
					CompoundNBT tags = stack.getTag();
					Entity entity = flag ? entityIn : stack.getItemFrame();

					if(worldIn == null)
					{
						worldIn = entity.world;
					}

					double d0;
					int dim = 0;
					if(tags != null && tags.contains("dim")) dim = tags.getInt("dim");

					boolean isSameDim = entityIn.dimension.getId() == dim;
					boolean show = isSameDim && (worldIn.getDimension().isSurfaceWorld() || BCConfig.allowCompassInAllDimensions);

					if(show)
					{
						double d1 = flag ? (double) entity.rotationYaw : this.getFrameRotation((ItemFrameEntity) entity);
						d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
						double d2 = this.getSpawnToAngle(worldIn, entity, stack) / (Math.PI * 2D);
						d0 = 0.5D - (d1 - 0.25D - d2);
					}else
					{
						if(tags != null && tags.contains("rotation")) d0 = tags.getDouble("rotation");
						else d0 = Math.random();
					}
					return MathHelper.positiveModulo((float) d0, 1.0F);
				}
			}

			@OnlyIn(Dist.CLIENT)
			private double getFrameRotation(ItemFrameEntity frame)
			{
				return MathHelper.wrapDegrees(180 + frame.getHorizontalFacing().getHorizontalIndex() * 90);
			}

			@OnlyIn(Dist.CLIENT)
			private double getSpawnToAngle(World world, Entity entity, ItemStack stack)
			{
				BlockPos pos = ItemCustomCompass.getPositionFromStack(stack, world);
				return Math.atan2((double) pos.getZ() - entity.posZ, (double) pos.getX() - entity.posX);
			}
		});
		this.setRegistryName("minecraft", "compass");
	}

	public ActionResultType onItemUse(ItemUseContext context)
	{
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		Hand hand = context.getHand();
		BlockPos pos = context.getPos();

		if(!BCConfig.allowCompassInAllDimensions && !world.getDimension().isSurfaceWorld())
		{
			return ActionResultType.FAIL;
		}

		if(BCConfig.compassActivateType == CompassSetting.SNEAK && !player.isSneaking())
			return ActionResultType.FAIL;

		ItemStack stack = player.getHeldItem(hand);

		if(BCConfig.compassActivateType == CompassSetting.REQUIRE_EMPTY && stack.hasTag())
			return ActionResultType.FAIL;

		CompoundNBT tags = stack.getTag();
		if(tags == null)
			tags = new CompoundNBT();

		tags.put("pos", NBTUtil.writeBlockPos(pos));
		tags.putInt("dim", player.dimension.getId());

		player.getHeldItem(hand).setTag(tags);
		player.sendStatusMessage(new TranslationTextComponent("bettercompass.message.set"), true);
		return ActionResultType.SUCCESS;
	}

	public static BlockPos getPositionFromStack(ItemStack stack, World world)
	{
		BlockPos pos = world.getSpawnPoint();
		if(stack.hasTag() && stack.getTag().contains("pos"))
		{
			pos = NBTUtil.readBlockPos(stack.getTag().getCompound("pos"));
		}
		return pos;
	}

	public static int getDimensionFromStack(ItemStack stack)
	{
		int dim = 0 ;
		if(stack.hasTag() && stack.getTag().contains("dim"))
		{
			return stack.getTag().getInt("dim");
		}
		return dim;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{

		if(stack.getTag() != null && stack.getTag().contains("pos"))
		{
			if(BCConfig.showCustomLocationText)
				tooltip.add(new TranslationTextComponent("bettercompass.message.info").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));

			if(BCConfig.showLocationInfoText && worldIn != null)
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

				ITextComponent txt = new StringTextComponent("x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ()).setStyle(new Style().setColor(TextFormatting.GRAY));
				ITextComponent txt2 = new TranslationTextComponent("bettercompass.message.info.dimension" + ": " + I18n.format(dimText)).setStyle(new Style().setColor(TextFormatting.GRAY));
				tooltip.add(txt);
				tooltip.add(txt2);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected)
	{
		if(world.isRemote)
		{
			time++;
			if(stack.hasTag())
			{
				CompoundNBT tags = stack.getTag();

				long lastUpdateTick = 0;
				double rotation = 0;
				double rota = 0;
				if(tags.contains("last_update"))
					lastUpdateTick = tags.getLong("last_update");
				if(tags.contains("rotation"))
					rotation = tags.getDouble("rotation");
				if(tags.contains("rota"))
					rota = tags.getDouble("rota");


				if (time != lastUpdateTick)
				{
					lastUpdateTick = time;
					double d0 = Math.random() - rotation;
					d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
					rota += d0 * 0.1D;
					rota *= 0.8D;
					rotation = MathHelper.positiveModulo(rotation + rota, 1.0D);

					tags.putLong("last_update", lastUpdateTick);
					tags.putDouble("rotation", rotation);
					tags.putDouble("rota", rota);
				}
			}
		}
	}
}

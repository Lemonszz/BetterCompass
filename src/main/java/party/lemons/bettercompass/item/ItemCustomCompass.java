package party.lemons.bettercompass.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import party.lemons.bettercompass.config.CompassSetting;
import party.lemons.bettercompass.config.ModConfig;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Sam on 3/02/2018.
 */
public class ItemCustomCompass extends ItemCompass
{
	public ItemCustomCompass()
	{
		this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter()
		{

			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if(stack.isOnItemFrame())
					return 0.0F;

				if (entityIn == null)
				{
					return 0.0F;
				}
				else
				{
					boolean flag = entityIn != null;
					NBTTagCompound tags = stack.getTagCompound();
					Entity entity = flag ? entityIn : stack.getItemFrame();

					if (worldIn == null)
					{
						worldIn = entity.world;
					}

					double d0;
					int dim = 0;
					if(tags != null && tags.hasKey("dim"))
						dim = tags.getInteger("dim");

					boolean isSameDim = entityIn.dimension == dim;
					boolean show = isSameDim && (worldIn.provider.isSurfaceWorld() || ModConfig.allowCompassInAllDimensions);

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

			@SideOnly(Side.CLIENT)
			private double getFrameRotation(EntityItemFrame p_185094_1_)
			{
				return (double)MathHelper.wrapDegrees(180 + p_185094_1_.facingDirection.getHorizontalIndex() * 90);
			}

			@SideOnly(Side.CLIENT)
			private double getSpawnToAngle(World world, Entity entity, ItemStack stack)
			{
				BlockPos pos = ItemCustomCompass.getPositionFromStack(stack, world);
				return Math.atan2((double)pos.getZ() - entity.posZ, (double)pos.getX() - entity.posX);
			}
		});

		if (ModConfig.useHomingCompassInstead)
		{
			this.setRegistryName("bettercompass", "homing_compass");
			this.setUnlocalizedName("bettercompass.homing_compass");
		}
		else
		{
			this.setRegistryName("minecraft", "compass");
			this.setUnlocalizedName("compass");
		}

		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setMaxStackSize(1);
	}

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!ModConfig.allowCompassInAllDimensions && !worldIn.provider.isSurfaceWorld())
		{
			return EnumActionResult.FAIL;
		}

		if(ModConfig.compassActivateType == CompassSetting.SNEAK && !player.isSneaking())
			return EnumActionResult.FAIL;

		ItemStack stack = player.getHeldItem(hand);

		if(ModConfig.compassActivateType == CompassSetting.REQUIRE_EMPTY && stack.hasTagCompound())
			return EnumActionResult.FAIL;

		NBTTagCompound tags = stack.getTagCompound();
		if(tags == null)
			tags = new NBTTagCompound();

		tags.setTag("pos", NBTUtil.createPosTag(pos));
		tags.setInteger("dim", player.dimension);

		player.getHeldItem(hand).setTagCompound(tags);
		player.sendStatusMessage(new TextComponentTranslation("bettercompass.message.set"), true);
		return EnumActionResult.SUCCESS;
	}

	public static BlockPos getPositionFromStack(ItemStack stack, World world)
	{
		BlockPos pos = world.getSpawnPoint();
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("pos"))
		{
			pos = NBTUtil.getPosFromTag(stack.getTagCompound().getCompoundTag("pos"));
		}
		return pos;
	}

	public static int getDimensionFromStack(ItemStack stack)
	{
		int dim = 0 ;
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("dim"))
		{
			return stack.getTagCompound().getInteger("dim");
		}
		return dim;
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{

		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("pos"))
		{
			if(ModConfig.showCustomLocationText)
				tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("bettercompass.message.info"));

			if(ModConfig.showLocationInfoText && worldIn != null)
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

				tooltip.add(TextFormatting.GRAY +  "x: " + pos.getX() + ", y: " + pos.getY() + ", z: " + pos.getZ());
				tooltip.add(TextFormatting.GRAY + I18n.format("bettercompass.message.info.dimension") + ": " + I18n.format(dimText));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
	{
		if(worldIn.isRemote)
		{
			if(stack.hasTagCompound())
			{
				NBTTagCompound tags = stack.getTagCompound();

				long lastUpdateTick = 0;
				double rotation = 0;
				double rota = 0;
				if(tags.hasKey("last_update"))
					lastUpdateTick = tags.getLong("last_update");
				if(tags.hasKey("rotation"))
					rotation = tags.getDouble("rotation");
				if(tags.hasKey("rota"))
					rota = tags.getDouble("rota");


				if (worldIn.getTotalWorldTime() != lastUpdateTick)
				{
					lastUpdateTick = worldIn.getTotalWorldTime();
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

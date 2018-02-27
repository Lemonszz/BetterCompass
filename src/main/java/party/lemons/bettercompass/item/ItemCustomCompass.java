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
			double rotation;
			@SideOnly(Side.CLIENT)
			double rota;
			@SideOnly(Side.CLIENT)
			long lastUpdateTick;

			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if (entityIn == null && !stack.isOnItemFrame())
				{
					return 0.0F;
				}
				else
				{
					boolean flag = entityIn != null;
					Entity entity = flag ? entityIn : stack.getItemFrame();

					if (worldIn == null)
					{
						worldIn = entity.world;
					}

					double d0;

					if (worldIn.provider.isSurfaceWorld())
					{
						double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((EntityItemFrame)entity);
						d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
						double d2 = this.getSpawnToAngle(worldIn, entity, stack) / (Math.PI * 2D);
						d0 = 0.5D - (d1 - 0.25D - d2);
					}
					else
					{
						d0 = Math.random();
					}

					return MathHelper.positiveModulo((float)d0, 1.0F);
				}
			}

			//TODO: Fix this to work as intended by bv anilla
			@SideOnly(Side.CLIENT)
			private double wobble(World worldIn, double p_185093_2_)
			{
				if (worldIn.getTotalWorldTime() != this.lastUpdateTick)
				{
					this.lastUpdateTick = worldIn.getTotalWorldTime();
					double d0 = p_185093_2_ - this.rotation;
					d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
					this.rota += d0 * 0.1D;
					this.rota *= 0.8D;
					this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
				}

				return this.rotation;
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

		this.setRegistryName("minecraft", "compass");
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.setUnlocalizedName("compass");
		this.setMaxStackSize(1);
	}

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		NBTTagCompound tags = stack.getTagCompound();
		if(tags == null)
			tags = new NBTTagCompound();

		tags.setTag("pos", NBTUtil.createPosTag(pos));

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

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(ModConfig.showCustomLocationText)
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("pos"))
		{
			tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("bettercompass.message.info"));
		}
	}

}

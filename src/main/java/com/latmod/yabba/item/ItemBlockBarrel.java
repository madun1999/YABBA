package com.latmod.yabba.item;

import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.latmod.yabba.YabbaCommon;
import com.latmod.yabba.api.IBarrel;
import com.latmod.yabba.api.Tier;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemBlockBarrel extends ItemBlock
{
	public ItemBlockBarrel(Block block)
	{
		super(block);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		return new BarrelItemData(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		IBarrel barrel = stack.getCapability(YabbaCommon.BARREL_CAPABILITY, null);

		tooltip.add(ItemHammer.getModelTooltip(barrel.getModel()));
		tooltip.add(ItemPainter.getSkinTooltip(barrel.getSkin()));

		Tier tier = barrel.getTier();
		ItemStack stack1 = barrel.getStackInSlot(0);

		if (!stack1.isEmpty())
		{
			tooltip.add("Item: " + stack1.getDisplayName());
		}

		if (!barrel.getFlag(IBarrel.FLAG_IS_CREATIVE))
		{
			if (barrel.getFlag(IBarrel.FLAG_INFINITE_CAPACITY))
			{
				tooltip.add(barrel.getItemCount() + " items");
			}
			else if (!stack1.isEmpty())
			{
				tooltip.add(barrel.getItemCount() + " / " + tier.getMaxItems(barrel, stack1));
			}
			else
			{
				tooltip.add("Max " + tier.maxItemStacks.getInt() + " stacks");
			}
		}

		NBTTagList upgrades = barrel.getUpgradeNames();

		if (upgrades != null && !upgrades.hasNoTags())
		{
			tooltip.add("Upgrades:");

			for (int i = 0; i < upgrades.tagCount(); i++)
			{
				tooltip.add("> " + StringUtils.translate(upgrades.getStringTagAt(i)));
			}
		}
	}
}
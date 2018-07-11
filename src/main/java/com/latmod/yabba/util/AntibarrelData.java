package com.latmod.yabba.util;

import com.feed_the_beast.ftblib.lib.item.ItemEntry;
import com.feed_the_beast.ftblib.lib.item.ItemEntryWithCount;
import com.feed_the_beast.ftblib.lib.tile.IChangeCallback;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.latmod.yabba.YabbaConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class AntibarrelData implements ICapabilitySerializable<NBTTagCompound>, IItemHandler
{
	@CapabilityInject(AntibarrelData.class)
	public static Capability<AntibarrelData> CAP;

	private IChangeCallback callback;
	public final Map<ItemEntry, ItemEntryWithCount> items;
	private ItemEntryWithCount[] itemsArray;
	private int totalItemCount;

	public static AntibarrelData get(ItemStack stack)
	{
		AntibarrelData data = stack.getCapability(CAP, null);

		if (NBTUtils.hasBlockData(stack))
		{
			data.deserializeNBT(NBTUtils.getBlockData(stack));
			NBTUtils.removeBlockData(stack);
		}

		return data;
	}

	public AntibarrelData(@Nullable IChangeCallback c)
	{
		callback = c;
		items = new LinkedHashMap<>();
	}

	public void clear()
	{
		items.clear();
		itemsArray = null;
		totalItemCount = -1;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this : null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (items.isEmpty())
		{
			return nbt;
		}

		NBTTagList list = new NBTTagList();

		for (ItemEntryWithCount entry : items.values())
		{
			if (!entry.isEmpty())
			{
				list.appendTag(entry.serializeNBT());
			}
		}

		nbt.setTag("Inv", list);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		clear();

		NBTTagList list = nbt.getTagList("Inv", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			ItemEntryWithCount entryc = new ItemEntryWithCount(list.getCompoundTagAt(i));

			if (!entryc.isEmpty())
			{
				items.put(entryc.entry, entryc);
			}
		}
	}

	public void copyFrom(AntibarrelData data)
	{
		clear();
		totalItemCount = data.totalItemCount;

		for (ItemEntryWithCount entry : data.items.values())
		{
			items.put(entry.entry, new ItemEntryWithCount(entry.entry, entry.count));
		}
	}

	@Override
	public int getSlots()
	{
		return items.size() + 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot <= 0 || slot > items.size() ? ItemStack.EMPTY : getItemArray()[slot - 1].getStack(false);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (slot < 0 || slot > items.size() || stack.isEmpty() || stack.isStackable())
		{
			return stack;
		}

		ItemEntry entry = ItemEntry.get(stack);
		ItemEntryWithCount entryc;
		int added = 0;

		if (slot == 0)
		{
			entryc = items.get(entry);

			if (entryc != null)
			{
				added = Math.min(YabbaConfig.general.antibarrel_items_per_type - entryc.count, stack.getCount());
			}
			else if (items.size() < YabbaConfig.general.antibarrel_capacity)
			{
				entryc = new ItemEntryWithCount(entry, 0);
				items.put(entry, entryc);
				itemsArray = null;
				totalItemCount = -1;
				added = Math.min(YabbaConfig.general.antibarrel_items_per_type, stack.getCount());
			}
		}
		else
		{
			entryc = getItemArray()[slot - 1];

			if (entryc.entry.equalsEntry(entry))
			{
				added = Math.min(YabbaConfig.general.antibarrel_items_per_type - entryc.count, stack.getCount());
			}
		}

		if (entryc != null && added > 0)
		{
			if (!simulate)
			{
				entryc.count += added;
				totalItemCount = -1;

				if (callback != null)
				{
					callback.onContentsChanged(false);
				}
			}

			return added == stack.getCount() ? ItemStack.EMPTY : ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - added);
		}

		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (slot <= 0 || slot > items.size() || amount < 1)
		{
			return ItemStack.EMPTY;
		}

		ItemEntryWithCount entryc = getItemArray()[slot - 1];

		if (entryc.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		int extracted = Math.min(amount, entryc.count);

		ItemStack is = entryc.entry.getStack(extracted, true);

		if (!simulate)
		{
			entryc.count -= extracted;
			totalItemCount = -1;

			if (callback != null)
			{
				callback.onContentsChanged(false);
			}
		}

		return is;
	}

	private ItemEntryWithCount[] getItemArray()
	{
		if (itemsArray == null)
		{
			itemsArray = items.values().toArray(new ItemEntryWithCount[0]);
		}

		return itemsArray;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return YabbaConfig.general.antibarrel_items_per_type;
	}

	public int getTotalItemCount()
	{
		if (totalItemCount >= 0)
		{
			return totalItemCount;
		}

		totalItemCount = 0;

		for (ItemEntryWithCount entry : items.values())
		{
			totalItemCount += entry.count;
		}

		return totalItemCount;
	}
}
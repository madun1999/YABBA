package com.latmod.yabba;

import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LatvianModder
 */
@Mod(modid = Yabba.MOD_ID, name = Yabba.MOD_NAME, version = Yabba.VERSION, acceptedMinecraftVersions = "[1.12,)", dependencies = "required-after:" + FTBLibFinals.MOD_ID + ";after:" + FTBLibFinals.FORESTRY)
public class Yabba
{
	public static final String MOD_ID = "yabba";
	public static final String MOD_NAME = "YABBA";
	public static final String VERSION = "@VERSION@";
	public static final String REFINED_STORAGE_ID = "refinedstorage";

	@Mod.Instance(Yabba.MOD_ID)
	public static Yabba INST;

	@SidedProxy(serverSide = "com.latmod.yabba.YabbaCommon", clientSide = "com.latmod.yabba.client.YabbaClient")
	public static YabbaCommon PROXY;

	public static final Logger LOGGER = LogManager.getLogger("YABBA");

	public static final CreativeTabs TAB = new CreativeTabs(MOD_ID)
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(YabbaItems.UPGRADE_BLANK);
		}
	};

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		PROXY.postInit();
	}
}
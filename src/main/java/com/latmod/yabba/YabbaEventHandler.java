package com.latmod.yabba;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.ReloadEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class YabbaEventHandler
{
	public static final ResourceLocation RELOAD_CONFIG = new ResourceLocation(Yabba.MOD_ID, "config");

	@SubscribeEvent
	public static void registerReloadIds(ReloadEvent.RegisterIds event)
	{
		event.register(RELOAD_CONFIG);
	}

	@SubscribeEvent
	public static void onReload(ReloadEvent event)
	{
		if (event.getSide().isServer())
		{
			if (event.reload(RELOAD_CONFIG))
			{
				YabbaConfig.sync();
			}
		}
	}
}
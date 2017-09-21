package com.latmod.yabba.integration;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.lib.TextureSet;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import com.latmod.yabba.api.BarrelSkin;
import com.latmod.yabba.api.YabbaSkinsEvent;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler(requiredMods = FTBLibFinals.FORESTRY)
public class ForestryIntegration
{
	@SubscribeEvent
	public static void registerSkins(YabbaSkinsEvent event)
	{
		for (IWoodType type : TreeManager.woodAccess.getRegisteredWoodTypes())
		{
			if (!(type instanceof EnumVanillaWoodType))
			{
				try
				{
					BarrelSkin skin = new BarrelSkin(FTBLibFinals.FORESTRY + ":planks_" + type.getName(), TextureSet.of("all=" + type.getPlankTexture()));

					try
					{
						skin.state = TreeManager.woodAccess.getBlock(type, WoodBlockKind.PLANKS, false);
					}
					catch (Exception ex1)
					{
					}

					skin.displayName = TreeManager.woodAccess.getStack(type, WoodBlockKind.PLANKS, false).getDisplayName();
					event.addSkin(skin);

					skin = new BarrelSkin(FTBLibFinals.FORESTRY + ":log_" + type.getName(), TextureSet.of("up&down=" + type.getHeartTexture() + ",all=" + type.getBarkTexture()));

					try
					{
						skin.state = TreeManager.woodAccess.getBlock(type, WoodBlockKind.LOG, false);
					}
					catch (Exception ex1)
					{
					}

					skin.displayName = TreeManager.woodAccess.getStack(type, WoodBlockKind.LOG, false).getDisplayName();
					event.addSkin(skin);
				}
				catch (Exception ex)
				{
				}
			}
		}
	}
}
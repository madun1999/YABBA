package com.latmod.yabba.client;

import com.feed_the_beast.ftbl.lib.Color4I;
import com.latmod.yabba.Yabba;
import com.latmod.yabba.YabbaCommon;
import com.latmod.yabba.YabbaConfig;
import com.latmod.yabba.api.IBarrel;
import com.latmod.yabba.net.MessageRequestBarrelUpdate;
import com.latmod.yabba.tile.TileBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;

/**
 * Created by LatvianModder on 13.12.2016.
 */
public class RenderBarrel extends TileEntitySpecialRenderer<TileBarrel>
{
    private static final ResourceLocation TEXTURE_SETTINGS = new ResourceLocation(Yabba.MOD_ID, "textures/blocks/barrel_settings.png");
    private static final Color4I CREATIVE_COLOR = new Color4I(false, 0xFFFF00DC);

    @Override
    public void renderTileEntityAt(TileBarrel te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if(te.isInvalid())
        {
            return;
        }
        else if(te.requestClientUpdate)
        {
            new MessageRequestBarrelUpdate(te.getPos()).sendToServer();
            te.requestClientUpdate = false;
        }

        IBarrel barrel = te.getCapability(YabbaCommon.BARREL_CAPABILITY, null);
        ItemStack stack = barrel.getStackInSlot(0);
        boolean hasStack = stack != null;
        Minecraft mc = Minecraft.getMinecraft();

        boolean isSneaking = mc.player.isSneaking();

        if(!hasStack && !isSneaking)
        {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0F, 1F, 0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate(te.getRotationAngleY(), 0F, 1F, 0F);
        GlStateManager.rotate(te.getRotationAngleX(), 1F, 0F, 0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        GlStateManager.color(1F, 1F, 1F, 1F);
        setLightmapDisabled(true);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.depthMask(true);

        boolean mouseOver = mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.getBlockPos().equals(te.getPos());

        if(mouseOver || barrel.getFlag(IBarrel.FLAG_ALWAYS_DISPLAY_DATA))
        {
            boolean isCreative = barrel.getFlag(IBarrel.FLAG_IS_CREATIVE);
            float textDistance = barrel.getModel().getTextDistance();

            if(hasStack)
            {
                if(!isCreative && !isSneaking && barrel.getFlag(IBarrel.FLAG_DISPLAY_BAR))
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.disableTexture2D();
                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer buffer = tessellator.getBuffer();
                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                    double b = 0.02D;
                    double b2 = b * 2D;
                    double bx = 0.0625D;
                    double by = 0.0625D;
                    double bw = 1D - bx * 2D;
                    double bh = 0.15D;
                    double filled = MathHelper.clamp(barrel.getItemCount() / (double) barrel.getTier().getMaxItems(barrel, barrel.getStackInSlot(0)), 0D, 1D);

                    int a = YabbaConfig.BAR_COLOR_ALPHA.getInt();
                    rect(buffer, bx, by, textDistance, b, bh, YabbaConfig.BAR_COLOR_BORDER.getColor(), a);
                    rect(buffer, 1D - b - bx, by, textDistance, b, bh, YabbaConfig.BAR_COLOR_BORDER.getColor(), a);
                    rect(buffer, bx + b, by, textDistance, bw - b2, b, YabbaConfig.BAR_COLOR_BORDER.getColor(), a);
                    rect(buffer, bx + b, by + bh - b, textDistance, bw - b2, b, YabbaConfig.BAR_COLOR_BORDER.getColor(), a);
                    rect(buffer, bx + b, by + b, textDistance, (bw - b2) * filled, bh - b2, YabbaConfig.BAR_COLOR_FREE.getColor(), a);
                    rect(buffer, bx + b + (bw - b2) * filled, by + b, textDistance, (bw - b2) * (1D - filled), bh - b2, YabbaConfig.BAR_COLOR_FILLED.getColor(), a);
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.popMatrix();
                }
                else
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.5F, 0.075F, textDistance);
                    String s1 = te.getItemDisplayCount(isSneaking);
                    int sw = getFontRenderer().getStringWidth(s1);
                    float f = 1F / (float) Math.max((sw + 10), 64);
                    GlStateManager.scale(f, f, 1F);
                    getFontRenderer().drawString(s1, -sw / 2, 0, 0xFFFFFFFF);
                    GlStateManager.popMatrix();
                }

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5F, 0.80F, textDistance);
                String s2 = te.getItemDisplayName();
                int sw1 = getFontRenderer().getStringWidth(s2);
                float f1 = 1F / (float) Math.max((sw1 + 10), 64);
                GlStateManager.scale(f1, f1, 1F);
                getFontRenderer().drawString(s2, -sw1 / 2, 0, 0xFFFFFFFF);
                GlStateManager.popMatrix();
            }

            if(isSneaking && mouseOver && mc.player.getHeldItem(EnumHand.MAIN_HAND) == null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0D, 0D, textDistance);
                mc.getTextureManager().bindTexture(TEXTURE_SETTINGS);
                GlStateManager.enableTexture2D();
                GlStateManager.color(1F, 1F, 1F, 1F);

                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer buffer = tessellator.getBuffer();

                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                int a = 255;

                double is = TileBarrel.BUTTON_SIZE;
                double ix = 1F - is;
                double iy = 0.5D - is / 2D;
                double u = isCreative ? 0.5D : (barrel.getFlag(IBarrel.FLAG_LOCKED) ? 0D : 0.5D);
                double v = isCreative ? 0.5D : 0D;

                buffer.pos(ix, iy + is, 0D).tex(u, v + 0.5D).color(255, 255, 255, a).endVertex();
                buffer.pos(ix + is, iy + is, 0D).tex(u + 0.5D, v + 0.5D).color(255, 255, 255, a).endVertex();
                buffer.pos(ix + is, iy, 0D).tex(u + 0.5D, v).color(255, 255, 255, a).endVertex();
                buffer.pos(ix, iy, 0D).tex(u, v).color(255, 255, 255, a).endVertex();

                ix = 0D;
                u = 0D;
                v = 0.5D;
                Color4I col = isCreative ? CREATIVE_COLOR : barrel.getTier().getColor();
                buffer.pos(ix, iy + is, 0D).tex(u, v + 0.5D).color(col.red(), col.green(), col.blue(), a).endVertex();
                buffer.pos(ix + is, iy + is, 0D).tex(u + 0.5D, v + 0.5D).color(col.red(), col.green(), col.blue(), a).endVertex();
                buffer.pos(ix + is, iy, 0D).tex(u + 0.5D, v).color(col.red(), col.green(), col.blue(), a).endVertex();
                buffer.pos(ix, iy, 0D).tex(u, v).color(col.red(), col.green(), col.blue(), a).endVertex();

                tessellator.draw();

                GlStateManager.popMatrix();
            }
        }

        if(hasStack)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5F, 0.5F, barrel.getModel().getItemDistance());
            GlStateManager.scale(0.4F, -0.4F, -0.015F);

            RenderItem itemRender = mc.getRenderItem();
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1F, 1F, 1F, 1F);
            IBakedModel bakedmodel = itemRender.getItemModelWithOverrides(stack, null, mc.player);
            bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
            itemRender.renderItem(stack, bakedmodel);
            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

            GlStateManager.popMatrix();
        }

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();

        setLightmapDisabled(false);
    }

    private static void rect(VertexBuffer buffer, double x, double y, double z, double w, double h, Color4I col, int a)
    {
        buffer.pos(x, y, z).color(col.red(), col.green(), col.blue(), a).endVertex();
        buffer.pos(x, y + h, z).color(col.red(), col.green(), col.blue(), a).endVertex();
        buffer.pos(x + w, y + h, z).color(col.red(), col.green(), col.blue(), a).endVertex();
        buffer.pos(x + w, y, z).color(col.red(), col.green(), col.blue(), a).endVertex();
    }
}
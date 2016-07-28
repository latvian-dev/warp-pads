package com.latmod.xpt.client;

import com.latmod.xpt.block.TileTeleporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderTeleporter extends TileEntitySpecialRenderer<TileTeleporter>
{
    private static long debugTimer = 0L;

    @Override
    public void renderTileEntityAt(@Nonnull TileTeleporter te, double rx, double ry, double rz, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(rx + 0.5D, ry + 0.5D, rz + 0.5D);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        //render

        String name = te.getName();

        GlStateManager.pushMatrix();
        GlStateManager.translate(rx + 0.5D, ry + 0.5D, rz + 0.5D);
        //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.enableTexture2D();
        float f1 = 0.02F;
        GlStateManager.scale(-f1, -f1, f1);

        GlStateManager.rotate(0F, 0F, 1F, 0F);

        GlStateManager.color(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().fontRendererObj.drawString(name, -(Minecraft.getMinecraft().fontRendererObj.getStringWidth(name) / 2), -8, 0xFFFFFFFF);
        GlStateManager.popMatrix();

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
    }

    private void drawRect(double x, double y, double w, double h, double z)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(x, y, z).endVertex();
        buffer.pos(x + w, y, z).endVertex();
        buffer.pos(x + w, y + h, z).endVertex();
        buffer.pos(x, y + h, z).endVertex();
        tessellator.draw();
    }

    private double getAlpha(double dist)
    {
        if(dist < 2D)
        {
            return dist / 2D;
        }
        double maxDist = 5D;
        if(dist <= maxDist)
        {
            return 1F;
        }
        if(dist > maxDist + 3D)
        {
            return 0F;
        }
        return (maxDist + 3D - dist) / (maxDist - 3D);
    }
}
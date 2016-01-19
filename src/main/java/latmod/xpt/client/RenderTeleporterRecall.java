package latmod.xpt.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.GlStateManager;
import latmod.xpt.XPT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTeleporterRecall implements IItemRenderer
{
	public static final RenderTeleporterRecall instance = new RenderTeleporterRecall();
	
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return type == ItemRenderType.INVENTORY;
	}
	
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}
	
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		RenderBlocks rb = (RenderBlocks) data[0];
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		XPT.teleporter_recall.setBlockBoundsForItemRender();
		rb.setRenderBoundsFromBlock(XPT.teleporter_recall);
		rb.renderBlockAsItem(XPT.teleporter_recall, 0, 1F);
		
		float lastBrightnessX = OpenGlHelper.lastBrightnessX;
		float lastBrightnessY = OpenGlHelper.lastBrightnessY;
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(0F, -0.6F, 0F);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(45F, 0F, 1F, 0F);
		
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		GlStateManager.color(1F, 1F, 0F, 0F);
		GL11.glBegin(GL11.GL_QUADS);
		float s = 0.75F;
		float s1 = 0.12F;
		GL11.glVertex3d(-s, -1.5F, 0F);
		GL11.glVertex3d(s, -1.5F, 0F);
		
		GlStateManager.color(1F, 0.9F, 0.2F, 0.8F);
		
		GL11.glVertex3f(s1, -0.125F, 0F);
		GL11.glVertex3f(-s1, -0.125F, 0F);
		GL11.glEnd();
		
		GlStateManager.popAttrib();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
		GlStateManager.popMatrix();
		
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.depthMask(true);
	}
}
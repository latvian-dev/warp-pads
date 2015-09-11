package latmod.xpt.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;
import latmod.xpt.XPT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

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
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		RenderBlocks rb = (RenderBlocks)data[0];
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		XPT.teleporter_recall.setBlockBoundsForItemRender();
		rb.setRenderBoundsFromBlock(XPT.teleporter_recall);
		rb.renderBlockAsItem(XPT.teleporter_recall, 0, 1F);
		
		float lastBrightnessX = OpenGlHelper.lastBrightnessX;
		float lastBrightnessY = OpenGlHelper.lastBrightnessY;
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GL11.glTranslatef(0F, -0.6F, 0F);
		float sc = 1F;
		GL11.glScalef(-sc, -sc, sc);
		GL11.glRotated(45F, 0D, 1D, 0D);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		GL11.glColor4f(1F, 1F, 0F, 0F);
		GL11.glBegin(GL11.GL_QUADS);
		float s = 0.75F;
		float s1 = 0.12F;
		GL11.glVertex3d(-s, -1.5F, 0F);
		GL11.glVertex3d(s, -1.5F, 0F);
		
		GL11.glColor4f(1F, 0.9F, 0.2F, 0.8F);
		
		GL11.glVertex3f(s1, -0.125F, 0F);
		GL11.glVertex3f(-s1, -0.125F, 0F);
		GL11.glEnd();
		
		GL11.glPopAttrib();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
		GL11.glPopMatrix();
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glDepthMask(true);
	}
}
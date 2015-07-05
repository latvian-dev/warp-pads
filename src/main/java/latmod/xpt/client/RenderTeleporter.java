package latmod.xpt.client;

import java.awt.Color;
import java.util.Random;

import latmod.xpt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class RenderTeleporter extends TileEntitySpecialRenderer implements IItemRenderer
{
	public static final RenderTeleporter instance = new RenderTeleporter();
	public static final Random random = new Random();
	
	public void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float pt)
	{
		if(te == null || te.isInvalid() || !(te instanceof TileTeleporter)) return;
		TileTeleporter t = (TileTeleporter)te;
		
		int ID = t.getType();
		if(ID == 0) return;
		
		Minecraft mc = Minecraft.getMinecraft();
		//double dist = mc.thePlayer.getDistance(t.xCoord + 0.5D, t.yCoord + 0.125D, t.zCoord + 0.5D);
		double dx = t.xCoord + 0.5D - RenderManager.instance.viewerPosX; dx = dx * dx;
		double dy = t.yCoord + 0.125D - RenderManager.instance.viewerPosY; dy = dy * dy;
		double dz = t.zCoord + 0.5D - RenderManager.instance.viewerPosZ; dz = dz * dz;
		double dist = Math.sqrt(dx + dy + dz);
		if(dx <= 0D && dz <= 0D) return;
		
		float alpha = (float)getAlpha(dist);
		if(alpha <= 0F) return;
		if(alpha > 1F) alpha = 1F;
		
		//if(te.getWorldObj().rand.nextInt(100) > 97) return;
		
		double cooldown = (t.cooldown > 0) ? (1D - (t.cooldown / (double)t.maxCooldown)) : 1D;
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		float lastBrightnessX = OpenGlHelper.lastBrightnessX;
		float lastBrightnessY = OpenGlHelper.lastBrightnessY;
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GL11.glTranslated(rx + 0.5D, ry + 0.0D, rz + 0.5D);
		GL11.glScalef(-1F, -1F, 1F);
		GL11.glRotated(-Math.atan2((te.xCoord + 0.5D) - RenderManager.instance.viewerPosX, (te.zCoord + 0.5D) - RenderManager.instance.viewerPosZ) * 180D / Math.PI, 0D, 1D, 0D);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		if(ID == 3) GL11.glColor4f(1F, 0.9F, 0.1F, 0F);
		else GL11.glColor4f(0F, 1F, 1F, 0F);
		
		GL11.glBegin(GL11.GL_QUADS);
		double s = cooldown * 0.75D;
		double s1 = cooldown * 0.12D;
		GL11.glVertex3d(-s, -1.5D, 0D);
		GL11.glVertex3d(s, -1.5D, 0D);
		
		if(ID == 3) GL11.glColor4f(1F, 0.9F, 0.1F, alpha);
		else if(ID == 1) GL11.glColor4f(0.2F, 0.4F, 1F, alpha);
		else GL11.glColor4f(0.2F, 1F, 0.2F, alpha * 0.8F);
		
		GL11.glVertex3d(s1, -0.125D, 0D);
		GL11.glVertex3d(-s1, -0.125D, 0D);
		GL11.glEnd();
		
		if(t.cooldown > 0)
		{
			double b = 1D / 32D;
			
			GL11.glColor4f(86F / 255F, 218F / 255F, 1F, alpha * 0.3F);
			double w = 1D;
			double h = 1D / 4D;
			double x = -w / 2D;
			double y = -2D - h / 2D;
			
			drawRect(x, y, w, b / 2D, 0D);
			drawRect(x, y + h - b / 2D, w, b / 2D, 0D);
			drawRect(x, y + b / 2D, b / 2D, h - b, 0D);
			drawRect(x + w - b / 2D, y + b / 2D, b / 2D, h - b, 0D);
			
			GL11.glColor4f(0F, 1F, 0F, alpha * 0.25F);
			double w1 = w * cooldown;
			drawRect(x + b, y + b, w1 - b * 2D, h - b * 2D, 0D);
			
			GL11.glColor4f(1F, 0F, 0F, alpha * 0.25F);
			double w2 = w1 - b * 2D;
			drawRect(w2 + x + b, y + b, w - b * 2D - w2, h - b * 2D, 0D);
		}
		
		GL11.glDepthMask(true);
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		
		if(alpha > 0.05F && !t.name.isEmpty())
		{
			GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			GL11.glTranslated(rx + 0.5D, ry + 1.6D, rz + 0.5D);
			GL11.glNormal3f(0F, 1F, 0F);
			//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			float f1 = 0.02F;
			GL11.glScalef(-f1, -f1, f1);
			
			GL11.glRotated(-Math.atan2((te.xCoord + 0.5D) - RenderManager.instance.viewerPosX, (te.zCoord + 0.5D) - RenderManager.instance.viewerPosZ) * 180D / Math.PI, 0D, 1D, 0D);
			
			GL11.glColor4f(1F, 1F, 1F, 1F);
			mc.fontRenderer.drawString(t.name, -(mc.fontRenderer.getStringWidth(t.name) / 2), -8, new Color(255, 255, 255, (int)(alpha * 255F + 0.5F)).getRGB());
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
	}
	
	public void drawQuad(double x1, double y1, double x2, double y2, double z)
	{
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(x1, y1, z);
		GL11.glVertex3d(x2, y1, z);
		GL11.glVertex3d(x2, y2, z);
		GL11.glVertex3d(x1, y2, z);
		GL11.glEnd();
	}
	
	public void drawRect(double x, double y, double w, double h, double z)
	{ drawQuad(x, y, x + w, y + h, z); }
	
	public double getAlpha(double dist)
	{
		if(dist < 2D) return dist / 2D;
		double maxDist = 5D;
		if(dist <= maxDist) return 1F;
		if(dist > maxDist + 3D) return 0F;
		return (maxDist + 3D - dist) / (maxDist - 3D);
	}
	
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
		
		XPT.teleporter.setBlockBoundsForItemRender();
		rb.setRenderBoundsFromBlock(XPT.teleporter);
		rb.renderBlockAsItem(XPT.teleporter, 0, 1F);
		
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
		
		GL11.glColor4f(0F, 1F, 1F, 0F);
		GL11.glBegin(GL11.GL_QUADS);
		float s = 0.75F;
		float s1 = 0.12F;
		GL11.glVertex3d(-s, -1.5F, 0F);
		GL11.glVertex3d(s, -1.5F, 0F);
		
		GL11.glColor4f(0.2F, 0.4F, 1F, 1F);
		
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
package latmod.xpt.client;

import java.awt.Color;

import latmod.xpt.TileXPT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class RenderXPT extends TileEntitySpecialRenderer
{
	public void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float pt)
	{
		if(te == null || te.isInvalid() || !(te instanceof TileXPT)) return;
		TileXPT t = (TileXPT)te;
		
		int ID = t.getIconID();
		if(ID == 0) return;
		
		Minecraft mc = Minecraft.getMinecraft();
		double dist = mc.thePlayer.getDistance(t.xCoord + 0.5D, t.yCoord + 0.125D, t.zCoord + 0.5D);
		float alpha = (float)getAlpha(dist);
		if(alpha <= 0F) return;
		if(alpha > 1F) alpha = 1F;
		
		//if(te.getWorldObj().rand.nextInt(100) > 97) return;
		
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		GL11.glTranslated(rx + 0.5D, ry - 0.0D, rz + 0.5D);
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
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(0F, 1F, 1F, 0F);
		GL11.glVertex3d(-0.75D, -1.5D, 0D);
		GL11.glVertex3d(0.75D, -1.5D, 0D);
		if(ID == 1) GL11.glColor4f(86F / 255F, 218F / 255F, 1F, alpha);
		else GL11.glColor4f(48F / 255F, 1F, 113F / 255F, alpha);
		GL11.glVertex3d(0.10D, 0D, 0D);
		GL11.glVertex3d(-0.10D, 0D, 0D);
		GL11.glEnd();
		
		GL11.glDepthMask(true);
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		
		String s = (t.name + ((t.cooldown > 0) ? ("[ " + (int)(100D - t.cooldown * 100D / (double)t.maxCooldown) + "% ]") : "")).trim();
		
		if(alpha > 0.05F && !s.isEmpty())
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
			mc.fontRenderer.drawString(s, -(mc.fontRenderer.getStringWidth(s) / 2), -8, new Color(255, 255, 255, (int)(alpha * 255F + 0.5F)).getRGB());
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
		
		GL11.glColor4f(1F, 1F, 1F, 1F);
	}
	
	public double getAlpha(double dist)
	{
		if(dist < 2D) return dist / 2D;
		double maxDist = 5D;
		if(dist <= maxDist) return 1F;
		if(dist > maxDist + 3D) return 0F;
		return (maxDist + 3D - dist) / (maxDist - 3D);
	}
}
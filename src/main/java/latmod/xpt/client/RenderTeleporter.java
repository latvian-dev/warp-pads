package latmod.xpt.client;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.GlStateManager;
import latmod.lib.LMColorUtils;
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

@SideOnly(Side.CLIENT)
public class RenderTeleporter extends TileEntitySpecialRenderer implements IItemRenderer
{
	public static final RenderTeleporter instance = new RenderTeleporter();
	
	public void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float pt)
	{
		if(te == null || te.isInvalid() || !(te instanceof TileTeleporter)) return;
		TileTeleporter t = (TileTeleporter) te;
		
		int ID = t.getType();
		if(ID == 0) return;
		
		Minecraft mc = Minecraft.getMinecraft();
		//double dist = mc.thePlayer.getDistance(t.xCoord + 0.5D, t.yCoord + 0.125D, t.zCoord + 0.5D);
		double dx = t.xCoord + 0.5D - RenderManager.instance.viewerPosX;
		dx = dx * dx;
		double dy = t.yCoord + 0.125D - RenderManager.instance.viewerPosY;
		dy = dy * dy;
		double dz = t.zCoord + 0.5D - RenderManager.instance.viewerPosZ;
		dz = dz * dz;
		double dist = Math.sqrt(dx + dy + dz);
		if(dx <= 0D && dz <= 0D) return;
		
		float alpha = (float) getAlpha(dist);
		if(alpha <= 0F) return;
		if(alpha > 1F) alpha = 1F;
		
		//if(te.getWorldObj().rand.nextInt(100) > 97) return;
		
		double cooldown = (t.cooldown > 0) ? (1D - ((t.cooldown + (t.cooldown - t.pcooldown) * pt) / (double) XPTConfig.cooldownTicks())) : 1D;
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		float lastBrightnessX = OpenGlHelper.lastBrightnessX;
		float lastBrightnessY = OpenGlHelper.lastBrightnessY;
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GlStateManager.translate(rx + 0.5D, ry + 0.0D, rz + 0.5D);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate((float) (-Math.atan2((te.xCoord + 0.5D) - RenderManager.instance.viewerPosX, (te.zCoord + 0.5D) - RenderManager.instance.viewerPosZ) * 180D / Math.PI), 0F, 1F, 0F);
		
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		GlStateManager.color(0F, 1F, 1F, 0F);
		
		GL11.glBegin(GL11.GL_QUADS);
		double s = cooldown * 0.75D;
		double s1 = cooldown * 0.12D;
		GL11.glVertex3d(-s, -1.5D, 0D);
		GL11.glVertex3d(s, -1.5D, 0D);
		
		if(ID == 1) GlStateManager.color(0.2F, 0.4F, 1F, alpha);
		else GlStateManager.color(0.2F, 1F, 0.2F, alpha * 0.8F);
		
		GL11.glVertex3d(s1, -0.125D, 0D);
		GL11.glVertex3d(-s1, -0.125D, 0D);
		GL11.glEnd();
		
		if(t.cooldown > 0)
		{
			double b = 1D / 32D;
			
			if(ID == 3) GlStateManager.color(1F, 0.9F, 0.1F, alpha * 0.3F);
			else GlStateManager.color(86F / 255F, 218F / 255F, 1F, alpha * 0.3F);
			
			double w = 1D;
			double h = 1D / 4D;
			double x = -w / 2D;
			double y = -2D + h * 0.1D;
			
			drawRect(x, y, w, b / 2D, 0D);
			drawRect(x, y + h - b / 2D, w, b / 2D, 0D);
			drawRect(x, y + b / 2D, b / 2D, h - b, 0D);
			drawRect(x + w - b / 2D, y + b / 2D, b / 2D, h - b, 0D);
			
			GlStateManager.color(0F, 1F, 0F, alpha * 0.25F);
			double w1 = w * cooldown;
			drawRect(x + b, y + b, w1 - b * 2D, h - b * 2D, 0D);
			
			GlStateManager.color(1F, 0F, 0F, alpha * 0.25F);
			double w2 = w1 - b * 2D;
			drawRect(w2 + x + b, y + b, w - b * 2D - w2, h - b * 2D, 0D);
		}
		
		GlStateManager.depthMask(true);
		
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
		
		if(alpha > 0.05F && !t.name.isEmpty())
		{
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.translate(rx + 0.5D, ry + 1.6D, rz + 0.5D);
			GL11.glNormal3f(0F, 1F, 0F);
			//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.disableCull();
			GlStateManager.disableLighting();
			float f1 = 0.02F;
			GlStateManager.scale(-f1, -f1, f1);
			
			GlStateManager.rotate((float) (-Math.atan2((te.xCoord + 0.5D) - RenderManager.instance.viewerPosX, (te.zCoord + 0.5D) - RenderManager.instance.viewerPosZ) * 180D / Math.PI), 0F, 1F, 0F);
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			mc.fontRenderer.drawString(t.name, -(mc.fontRenderer.getStringWidth(t.name) / 2), -8, LMColorUtils.getRGBAF(1F, 1F, 1F, alpha));
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
		
		GlStateManager.color(1F, 1F, 1F, 1F);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
	}
	
	private void drawQuad(double x1, double y1, double x2, double y2, double z)
	{
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(x1, y1, z);
		GL11.glVertex3d(x2, y1, z);
		GL11.glVertex3d(x2, y2, z);
		GL11.glVertex3d(x1, y2, z);
		GL11.glEnd();
	}
	
	private void drawRect(double x, double y, double w, double h, double z)
	{ drawQuad(x, y, x + w, y + h, z); }
	
	private double getAlpha(double dist)
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
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		
		RenderBlocks rb = (RenderBlocks) data[0];
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		XPT.teleporter.setBlockBoundsForItemRender();
		rb.setRenderBoundsFromBlock(XPT.teleporter);
		rb.renderBlockAsItem(XPT.teleporter, 0, 1F);
		
		float lastBrightnessX = OpenGlHelper.lastBrightnessX;
		float lastBrightnessY = OpenGlHelper.lastBrightnessY;
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GlStateManager.translate(0F, -0.6F, 0F);
		float sc = 1F;
		GlStateManager.scale(-sc, -sc, sc);
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
		
		GlStateManager.color(0F, 1F, 1F, 0F);
		GL11.glBegin(GL11.GL_QUADS);
		float s = 0.75F;
		float s1 = 0.12F;
		GL11.glVertex3d(-s, -1.5F, 0F);
		GL11.glVertex3d(s, -1.5F, 0F);
		
		GlStateManager.color(0.2F, 0.4F, 1F, 1F);
		
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
package latmod.xpt.client;

import ftb.lib.api.client.*;
import latmod.lib.LMColorUtils;
import latmod.xpt.block.TileTeleporter;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.*;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTeleporter extends TileEntitySpecialRenderer<TileTeleporter>
{
	private static long debugTimer = 0L;
	
	@Override
	public void renderTileEntityAt(TileTeleporter te, double rx, double ry, double rz, float partialTicks, int destroyStage)
	{
		double tx = te.getPos().getX() + 0.5D;
		double tz = te.getPos().getZ() + 0.5D;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(rx + 0.5D, ry + 0.5D, rz + 0.5D);
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		//render
		
		float alpha = 1F;
		
		te.name = "XPT, FTBLib & FTBUtilities in 1.9 \\o/";
		
		if(alpha > 0.05F && !te.name.isEmpty())
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(rx + 0.5D, ry + 1.6D, rz + 0.5D);
			GL11.glNormal3f(0F, 1F, 0F);
			//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.enableTexture2D();
			float f1 = 0.02F;
			GlStateManager.scale(-f1, -f1, f1);
			
			GlStateManager.rotate((float) (-Math.atan2(tx - LMFrustrumUtils.playerX, tz - LMFrustrumUtils.playerZ) * 180D / Math.PI), 0F, 1F, 0F);
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			FTBLibClient.mc.fontRendererObj.drawString(te.name, -(FTBLibClient.mc.fontRendererObj.getStringWidth(te.name) / 2), -8, LMColorUtils.getRGBAF(1F, 1F, 1F, alpha));
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		
		/*
		double tx = t.getPos().getX() + 0.5D;
		double tz = t.getPos().getZ() + 0.5D;
		
		//double dist = mc.thePlayer.getDistance(t.xCoord + 0.5D, t.yCoord + 0.125D, t.zCoord + 0.5D);
		double dist = MathHelperLM.dist(tx, t.getPos().getY(), tz, LMFrustrumUtils.playerX, LMFrustrumUtils.playerY, LMFrustrumUtils.playerZ);
		if(dist <= 0D) return;
		
		float alpha = (float) getAlpha(dist);
		if(alpha <= 0F) return;
		if(alpha > 1F) alpha = 1F;
		
		//FTBLib.printChat(null, t.getPos() + ": " + pt);
		
		//if(te.getWorldObj().rand.nextInt(100) > 97) return;
		
		double cooldown = (t.cooldown > 0) ? (1D - ((t.cooldown + (t.cooldown - t.pcooldown) * pt) / (double) XPTConfig.cooldownTicks())) : 1D;
		
		GlStateManager.pushMatrix();
		
		FTBLibClient.pushMaxBrightness();
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		
		GlStateManager.translate(rx + 0.5D, ry + 0.0D, rz + 0.5D);
		GlStateManager.scale(-1F, -1F, 1F);
		
		//GlStateManager.rotate((float) (-Math.atan2(tx - LMFrustrumUtils.playerX, tz - LMFrustrumUtils.playerZ) * 180D / Math.PI), 0F, 1F, 0F);
		
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableAlpha();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		
		double s = cooldown * 0.75D;
		double s1 = cooldown * 0.12D;
		
		buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(-s, -1.5D, 0D).color(0F, 1F, 1F, 0F);
		buffer.pos(s, -1.5D, 0D).color(0F, 1F, 1F, 0F);
		buffer.pos(s1, -0.125D, 0D).color(0.2F, 0.4F, 1F, alpha);
		buffer.pos(-s1, -0.125D, 0D).color(0.2F, 0.4F, 1F, alpha);
		tessellator.draw();
		
		if(t.cooldown > 0)
		{
			double b = 1D / 32D;
			
			GlStateManager.color(86F / 255F, 218F / 255F, 1F, alpha * 0.3F);
			
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
		
		if(alpha > 0.05F && !t.name.isEmpty())
		{
			GlStateManager.pushMatrix();
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
			
			GlStateManager.rotate((float) (-Math.atan2(tx - LMFrustrumUtils.playerX, tz - LMFrustrumUtils.playerZ) * 180D / Math.PI), 0F, 1F, 0F);
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			FTBLibClient.mc.fontRendererObj.drawString(t.name, -(FTBLibClient.mc.fontRendererObj.getStringWidth(t.name) / 2), -8, LMColorUtils.getRGBAF(1F, 1F, 1F, alpha));
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableCull();
		GlStateManager.color(1F, 1F, 1F, 1F);
		FTBLibClient.popMaxBrightness();
		GlStateManager.enableTexture2D();
		
		*/
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
		if(dist < 2D) return dist / 2D;
		double maxDist = 5D;
		if(dist <= maxDist) return 1F;
		if(dist > maxDist + 3D) return 0F;
		return (maxDist + 3D - dist) / (maxDist - 3D);
	}
}
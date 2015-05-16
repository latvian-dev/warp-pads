package latmod.xpt.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class RenderXPT extends TileEntitySpecialRenderer
{
	public void renderTileEntityAt(TileEntity te, double rx, double ry, double rz, float pt)
	{
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
}
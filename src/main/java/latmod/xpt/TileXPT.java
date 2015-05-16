package latmod.xpt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileXPT extends TileEntity
{
	public int linkedX, linkedY, linkedZ, linkedDim;
	
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		int[] pos = tag.getIntArray("Pos");
		linkedX = pos[0];
		linkedY = pos[1];
		linkedZ = pos[2];
		linkedDim = pos[3];
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setIntArray("Pos", new int[] { linkedX, linkedY, linkedZ, linkedDim });
	}
}
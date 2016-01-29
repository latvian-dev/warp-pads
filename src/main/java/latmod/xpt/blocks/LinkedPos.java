package latmod.xpt.blocks;

import ftb.lib.EntityPos;
import net.minecraft.util.BlockPos;

/**
 * Created by LatvianModder on 27.01.2016.
 */
public class LinkedPos
{
	public BlockPos pos;
	public int dim;
	
	public LinkedPos(BlockPos p, int d)
	{
		set(p.getX(), p.getY(), p.getZ(), d);
	}
	
	public LinkedPos(int[] ai)
	{
		set(ai[0], ai[1], ai[2], ai[3]);
	}
	
	public void set(int x, int y, int z, int d)
	{
		pos = new BlockPos(x, y, z);
		dim = d;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Integer) return o.hashCode() == dim;
		else if(o instanceof BlockPos) return o.equals(pos);
		else return o.equals(dim) && o.equals(pos);
	}
	
	public int[] toArray()
	{ return new int[] {pos.getX(), pos.getY(), pos.getZ(), dim}; }
	
	public EntityPos entityPos()
	{ return new EntityPos(pos, dim); }
}

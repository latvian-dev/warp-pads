package latmod.xpt.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import latmod.xpt.XPTCommon;
import latmod.xpt.block.TileTeleporter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class XPTClient extends XPTCommon
{
    @Override
    public void load()
    {
        FTBLibClient.addTileRenderer(TileTeleporter.class, new RenderTeleporter());
    }
}
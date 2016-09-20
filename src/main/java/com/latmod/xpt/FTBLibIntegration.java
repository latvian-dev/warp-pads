package com.latmod.xpt;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.FTBLibAddon;
import com.feed_the_beast.ftbl.api.IFTBLibAddon;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 20.09.2016.
 */
@FTBLibAddon
public class FTBLibIntegration implements IFTBLibAddon
{
    public static FTBLibAPI API;

    @Override
    public void onFTBLibLoaded(FTBLibAPI api)
    {
        API = api;
        API.getRegistries().syncedData().register(new ResourceLocation(XPT.MOD_ID, "config"), new XPTSyncConfig());
        API.getRegistries().recipeHandlers().register(new ResourceLocation(XPT.MOD_ID, "blocks"), new XPTRecipes());
    }
}
package com.latmod.warp_pads.client;

import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.security.EnumPrivacyLevel;
import com.feed_the_beast.ftbl.lib.client.TextureCoords;
import com.feed_the_beast.ftbl.lib.gui.ButtonLM;
import com.feed_the_beast.ftbl.lib.gui.EnumDirection;
import com.feed_the_beast.ftbl.lib.gui.GuiHelper;
import com.feed_the_beast.ftbl.lib.gui.GuiIcons;
import com.feed_the_beast.ftbl.lib.gui.GuiLM;
import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.gui.SliderLM;
import com.feed_the_beast.ftbl.lib.gui.TextBoxLM;
import com.latmod.warp_pads.WarpPads;
import com.latmod.warp_pads.block.TileWarpPad;
import com.latmod.warp_pads.block.WarpPadNode;
import com.latmod.warp_pads.net.MessageSelectTeleporter;
import com.latmod.warp_pads.net.MessageSetName;
import com.latmod.warp_pads.net.MessageToggleActive;
import com.latmod.warp_pads.net.MessageTogglePrivacy;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 28.07.2016.
 */
@SideOnly(Side.CLIENT)
public class GuiWarpPad extends GuiLM
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(WarpPads.MOD_ID, "textures/gui/warp_pad.png");
    private static final TextureCoords BACKGROUND = TextureCoords.fromCoords(TEXTURE, 0, 0, 126, 110, 128, 128);
    private static final TextureCoords SLIDER_TEX = TextureCoords.fromCoords(TEXTURE, 0, 110, 6, 10, 128, 128);
    private static final TextureCoords AVAILABLE_ON = TextureCoords.fromCoords(TEXTURE, 6, 110, 7, 7, 128, 128);
    private static final TextureCoords AVAILABLE_OFF = TextureCoords.fromCoords(TEXTURE, 13, 110, 7, 7, 128, 128);
    private static final TextureCoords BAR_H = TextureCoords.fromCoords(TEXTURE, 24, 111, 104, 1, 128, 128);
    private static final TextureCoords BAR_V = TextureCoords.fromCoords(TEXTURE, 127, 0, 1, 81, 128, 128);

    private class ButtonXPT extends ButtonLM
    {
        private final WarpPadNode node;

        private ButtonXPT(WarpPadNode n)
        {
            super(6, 0, 104, 11);
            node = n;
        }

        @Override
        public void onClicked(IGui gui, IMouseButton button)
        {
            GuiHelper.playClickSound();

            if(node.available && button.isLeft())
            {
                new MessageSelectTeleporter(teleporter.getPos(), node.pos).sendToServer();
                gui.closeGui();
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            int ax = getAX();
            int ay = getAY();
            BAR_H.draw(ax, ay + getHeight(), 104, 1);
            (node.available ? AVAILABLE_ON : AVAILABLE_OFF).draw(ax, ay + 2, 7, 7);
            getFont().drawString(node.name, ax + 10, ay + 2, 0xFFFFFFFF, false);

            String lvls = Integer.toString(node.energy);

            getFont().drawString(lvls, ax + getWidth() - getFont().getStringWidth(lvls) - 2, ay + 2, node.available ? 0xFF56FF47 : 0xFFFF4646, false);
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    private final TileWarpPad teleporter;
    private final ButtonLM buttonPrivacy, buttonToggle;
    private final List<ButtonXPT> buttons;
    private final SliderLM slider;
    private final TextBoxLM textBox;

    public GuiWarpPad(TileWarpPad te, List<WarpPadNode> t)
    {
        super(126, 110);
        teleporter = te;
        buttons = new ArrayList<>();

        for(WarpPadNode n : t)
        {
            buttons.add(new ButtonXPT(n));
        }

        buttonPrivacy = new ButtonLM(105, 5, 16, 16, EnumPrivacyLevel.ENUM_LANG_KEY.translate())
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                new MessageTogglePrivacy(teleporter.getPos(), button.isLeft()).sendToServer();
            }
        };

        buttonToggle = new ButtonLM(87, 5, 16, 16)
        {
            @Override
            public void onClicked(IGui gui, IMouseButton button)
            {
                GuiHelper.playClickSound();
                new MessageToggleActive(teleporter.getPos()).sendToServer();
            }

            @Override
            public String getTitle(IGui gui)
            {
                return teleporter.inactive ? GuiLang.LABEL_DISABLED.translate() : GuiLang.LABEL_ENABLED.translate();
            }
        };

        slider = new SliderLM(114, 23, 6, 81, 10)
        {
            @Override
            public void onMoved(IGui gui)
            {
            }

            @Override
            public EnumDirection getDirection()
            {
                return EnumDirection.VERTICAL;
            }
        };

        slider.slider = SLIDER_TEX;

        textBox = new TextBoxLM(6, 6, 78, 14)
        {
            @Override
            public void onEnterPressed(IGui gui)
            {
                new MessageSetName(teleporter.getPos(), getText()).sendToServer();
            }
        };

        textBox.writeText(this, teleporter.getName());
        textBox.charLimit = 20;

        //width = 220D;
        //height = 180D;
    }

    @Override
    public void onInit()
    {
        int y = 23;

        for(ButtonXPT b : buttons)
        {
            b.posY = y;
            y += b.getHeight() + 1D;
        }
    }

    @Override
    public void addWidgets()
    {
        add(slider);
        add(buttonPrivacy);
        add(buttonToggle);
        addAll(buttons);
        add(textBox);
    }

    @Override
    public boolean isEnabled(IGui gui)
    {
        return !teleporter.isInvalid();
    }

    @Override
    public IDrawableObject getIcon(IGui gui)
    {
        return BACKGROUND;
    }

    @Override
    public void onClientDataChanged()
    {
        buttonPrivacy.setIcon(teleporter.getPrivacyLevel().getIcon());
        buttonToggle.setIcon(teleporter.inactive ? GuiIcons.ACCEPT_GRAY : GuiIcons.ACCEPT);
    }
}
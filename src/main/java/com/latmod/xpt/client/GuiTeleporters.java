package com.latmod.xpt.client;

import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.gui.GuiHelper;
import com.feed_the_beast.ftbl.api.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.gui.GuiLM;
import com.feed_the_beast.ftbl.api.gui.GuiLang;
import com.feed_the_beast.ftbl.api.gui.IGui;
import com.feed_the_beast.ftbl.api.gui.IMouseButton;
import com.feed_the_beast.ftbl.api.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.gui.widgets.EnumDirection;
import com.feed_the_beast.ftbl.api.gui.widgets.SliderLM;
import com.feed_the_beast.ftbl.api.gui.widgets.TextBoxLM;
import com.feed_the_beast.ftbl.api.security.EnumPrivacyLevel;
import com.latmod.lib.client.TextureCoords;
import com.latmod.xpt.XPT;
import com.latmod.xpt.block.TileTeleporter;
import com.latmod.xpt.block.XPTNode;
import com.latmod.xpt.net.MessageSelectTeleporter;
import com.latmod.xpt.net.MessageSetName;
import com.latmod.xpt.net.MessageToggleActive;
import com.latmod.xpt.net.MessageTogglePrivacy;
import net.minecraft.client.gui.GuiScreen;
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
public class GuiTeleporters extends GuiLM
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(XPT.MOD_ID, "textures/gui/teleporters.png");
    private static final TextureCoords SLIDER_TEX = TextureCoords.fromCoords(TEXTURE, 0, 110, 6, 10, 128, 128);
    private static final TextureCoords AVAILABLE_ON = TextureCoords.fromCoords(TEXTURE, 6, 110, 7, 7, 128, 128);
    private static final TextureCoords AVAILABLE_OFF = TextureCoords.fromCoords(TEXTURE, 13, 110, 7, 7, 128, 128);
    private static final TextureCoords BAR_H = TextureCoords.fromCoords(TEXTURE, 24, 111, 104, 1, 128, 128);
    private static final TextureCoords BAR_V = TextureCoords.fromCoords(TEXTURE, 127, 0, 1, 81, 128, 128);

    private class ButtonXPT extends ButtonLM
    {
        private final XPTNode node;

        private ButtonXPT(XPTNode n)
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
                new MessageSelectTeleporter(teleporter.getPos(), node.uuid).sendToServer();
                closeGui();
            }
        }

        @Override
        public void renderWidget(IGui gui)
        {
            int ax = getAX();
            int ay = getAY();
            GuiHelper.render(BAR_H, ax, ay + getHeight(), 104, 1);
            GuiHelper.render(node.available ? AVAILABLE_ON : AVAILABLE_OFF, ax, ay + 2, 7, 7);
            getFont().drawString(node.name, ax + 10, ay + 2, 0xFFFFFFFF, false);

            String lvls = Integer.toString(node.levels);

            getFont().drawString(lvls, ax + getWidth() - getFont().getStringWidth(lvls) - 2, ay + 2, node.available ? 0xFF56FF47 : 0xFFFF4646, false);
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    private final TileTeleporter teleporter;
    private final ButtonLM buttonPrivacy, buttonToggle;
    private final List<ButtonXPT> buttons;
    private final SliderLM slider;
    private final TextBoxLM textBox;

    public GuiTeleporters(TileTeleporter te, List<XPTNode> t)
    {
        super(126, 110);
        teleporter = te;
        buttons = new ArrayList<>();

        for(XPTNode n : t)
        {
            buttons.add(new ButtonXPT(n));
        }

        buttonPrivacy = new ButtonLM(105, 5, 16, 16, EnumPrivacyLevel.enumLangKey.translate())
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

        textBox = new TextBoxLM(6, 6, 78, 14)
        {
            @Override
            public void onEnterPressed(IGui gui)
            {
                new MessageSetName(teleporter.getPos(), getText()).sendToServer();
            }
        };

        textBox.setText(teleporter.getName());
        textBox.textRenderY = 3;
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
        add(buttonPrivacy);
        add(buttonToggle);
        addAll(buttons);
        add(slider);
        add(textBox);
    }

    @Override
    public void drawBackground()
    {
        if(teleporter.isInvalid())
        {
            closeGui();
            return;
        }

        slider.updateSlider(this);

        FTBLibClient.setTexture(TEXTURE);
        int ax = getAX();
        int ay = getAY();
        GuiScreen.drawModalRectWithCustomSizedTexture(ax, ay, 0F, 0F, getWidth(), getHeight(), 128F, 128F);
        super.drawBackground();

        buttonPrivacy.render(teleporter.security.getPrivacyLevel().getIcon());
        buttonToggle.render(teleporter.inactive ? GuiIcons.ACCEPT_GRAY : GuiIcons.ACCEPT);
        slider.renderSlider(SLIDER_TEX);
    }
}
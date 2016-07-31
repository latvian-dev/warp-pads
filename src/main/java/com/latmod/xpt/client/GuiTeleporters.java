package com.latmod.xpt.client;

import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiIcons;
import com.feed_the_beast.ftbl.api.client.gui.GuiLM;
import com.feed_the_beast.ftbl.api.client.gui.GuiLang;
import com.feed_the_beast.ftbl.api.client.gui.widgets.ButtonLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.SliderLM;
import com.feed_the_beast.ftbl.api.client.gui.widgets.TextBoxLM;
import com.feed_the_beast.ftbl.api.security.EnumPrivacyLevel;
import com.feed_the_beast.ftbl.util.TextureCoords;
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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 28.07.2016.
 */
@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class GuiTeleporters extends GuiLM
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(XPT.MOD_ID, "textures/gui/teleporters.png");
    public static final TextureCoords SLIDER_TEX = new TextureCoords(TEXTURE, 0, 110, 6, 10, 128, 128);
    public static final TextureCoords AVAILABLE_ON = new TextureCoords(TEXTURE, 6, 110, 7, 7, 128, 128);
    public static final TextureCoords AVAILABLE_OFF = new TextureCoords(TEXTURE, 13, 110, 7, 7, 128, 128);
    public static final TextureCoords BAR_H = new TextureCoords(TEXTURE, 24, 111, 104, 1, 128, 128);
    public static final TextureCoords BAR_V = new TextureCoords(TEXTURE, 127, 0, 1, 81, 128, 128);

    public class ButtonXPT extends ButtonLM
    {
        public final XPTNode node;

        public ButtonXPT(XPTNode n)
        {
            super(6, 0, 104, 11);
            node = n;
        }

        @Override
        public void onClicked(GuiLM gui, MouseButton button)
        {
            playClickSound();

            if(node.available && button.isLeft())
            {
                new MessageSelectTeleporter(teleporter.getPos(), node.uuid).sendToServer();
                closeGui();
            }
        }

        @Override
        public void renderWidget(GuiLM gui)
        {
            double ax = getAX();
            double ay = getAY();
            GuiLM.render(BAR_H, ax, ay + height, 104, 1);
            GuiLM.render(node.available ? AVAILABLE_ON : AVAILABLE_OFF, ax, ay + 2, 7, 7);
            font.drawString(node.name, (int) ax + 10, (int) ay + 2, 0xFFFFFFFF, false);

            String lvls = Integer.toString(node.levels);

            font.drawString(lvls, (int) (ax + width) - font.getStringWidth(lvls) - 2, (int) ay + 2, node.available ? 0xFF56FF47 : 0xFFFF4646, false);
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    public final TileTeleporter teleporter;
    public final ButtonLM buttonPrivacy, buttonToggle;
    public final List<ButtonXPT> buttons;
    public final SliderLM slider;
    public final TextBoxLM textBox;
    private Boolean lastInactive;

    public GuiTeleporters(TileTeleporter te, List<XPTNode> t)
    {
        teleporter = te;
        buttons = new ArrayList<>();
        width = 126;
        height = 110;

        for(XPTNode n : t)
        {
            buttons.add(new ButtonXPT(n));
        }

        buttonPrivacy = new ButtonLM(105, 5, 16, 16)
        {
            @Override
            public void onClicked(GuiLM gui, MouseButton button)
            {
                playClickSound();
                new MessageTogglePrivacy(teleporter.getPos(), button.isLeft()).sendToServer();
            }
        };

        buttonPrivacy.title = EnumPrivacyLevel.enumLangKey.translate();

        buttonToggle = new ButtonLM(87, 5, 16, 16)
        {
            @Override
            public void onClicked(GuiLM gui, MouseButton button)
            {
                playClickSound();
                new MessageToggleActive(teleporter.getPos()).sendToServer();
            }
        };

        slider = new SliderLM(114, 23, 6, 81, 10)
        {
            @Override
            public void onMoved(GuiLM gui)
            {
            }
        };

        slider.isVertical = true;

        textBox = new TextBoxLM(6, 6, 78, 14)
        {
            @Override
            public void onEnterPressed(GuiLM gui)
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
        double y = 23D;

        for(ButtonXPT b : buttons)
        {
            b.posY = y;
            y += b.height + 1D;
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

        if(lastInactive == null || lastInactive != teleporter.inactive)
        {
            lastInactive = teleporter.inactive;
            buttonToggle.title = teleporter.inactive ? GuiLang.label_disabled.translate() : GuiLang.label_enabled.translate();
        }

        slider.update(this);

        FTBLibClient.setTexture(TEXTURE);
        int ax = (int) getAX();
        int ay = (int) getAY();
        GuiScreen.drawModalRectWithCustomSizedTexture(ax, ay, 0F, 0F, (int) width, (int) height, 128F, 128F);
        super.drawBackground();

        buttonPrivacy.render(teleporter.security.getPrivacyLevel().getIcon());
        buttonToggle.render(teleporter.inactive ? GuiIcons.accept_gray : GuiIcons.accept);
        slider.renderSlider(SLIDER_TEX);
    }
}
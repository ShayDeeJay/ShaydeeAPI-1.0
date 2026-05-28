package org.shaydee.shaydeeapi.helpers

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

public object ClientHelpers {

    @JvmStatic
    public fun getMinecraft(): Minecraft = Minecraft.getInstance()

    @JvmStatic
    public fun GuiGraphics.centerX(): Int = this.guiWidth() / 2

    @JvmStatic
    public fun GuiGraphics.centerY(): Int = this.guiHeight() / 2

    @JvmStatic
    public fun isKeyDown(key: Int): Boolean =
        InputConstants.isKeyDown(getMinecraft().window.window, key)

    @JvmStatic
    public fun fadeBlack(alpha: Float): Int =
        getMinecraft().options.getBackgroundColor(alpha)

    @JvmStatic
    public fun GuiGraphics.displayString(
        component: Component = Component.empty(),
        x: Int = 0,
        y: Int = 0,
        colour: Int = -1
    ) {
        this.drawString(getMinecraft().font, component, x, y, colour)
    }

    @JvmStatic
    public fun GuiGraphics.boxMaker(
        startX: Int,
        startY: Int,
        widthOffset: Int,
        heightOffset: Int,
        colourBorder: Int = 0,
        fillColour: Int = -1,
    ) {
        val widthTo = startX + widthOffset * 2
        val heightTo = startY + heightOffset * 2

        fill(startX, startY, widthTo, heightTo, fillColour)
        renderOutline(startX, startY, widthTo - startX, heightTo - startY, colourBorder)
    }

    @JvmStatic
    public fun GuiGraphics.boxMaker(
        startX: Int,
        startY: Int,
        widthOffset: Int,
        heightOffset: Int,
        colourBorder: Int = 0,
        start: Int = -1,
        end: Int = -2,
    ) {
        val widthTo = startX + widthOffset * 2
        val heightTo = startY + heightOffset * 2

        fillGradient(startX, startY, widthTo, heightTo, start, end)
        renderOutline(startX, startY, widthTo - startX, heightTo - startY, colourBorder)
    }

    public fun GuiGraphics.alphaWrapper(
        colour: Int = -1,
        alpha: Float = 1F,
        func: (GuiGraphics) -> Unit,
    ){
        val r = ((colour shr 16) and 0xFF) / 255f
        val g = ((colour shr 8) and 0xFF) / 255f
        val b = (colour and 0xFF) / 255f
        this.pose().pushPose()
        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(r, g, b, alpha)
        func.invoke(this)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableBlend()
        this.pose().popPose()
    }

    @JvmStatic
    public fun GuiGraphics.drawStringWithBackground(
        pX: Int,
        pY: Int,
        pText: Component = Component.empty(),
        textColour: Int = -1,
        backgroundColour: Int = 1,
        isCentered: Boolean = true,
        pFont: Font = getMinecraft().font,
    ) {
        pose().pushPose()
        val sequence = pText.visualOrderText
        val s = pText.string
        val i1 = if (isCentered) pX - pFont.width(sequence) / 2 else pX
        drawString(pFont, s, i1 + 1, pY, backgroundColour, false)
        drawString(pFont, s, i1 - 1, pY, backgroundColour, false)
        drawString(pFont, s, i1, pY + 1, backgroundColour, false)
        drawString(pFont, s, i1, pY - 1, backgroundColour, false)
        drawString(pFont, s, i1, pY, textColour, false)
        pose().popPose()
    }

}
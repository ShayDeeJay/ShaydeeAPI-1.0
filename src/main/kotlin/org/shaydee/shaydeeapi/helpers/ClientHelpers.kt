package org.shaydee.shaydeeapi.helpers

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagEntry.element
import net.minecraft.world.level.levelgen.Column.line
import org.lwjgl.glfw.GLFW
import org.shaydee.shaydeeapi.client.Icons
import java.util.Optional
import kotlin.math.roundToInt

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
    public fun isMousePressed(mc: Minecraft, key: Int): Boolean {
        val handle = mc.window.window
        return GLFW.glfwGetMouseButton(handle,  key) == GLFW.GLFW_PRESS
    }

    @JvmStatic
    public fun GuiGraphics.icon(
        resourceLocation: ResourceLocation,
        size: Int = 16,
        posX: Int = centerX() - (size / 2),
        posY: Int = centerY() - (size / 2),
    ): Unit = this.blit(resourceLocation, posX, posY, 0F, 0F, size, size, size, size)

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
    public fun GuiGraphics.refinedTooltip(
        posX: Int,
        posY: Int,
        components: MutableList<Component>,
        font: Font = getMinecraft().font,
    ) {
        renderTooltip(font, components, Optional.empty(), posX, posY)
    }

    @JvmStatic
    public fun GuiGraphics.drawCenterComponent(string: Component, posX: Int, posY: Int, textColour: Int) {
        val font = Minecraft.getInstance().font
        drawCenteredString(font, string, posX, posY, textColour)
    }

    public fun GuiGraphics.stringWithBackground(
        alpha: Float,
        name: Component,
        x: Int,
        y: Int,
        borderColour: Int,
        textColour: Int = -1,
        fillColour: Int = fadeBlack(0.6F),
        width: Int = Minecraft.getInstance().font.width(name.string)/2 + 4,
        center: Boolean = true
    ) {
        pose().pushPose()
        alphaWrapper(alpha = alpha) {
            if(center){
                drawCenterComponent(name, x, y, textColour)
                boxMaker(x - width, y - 4, width, 8, borderColour, fillColour)
            } else {
                drawStringWithBackground(x + 4, y, name, textColour, isCentered = false)
                boxMaker(x , y - 4, width, 8, borderColour, fillColour)
            }
        }
        pose().popPose()
    }

    @JvmStatic
    @JvmOverloads
    public fun GuiGraphics.refinedTooltip(
        posX: Int,
        posY: Int,
        vararg components: Component,
        font: Font = Minecraft.getInstance().font,
    ) {
        renderTooltip(font, components.toList(), Optional.empty(), posX, posY)
    }

    @JvmStatic
    public fun GuiGraphics.vLineRenderer(startX: Int, startY: Int, height: Int, colour: Int): Unit =
        vLine(startX, startY, startY + height, colour)

    @JvmStatic
    public fun GuiGraphics.hLineRenderer(startX: Int, startY: Int, width: Int, colour: Int): Unit =
        hLine(startX, startX + width, startY, colour)

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
    public fun GuiGraphics.customisableIcon(
        icon: ResourceLocation,
        startX: Int,
        startY: Int,
        colour: Int = -1,
        size: Int = 16,
        rotation: Float = 0F,
        alpha: Float = 1F,
    ) {
        val pose = pose()
        pose.pushPose()
        pose.translate(startX.toDouble(), startY.toDouble(), 0.0) // this is your pivot
        pose.mulPose(Axis.ZP.rotationDegrees(rotation))
        val center = size/2
        alphaWrapper(colour, alpha){ icon(icon, size, -center, -center) }
        pose.popPose()
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

    @JvmStatic
    public fun GuiGraphics.progressBar(
        startX: Int,
        startY: Int,
        width: Int,
        height: Int,
        offset: Int,
        current: Int,
        needed: Int,
        barColour: Int,
        containerBorder: Int,
        backgroundColour: Int,
        alpha: Float = 1F
    ) {
        alphaWrapper(alpha = alpha) {
            boxMaker(startX, startY, width, height, containerBorder, backgroundColour, backgroundColour)
            if (current > 0) {
                val progressRatio = minOf(current, needed).toFloat() / needed
                val barWidth = ((width - offset) * progressRatio).roundToInt()
                boxMaker(startX + offset, startY + offset, maxOf(barWidth, 1), height - offset, barColour, barColour, barColour)
            }
        }
    }

    public fun GuiGraphics.bezelMaker(
        posX: Int,
        posY: Int,
        offsetX: Int,
        offsetY: Int
    ) {
        fun bezel(
            adjustX: Int = 0,
            adjustY: Int = 0,
            startY: Float = 0F,
        ) = blit(Icons.BEZEL.icon(), posX + adjustX, posY + adjustY, 0F, startY, 17, 17, 17, 68)
        bezel()
        bezel(offsetX, startY = 17F)
        bezel(adjustY = offsetY, startY = 34F)
        bezel(offsetX, offsetY, 51F)
    }

}
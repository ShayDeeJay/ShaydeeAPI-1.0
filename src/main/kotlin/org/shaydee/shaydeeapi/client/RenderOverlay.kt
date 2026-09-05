package org.shaydee.shaydeeapi.client

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Overlay

public class RenderOverlay(public val render: (gui: GuiGraphics, mouseX: Int, mouseY: Int, partial: Float) -> Unit) : Overlay() {

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float, ): Unit =
        render.invoke(guiGraphics, mouseX, mouseY, partialTick)

}
package org.shaydee.shaydeeapi.client

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.shaydee.shaydeeapi.helpers.ClientHelpers.boxMaker
import org.shaydee.shaydeeapi.helpers.ColourHelpers

public abstract class AbstractScreen : Screen(Component.empty()) {

    public fun renderable(renderable: (gui: GuiGraphics, mouseX: Int, mouseY: Int, partial: Float) -> Unit) {
        addRenderableOnly(
            RenderOverlay { graphics, mouseX, mouseY, partial ->
                renderable.invoke(graphics, mouseX, mouseY, partial)
            }
        )
    }

    public fun renderableOnly(onRender: (p0: GuiGraphics, p1: Int, p2: Int, p3: Float) -> Unit) {
        addRenderableOnly(
            object : Renderable {
                override fun render(p0: GuiGraphics, p1: Int, p2: Int, p3: Float) = onRender(p0, p1, p2, p3)
            }
        )
    }

}
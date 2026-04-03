package org.shaydee.shaydeeapi.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import kotlin.math.max
import kotlin.math.min

public abstract class AbstractTimedOverlay(
    public var timer: Int = 0,
    public var fadeIn: Float = 0f,
    public var maxFadeIn: Float = 10.0f,
    public var minFadeIn: Float = -240.0f,
    public var easeFactor: Float = 0.15f
) : LayeredDraw.Layer {

    public fun slideGui() {
        if (timer > 0) {
            val distanceToMax = maxFadeIn - this.fadeIn
            val fadeAmount = distanceToMax * easeFactor
            this.fadeIn = min(this.fadeIn + fadeAmount, maxFadeIn)
        } else {
            val distanceFromMin = this.fadeIn - minFadeIn
            val fadeAmount = distanceFromMin * easeFactor
            this.fadeIn = max(this.fadeIn - fadeAmount, minFadeIn)
        }
    }

    override fun render(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        slideGui()
        timer = max(0, timer - 1)
    }
}
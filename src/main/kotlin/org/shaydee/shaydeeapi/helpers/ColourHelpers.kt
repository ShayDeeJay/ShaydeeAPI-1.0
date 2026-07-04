package org.shaydee.shaydeeapi.helpers

import net.minecraft.util.FastColor
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

public object ColourHelpers {
    @JvmStatic public var headerColour: Int = -9013642
        private set

    @JvmStatic public var offWhite: Int = -1381654
        private set

    @JvmStatic public var experienceGreen: Int = 8453920
        private set

    @JvmStatic public var boxColour: Int = -804253680
        private set

    @JvmStatic public var borderColour: Int = -12434878
        private set

    @JvmStatic public var subHeaderColour: Int = FastColor.ARGB32.color(198, 198, 198)
        private set

    @JvmStatic public var aetherBlue: Int = FastColor.ARGB32.color(87, 180, 250)
        private set

    @JvmStatic public var cosmicPurple: Int = FastColor.ARGB32.color(171, 87, 194)
        private set

    @JvmStatic public var perkGreen: Int = FastColor.ARGB32.color(193, 255, 99)
        private set

    @JvmStatic public var sympathiserOrange: Int = FastColor.ARGB32.color(255, 153, 77)
        private set

    @JvmStatic public var pendentName: Int = FastColor.ARGB32.color(91, 174, 252)
        private set

    @JvmStatic public var magnetRangeGreen: Int = FastColor.ARGB32.color(209, 227, 163)
        private set

    @JvmStatic public var magnetStrengthRed: Int = FastColor.ARGB32.color(227, 163, 174)
        private set

    @JvmStatic public var absorptionTextYellow: Int = FastColor.ARGB32.color(227, 222, 163)
        private set

    @JvmStatic public var championGold: Int = FastColor.ARGB32.color(255, 215, 0)
        private set

    @JvmStatic public var negativeRed: Int = FastColor.ARGB32.color(235, 88, 77)
        private set

    @JvmStatic public var absorptionYellow: Int = FastColor.ARGB32.color(224, 219, 88)
        private set

    @JvmStatic public var uniqueA: Int = FastColor.ARGB32.color(166, 255, 125)
        private set

    @JvmStatic public var uniqueB: Int = FastColor.ARGB32.color(104, 243, 252)
        private set

    @JvmStatic public var bronzeCoin: Int = FastColor.ARGB32.color(193, 108, 51)
        private set

    @JvmStatic public var silverCoin: Int = FastColor.ARGB32.color(191, 192, 192)
        private set

    @JvmStatic public var goldCoin: Int = FastColor.ARGB32.color(225, 155, 50)
        private set

    @JvmStatic public var platinumCoin: Int = FastColor.ARGB32.color(228, 228, 228)
        private set

    @JvmStatic public var diamondBox: Int = FastColor.ARGB32.color(135, 222, 222)
        private set

    @JvmStatic public var netheriteBox: Int = FastColor.ARGB32.color(107, 95, 96)
        private set

    @JvmStatic public var cooldownGreen: Int = FastColor.ARGB32.color(66, 245, 197)
        private set

    @JvmStatic public var walletBrown: Int = FastColor.ARGB32.color(143, 72, 20)
        private set

    @JvmStatic public var rating1Gray: Int = FastColor.ARGB32.color(211, 211, 211)
        private set

    @JvmStatic public var rating2Red: Int = FastColor.ARGB32.color(230, 71, 71)
        private set

    @JvmStatic public var rating3Orange: Int = FastColor.ARGB32.color(224, 156, 59)
        private set

    @JvmStatic public var rating4Yellow: Int = FastColor.ARGB32.color(230, 226, 46)
        private set

    @JvmStatic public var rating5Green: Int = FastColor.ARGB32.color(143, 185, 53)
        private set

    @JvmStatic
    public fun colourByPercentRanged(targetNumber: Int, currentNumber: Int): Int {
        val percent = currentNumber.toFloat() / targetNumber.toFloat()
        return when {
            percent <= 0.1f -> offWhite
            percent <= 0.3f -> rating5Green
            percent <= 0.6f -> rating4Yellow
            percent <= 0.9f -> rating3Orange
            else -> rating2Red
        }
    }

    @JvmStatic
    public fun colourByPercent(targetNumber: Int, currentNumber: Int, reversed: Boolean): Int {
        val split = targetNumber / 3
        return when {
            currentNumber <= split -> if (reversed) negativeRed else perkGreen
            currentNumber <= split * 2.5 -> absorptionYellow else -> if (reversed) perkGreen else negativeRed
        }
    }

    @JvmStatic
    public fun getRgb(): Int = Color((Math.random() * 0x1000000).toInt()).rgb

    @JvmStatic
    public fun getCyclicColorVariant(baseColor: Int, ticker: Int, range: Double, transitionDelay: Double): Color {
        val red = (baseColor shr 16) and 0xFF
        val green = (baseColor shr 8) and 0xFF
        val blue = baseColor and 0xFF

        val phase = sin(ticker / transitionDelay)
        val variantRed = min(max(red + phase * range, 0.0), 255.0).toInt()
        val variantGreen = min(max(green + phase * range, 0.0), 255.0).toInt()
        val variantBlue = min(max(blue + phase * range, 0.0), 255.0).toInt()

        return Color(variantRed, variantGreen, variantBlue)
    }

    @JvmStatic
    public fun getColourDarker(color: Int, darkValue: Double): Int {
        // Extract ARGB components from the integer
        val alpha = (color shr 24) and 0xFF
        var red = (color shr 16) and 0xFF
        var green = (color shr 8) and 0xFF
        var blue = color and 0xFF

        red = min((red / darkValue).toInt(), 255)
        green = min((green / darkValue).toInt(), 255)
        blue = min((blue / darkValue).toInt(), 255)

        // Combine the components back into an integer
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }

    @JvmStatic
    public fun getColourLight(color: Int, lightValue: Double): Int {
        // Extract ARGB components from the integer
        val alpha = (color shr 24) and 0xFF
        var red = (color shr 16) and 0xFF
        var green = (color shr 8) and 0xFF
        var blue = color and 0xFF

        red = min((red * lightValue).toInt(), 255)
        green = min((green * lightValue).toInt(), 255)
        blue = min((blue * lightValue).toInt(), 255)

        // Combine the components back into an integer
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }

    @JvmStatic
        public fun getColorTransition(
        startColor: Int,
        endColor: Int,
        ticker: Int,
        transitionDelay: Double
    ): Int {
        fun lerp(a: Int, b: Int, t: Double): Int = (a + (b - a) * t).toInt().coerceIn(0, 255)

        val progress = 0.5 * (1.0 + sin(2 * Math.PI * ticker / transitionDelay))

        val sr = (startColor shr 16) and 0xFF
        val sg = (startColor shr 8) and 0xFF
        val sb = startColor and 0xFF

        val er = (endColor shr 16) and 0xFF
        val eg = (endColor shr 8) and 0xFF
        val eb = endColor and 0xFF

        val r = lerp(sr, er, progress)
        val g = lerp(sg, eg, progress)
        val b = lerp(sb, eb, progress)

        return (r shl 16) or (g shl 8) or b
    }

}
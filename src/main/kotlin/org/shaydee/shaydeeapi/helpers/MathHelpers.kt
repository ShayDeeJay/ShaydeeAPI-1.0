package org.shaydee.shaydeeapi.helpers

import java.text.DecimalFormat
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.text.iterator

public object MathHelpers {

    @JvmStatic
    public fun nextFloat(a: Double, b: Double): Float {
        return Random.nextDouble(a, b).toFloat()
    }

    @JvmStatic
    public var FORMAT: DecimalFormat = DecimalFormat("#.##")

    @JvmStatic
    public fun getPercentage(multiplier: Double, baseValue: Double): Double {
        return (multiplier * baseValue) / 100
    }

    @JvmStatic
    public fun getPercentageTotal(multiplier: Double, baseValue: Double): Double {
        return baseValue + (multiplier * baseValue) / 100
    }

    @JvmStatic
    public fun getFormattedFloat(value: Float): Float {
        return FORMAT.format(value.toDouble()).toFloat()
    }

    @JvmStatic
    public fun singleFormattedDouble(value: Double): Double {
        val decimalFormat = DecimalFormat("#.#")
        return roundNonWholeDouble(decimalFormat.format(value).toDouble())
    }

    @JvmStatic
    public fun doubleFormattedDouble(value: Double): Double {
        return roundNonWholeDouble(FORMAT.format(value).toDouble())
    }

    @JvmStatic
    public fun tripleFormattedDouble(value: Double): Double {
        val decimalFormat = DecimalFormat("#.###")
        return roundNonWholeDouble(decimalFormat.format(value).toDouble())
    }

    @JvmStatic
    public fun Double.roundToString(): String {
        return roundNonWholeString(this)
    }

    @JvmStatic
    public fun Double.formatSingleString(): String {
        return roundNonWholeString(singleFormattedDouble(this))
    }

    @JvmStatic
    public fun Double.formatDoubleString(): String {
        return roundNonWholeString(doubleFormattedDouble(this))
    }

    @JvmStatic
    public fun Double.singleAndRound(): String {
        return roundNonWholeString(singleFormattedDouble(this))
    }

    @JvmStatic
    public fun Float.singleAndRound(): String {
        return roundNonWholeString(singleFormattedDouble(this.toDouble()))
    }

    @JvmStatic
    public fun Number.roundToNearest(factor: Double): Double =
        round(this.toDouble() / factor) * factor

    @JvmStatic
    public fun roundNonWholeDouble(number: Double): Double {
        val decimalPart = number - number.toInt()
        if (decimalPart == 0.0) return number.roundToInt().toDouble()
        return number
    }

    @JvmStatic
    public fun roundNonWholeString(number: Double): String {
        val decimalPart = number - number.toInt()
        if (decimalPart == 0.0) return number.roundToInt().toString()
        return number.toString()
    }

    @JvmStatic
    public fun percentageChance(percentageChance: Int): Boolean {
        if (percentageChance == 0) return false
        require(percentageChance in 0..100) { "Percentage chance must be between 0 and 100." }

        val randomValue = Random.nextInt(100) + 1
        return randomValue <= percentageChance
    }

    @JvmStatic
    public fun percentageChance(percentageChance: Number, doOnChance : () -> Unit) {
        val chance = percentageChance.toDouble()

        if (chance <= 0.0) return
        require(chance <= 100.0) { "Percentage chance must be between 0 and 100." }
        
        if ( Random.nextDouble(0.0, 100.0) < chance) doOnChance()
    }

    @JvmStatic
    public fun percentageChance(percentageChance: Double): Boolean {
        if (percentageChance <= 0) return false
        require(percentageChance <= 100) { "Percentage chance must be between 0 and 100." }

        val randomValue = Random.nextDouble(100.0) + 1
        return randomValue <= percentageChance
    }

    @JvmStatic
    public fun percentageChance(percentageChance: Int, seed: Long): Boolean {
        if (percentageChance == 0) return false
        require(percentageChance in 0..100) { "Percentage chance must be between 0 and 100." }

        val randomValue = Random(seed).nextInt(100) + 1
        return randomValue <= percentageChance
    }

    @JvmStatic
    public fun processNumber(number: String): String {
        return try {
            val num = number.toDouble()
            val decimalPart = num - num.toInt()
            if (decimalPart == 0.0) num.roundToInt().toString() else num.toString()
        } catch (e: NumberFormatException) {
            number
        }
    }

    @JvmStatic
    public fun toPercent(max: Double): Double {
        if (max == 0.0) return 100.0
        if (max <= 0) return 0.0
        return FORMAT.format((1.0 / max) * 100).toDouble()
    }

    @JvmStatic
    public fun Int.toRoman(): String {
        if (this <= 0) return "0"
        val map = linkedMapOf(
            50 to "L", 40 to "XL", 10 to "X", 9 to "IX",
            5 to "V", 4 to "IV", 1 to "I"
        )
        var num = this
        val res = StringBuilder()
        for ((value, roman) in map) {
            while (num >= value) {
                res.append(roman)
                num -= value
            }
        }
        return res.toString()
    }

    @JvmStatic
    public fun Double.ticksToTime(): String {
        val duration = (this / 20).toInt()
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        val builder = StringBuilder()
        if (hours > 0) builder.append(hours).append("h ")
        if (minutes > 0) builder.append(minutes).append("m ")
        if (seconds > 0 || builder.isEmpty()) builder.append(seconds).append("s")

        return builder.toString().trim()
    }

    @JvmStatic
    @JvmName("tickswithmili")
    public fun Double.ticksToTime(showMilliseconds: Boolean): String {
        val totalSeconds = this / 20.0

        if (totalSeconds < 1.0) {
            return if (showMilliseconds && totalSeconds > 0)
                "%.2fs".format(java.util.Locale.US, totalSeconds) else "0s"
        }

        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")

            // Append seconds if they exist, or if the string is empty
            if (seconds > 0 || isEmpty()) append("${seconds}s")
        }.trim()
    }

    @JvmStatic
    public fun ticksToTime(current: String): String {
        val duration = (current.toDouble() / 20).toInt()
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        val builder = StringBuilder()
        if (hours > 0) builder.append(hours).append("h ")
        if (minutes > 0) builder.append(minutes).append("m ")
        if (seconds > 0 || builder.isEmpty()) builder.append(seconds).append("s")

        return builder.toString().trim()
    }

    @JvmStatic
    public fun ticksToTime(current: String, showMilliseconds: Boolean): String {
        val totalSeconds = current.toDouble() / 20.0
        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = totalSeconds.toInt()
        val milliseconds = ((totalSeconds - seconds) * 1000).toInt()

        val builder = StringBuilder()
        if (hours > 0) builder.append(hours).append("h ")
        if (minutes > 0) builder.append(minutes).append("m ")

        if (minutes == 0) {
            if (showMilliseconds) {
                val secWithMs = seconds + (milliseconds / 1000.0)
                builder.append(String.format("%.3fs", secWithMs))
            } else {
                builder.append(seconds).append("s")
            }
        }

        return builder.toString().trim()
    }

    @JvmStatic
    public fun roundNonWholeString(input: String): String {
        val result = StringBuilder()
        val numberBuffer = StringBuilder()

        for (c in input) {
            if (c.isDigit() || c == '.') {
                numberBuffer.append(c)
            } else {
                if (numberBuffer.isNotEmpty()) {
                    result.append(processNumber(numberBuffer.toString()))
                    numberBuffer.setLength(0)
                }
                result.append(c)
            }
        }

        if (numberBuffer.isNotEmpty()) {
            result.append(processNumber(numberBuffer.toString()))
        }

        return result.toString()
    }
}
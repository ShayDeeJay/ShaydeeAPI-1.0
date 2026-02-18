package org.shaydee.shaydeeapi.helpers

import java.text.DecimalFormat
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.text.iterator

public object MathHelpers {

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

        val randomValue = Random.Default.nextInt(100) + 1
        return randomValue <= percentageChance
    }

    @JvmStatic
    public fun percentageChance(percentageChance: Double): Boolean {
        if (percentageChance <= 0) return false
        require(percentageChance <= 100) { "Percentage chance must be between 0 and 100." }

        val randomValue = Random.Default.nextDouble(100.0) + 1
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
package org.shaydee.shaydeeapi.helpers

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.Util.prefix
import net.minecraft.client.StringSplitter
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.player.Player
import org.apache.logging.log4j.core.tools.picocli.CommandLine
import org.openjdk.nashorn.tools.ShellFunctions.input
import org.shaydee.shaydeeapi.helpers.ColourHelpers.getRgb
import org.shaydee.shaydeeapi.helpers.ColourHelpers.headerColour
import org.shaydee.shaydeeapi.helpers.ColourHelpers.subHeaderColour
import org.shaydee.shaydeeapi.helpers.TextHelpers.withStyleComponentTrans
import java.util.*

public object TextHelpers {

    @JvmStatic
    public fun MutableList<Component>.spacer(): Boolean = this.add(Component.literal(" "))

    @JvmStatic
    public fun MutableComponent.spacer(): MutableComponent = this.append(" ")

    @JvmStatic
    public fun String.translatable(): String = Component.translatable(this).string

    @JvmStatic
    public fun Boolean.onTrue(doOnTrue: (Boolean) -> Unit) { if (this) doOnTrue.invoke(true) }

    @JvmStatic
    public fun Boolean.onFalse(doOnTrue: (Boolean) -> Unit) { if (!this) doOnTrue.invoke(false) }

    @JvmStatic
    public fun nameToId(input: String): String =
        input.split(" ").joinToString("_") { it.lowercase() }

    @JvmStatic
    @JvmName("nameToIdExtension")
    public fun String.nameToId(): String =
        this.split(" ").joinToString("_") { it.lowercase() }

    @JvmStatic
    public fun String.capsFirst(): String = this.replaceFirstChar { it.uppercase() }

    @JvmStatic
    public fun withStyleComponentTrans(text: String, colour: Int, vararg args: Any?): Component =
        Component.translatable(text, *args).withStyle { it.withColor(colour)}

    @JvmStatic
    @JvmName("withStyleComponentTrans")
    public fun String.withStyleComponentTrans(
        colour: Int = -1,
        bold: Boolean = false,
        underline: Boolean = false,
        strike: Boolean = false,
        vararg args: Any?
    ): Component =
        Component.translatable(this, *args).withStyle {
            it.withColor(colour)
                .withBold(bold)
                .withUnderlined(underline)
                .withStrikethrough(strike)
        }

    @JvmStatic
    public fun playDebugMessage(player: Player, string: String): Unit =
        player.sendSystemMessage(
            Component.literal(string).withStyle { it.withColor(getRgb()) }
        )

    @JvmStatic
    public fun playDebugMessageComp(player: Player, vararg info: String): Unit =
        info.forEach {
            player.sendSystemMessage(withStyleComponentTrans(it, getRgb()))
        }

    @JvmStatic
    @JvmOverloads
    public fun withStyleComponent(text: String, colour: Int, bold: Boolean = false, underline: Boolean = false, strike: Boolean = false): Component =
        Component.literal(text).withStyle {
            it.withColor(colour)
                .withBold(bold)
                .withUnderlined(underline)
                .withStrikethrough(strike)
        }

    @JvmStatic
    public fun stringIdToName(input: String): String =
        input.split("_").joinToString(" ") {
                name -> name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

    @JvmStatic
    @JvmOverloads
    public fun displaySelectedKey(
        key: Int,
        prefixColour: Int = headerColour,
        keyColour: Int = subHeaderColour,
    ): Component {
        val getKey = InputConstants.getKey(key, -1)
        val translatable = "text.shaydeeapi.hold_details"
        return displaySplitText(translatable, getKey.name, prefixColour, keyColour)
    }

    @JvmStatic
    @JvmOverloads
    /**
     *Strings must be translatable
     * */
    public fun displaySelectedKey(
        suffix: String,
        prefixColour: Int = headerColour,
        keyColour: Int = subHeaderColour,
    ): Component {
        val translatable = "text.shaydeeapi.hold_details"
        return displaySplitText(translatable,suffix, prefixColour, keyColour)
    }

    @JvmStatic
    public fun String.withStyle(
        colour: Int = -1,
        bold: Boolean = false,
        underline: Boolean = false,
        strike: Boolean = false,
        vararg args: Any?
    ) : Component {
        return withStyleComponentTrans(this.translatable(), colour, bold, underline, strike)
    }

    /**
     *Strings must be translatable
     * */
    @JvmStatic
    @JvmOverloads
    public fun displaySplitText(
        prefix: String,
        suffix: String,
        prefixColour: Int = headerColour,
        keyColour: Int = subHeaderColour,
    ): Component {
        val keyComponent = withStyleComponentTrans(suffix, 0)
        val deco = "[${keyComponent.string}]"
        val new = withStyleComponentTrans(deco, keyColour)

        return withStyleComponentTrans(prefix, prefixColour, new)
    }

    @JvmStatic
    public fun multiLineComponent(
        body: String,
        colour1: Int,
        colour2: Int,
        name: String = "",
        maxWidth: Int = 200,
        addSpacer: Boolean = false,
    ): List<Component> = buildList {
        val formattedText = StringSplitter { _, _ -> 10f }.splitLines(body, maxWidth, Style.EMPTY)
        if (name.isNotEmpty()) {
            add(withStyleComponent(name, colour1))
            if(addSpacer) add(Component.literal(" "))
        }

        for (line in formattedText) add(withStyleComponent(line.string, colour2))
    }

}
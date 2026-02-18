package org.shaydee.shaydeeapi.helpers

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.player.Player
import org.shaydee.shaydeeapi.helpers.ColourHelpers.getRgb
import java.util.Locale

public object TextHelpers {

    @JvmStatic
    public fun nameToId(input: String): String =
        input.split(" ").joinToString("_") { it.lowercase() }

    @JvmStatic
    public fun withStyleComponent(text: String, colour: Int): Component =
        Component.literal(text).withStyle { it.withColor(colour) }

    @JvmStatic
    public fun withStyleComponentTrans(text: String, colour: Int, vararg args: Any?): Component =
        Component.translatable(text, *args).withStyle { it.withColor(colour) }

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
    public fun stringIdToName(input: String): String =
        input.split("_").joinToString(" ") {
            name -> name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

}
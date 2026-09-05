package org.shaydee.shaydeeapi.client

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import org.shaydee.shaydeeapi.helpers.ClientHelpers.icon
import org.shaydee.shaydeeapi.helpers.RenderHelpers.customItemRenderer

public sealed interface MultiIconType {

    public data class TextureIcon(val location: ResourceLocation?) : MultiIconType

    public data class ItemIcon(val stack: Item?) : MultiIconType

    public data class ItemIconNamed(val id: String, val itemId: String) : MultiIconType

    public companion object{
        public fun translatedIcon(icon: MultiIconType, gui: GuiGraphics, size: Int, x: Int, y: Int){
            when(icon){
                is TextureIcon -> gui.icon(icon.location ?: Icons.BLANK.icon(), size, x, y)
                is ItemIcon -> gui.customItemRenderer(icon.stack?.defaultInstance ?: Items.AIR.defaultInstance, x, y, size = size.toFloat())
                is ItemIconNamed -> gui.customItemRenderer(icon.id, icon.itemId, x, y, size = size.toFloat())
            }
        }
    }

}
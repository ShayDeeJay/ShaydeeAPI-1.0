package org.shaydee.shaydeeapi.client

import net.minecraft.resources.ResourceLocation
import org.shaydee.shaydeeapi.Helpers

public enum class Icons {

    COG,
    SLOT_BORDERED,
    TICK,
    NOTIFICATION,
    DIRECTION_ARROW,
    BLANK,
    BUTTON_SELECTED,
    BUTTON_UNSELECTED,
    COMPARE,
    MENU_BUTTON,
    REFRESH,
    SAVE,
    UPGRADE,
    INVENTORY,
    CENTER_VIEW,
    BEZEL,
    VIEW,
    INFORMATION,
    HINT,
    X,
    IN_INVENTORY;

    public fun icon(): ResourceLocation = Helpers.apiRes("textures/icons/${name.lowercase()}.png")

}
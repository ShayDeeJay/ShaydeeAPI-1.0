package org.shaydee.shaydeeapi.helpers

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft

public object ClientHelpers {

    @JvmStatic
    public fun getMinecraft(): Minecraft = Minecraft.getInstance()

    @JvmStatic
    public fun isKeyDown(key: Int): Boolean = InputConstants.isKeyDown(getMinecraft().window.window, key)

}
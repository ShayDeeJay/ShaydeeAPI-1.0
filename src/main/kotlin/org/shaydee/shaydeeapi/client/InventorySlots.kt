package org.shaydee.shaydeeapi.client

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

public class InventorySlots(container: Container, slot: Int, x: Int, y: Int) : Slot(container, slot, x, y) {
    public var isSlotActive: Boolean = true


    public override fun mayPlace(stack: ItemStack): Boolean {
        return isSlotActive
    }
}
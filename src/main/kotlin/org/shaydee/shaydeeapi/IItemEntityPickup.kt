package org.shaydee.shaydeeapi

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity

public interface IItemEntityPickup {

    public fun onItemInteraction(itemEntity: ItemEntity?, livingEntity: LivingEntity?): Boolean

}
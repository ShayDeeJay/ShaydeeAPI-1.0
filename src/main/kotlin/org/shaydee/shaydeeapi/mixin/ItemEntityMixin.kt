package org.shaydee.shaydeeapi.mixin

import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.shaydee.shaydeeapi.IItemEntityPickup
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.*

@Mixin(ItemEntity::class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract fun getItem(): ItemStack

    @Shadow
    private var pickupDelay: Int = 0

    @Shadow
    private var target: UUID? = null

    @Inject(
        method = ["playerTouch"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/event/EventHooks;fireItemPickupPre(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)Lnet/neoforged/neoforge/event/entity/player/ItemEntityPickupEvent\$Pre;",
            remap = false
        )],
        cancellable = true
    )
    private fun onItemInteraction(player: Player, ci: CallbackInfo) {
        val itemStack = this.getItem()

        if (this.pickupDelay == 0 && (this.target == null || this.target == player.uuid)) {
            val item = itemStack.item
            if (item is IItemEntityPickup) {
                // In Kotlin, we cast 'this' to 'Any' then to the target class
                val self = (this as Any) as ItemEntity
                if (item.onItemInteraction(self, player)) {
                    ci.cancel()
                }
            }
        }
    }
}
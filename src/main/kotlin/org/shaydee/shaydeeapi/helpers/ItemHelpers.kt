package org.shaydee.shaydeeapi.helpers

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.behavior.BehaviorUtils
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.shaydee.shaydeeapi.helpers.ColourHelpers.getRgb
import org.shaydee.shaydeeapi.helpers.SoundHelpers.getSoundWithPosition
import org.shaydee.shaydeeapi.helpers.TextHelpers.withStyleComponent
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

public object ItemHelpers {

    @JvmStatic
    public fun getUsedItem(player: LivingEntity): ItemStack =
        player.getItemInHand(player.usedItemHand)

    @JvmStatic
    public fun hurtAndKeepItem(itemStack: ItemStack, damage: Int, level: ServerLevel, livingEntity: LivingEntity): Unit =
        hurtAndKeepItemChanced(itemStack, damage, level, livingEntity, 10)

    @JvmStatic
    public fun stackDurability(itemStack: ItemStack): Int =
        itemStack.item.getMaxDamage(itemStack) - itemStack.item.getDamage(itemStack)

    @JvmStatic
    public fun ItemStack.throwOrAddItem(player: Player){
        when{
            player.mainHandItem.isEmpty -> player.setItemInHand(InteractionHand.MAIN_HAND, this)
            player.getInventory().freeSlot != -1 -> player.addItem(this)
            else -> throwNewItem(player, this)
        }
    }

    @Deprecated("use ext function instead")
    @JvmStatic
    public fun throwOrAddItem(player: Player, newItem: ItemStack) {
        val isValidSlot = player.getInventory().freeSlot != -1
        if (isValidSlot) player.addItem(newItem) else throwNewItem(player, newItem)
    }

    @JvmStatic
    public fun repairDurability(itemStack: ItemStack): Int {
        itemStack.set(DataComponents.DAMAGE, 0)
        return 0
    }

    @JvmStatic
    public fun ItemStack.durabilityDamageCount(): Int {
        val maxDamage = this.get(DataComponents.MAX_DAMAGE) ?: return 0
        val damageTaken = this.get(DataComponents.DAMAGE) ?: return 0

        return maxDamage - damageTaken
    }

    @JvmStatic
    public fun setDurability(itemStack: ItemStack, maxDamage: Int) {
        itemStack.set(DataComponents.MAX_DAMAGE, maxDamage)
        itemStack.set(DataComponents.MAX_STACK_SIZE, 1)
        itemStack.set(DataComponents.DAMAGE, 0)
    }

    @JvmStatic
    @JvmName("hurt_ext")
    public fun ItemStack.hurtAndKeepItem(
        damage: Int,
        level: ServerLevel,
        livingEntity: LivingEntity,
        chance: Int = 10
    ): Unit = hurtAndKeepItemChanced(this, damage, level, livingEntity, chance)

    @JvmStatic
    public fun throwNewItem(livingEntity: LivingEntity, itemStack: ItemStack) {
        val offsetX = -sin(Math.toRadians(livingEntity.yRotO.toDouble())) * 2
        val offsetZ = cos(Math.toRadians(livingEntity.yRotO.toDouble())) * 2
        val spawnX = livingEntity.x + offsetX
        val spawnY = livingEntity.y + livingEntity.eyeHeight - 0.7 // No vertical offset
        val spawnZ = livingEntity.z + offsetZ
        BehaviorUtils.throwItem(livingEntity, itemStack, Vec3(spawnX, spawnY, spawnZ))
    }

    @JvmStatic
    public fun debugComponent(itemStack: ItemStack, player: Player) {
        player.sendSystemMessage(Component.literal("New Request"))
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"))
        for (component in itemStack.getComponents()) {
            player.sendSystemMessage(withStyleComponent(component.toString(), getRgb()))
            player.sendSystemMessage(Component.literal(" "))
        }
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"))
        player.sendSystemMessage(Component.literal(" "))
    }

    @JvmStatic
    public fun throwItem(livingEntity: LivingEntity, stack: ItemStack) {
        // Calculate spawn position in front of the entity
        val yaw = Math.toRadians(livingEntity.yRotO.toDouble())
        val offsetX = -sin(yaw) * 0.5
        val offsetZ = cos(yaw) * 0.5

        val spawnX = livingEntity.x + offsetX
        val spawnY = livingEntity.y + 0.5
        val spawnZ = livingEntity.z + offsetZ

        val spawnPos = Vec3(spawnX, spawnY, spawnZ)
        val x = 0.4
        val speedMultiplier = Vec3(x, x, x) // Tweak as needed

        throwItem(livingEntity.position(), stack, spawnPos, speedMultiplier, livingEntity.level())
    }

    @JvmStatic
    private fun throwItem(pos: Vec3, stack: ItemStack, offset: Vec3, speedMultiplier: Vec3, level: Level) {
        val itemEntity = ItemEntity(level, pos.x, pos.y, pos.z, stack)
        val direction = offset.subtract(pos).normalize()
        val v = 0.4
        val randX = (Random.nextDouble() - 0.5) * v
        val randY = (Random.nextDouble() - 0.5) * v
        val randZ = (Random.nextDouble() - 0.5) * v

        val finalVelocity = Vec3(
            (direction.x + randX) * speedMultiplier.x,
            (direction.y + randY) * speedMultiplier.y,
            (direction.z + randZ) * speedMultiplier.z
        )

        itemEntity.deltaMovement = finalVelocity
        itemEntity.setDefaultPickUpDelay()
        level.addFreshEntity(itemEntity)
    }

    @JvmStatic
    public fun ItemStack.throwItem(
        pos: Vec3,
        speed: Double,
        upwardSpeed: Double,
        level: Level,
        pickupDelay: Int = 10
    ) {
        val itemEntity = ItemEntity(level, pos.x, pos.y, pos.z, this)

        itemEntity.setPickUpDelay(pickupDelay)
        itemEntity.deltaMovement = Vec3(
            (Random.nextDouble() - 0.5) * speed,
            upwardSpeed + (Random.nextDouble() * 0.2),
            (Random.nextDouble() - 0.5) * speed
        )

        itemEntity.setDefaultPickUpDelay()
        level.addFreshEntity(itemEntity)
    }

    @JvmStatic
    public fun hurtAndKeepItemChanced(
        itemStack: ItemStack,
        damage: Int,
        level: Level,
        livingEntity: LivingEntity,
        chance: Int,
    ) {
        when{
            !itemStack.isDamageableItem -> return
            Random.nextInt(chance) != 0 -> return
        }

        var finalDamage = itemStack.item.damageItem(itemStack, damage, livingEntity) {}

        if (finalDamage > 0 && level is ServerLevel) {
            finalDamage = EnchantmentHelper.processDurabilityChange(level, itemStack, finalDamage)
            if (finalDamage <= 0) return
        }

        if (finalDamage != 0 && livingEntity is ServerPlayer) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(
                livingEntity,
                itemStack,
                itemStack.damageValue + finalDamage
            )
        }

        itemStack.damageValue += finalDamage

        if (stackDurability(itemStack) == 0) {
            getSoundWithPosition(
                level, livingEntity.position(), SoundEvents.ITEM_BREAK
            )
        }
    }
}
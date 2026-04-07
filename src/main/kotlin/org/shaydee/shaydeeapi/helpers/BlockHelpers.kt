package org.shaydee.shaydeeapi.helpers

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntitySelector.ENTITY_STILL_ALIVE
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemStackHandler
import org.apache.commons.lang3.tuple.Pair
import java.util.*

public object BlockHelpers {

    @JvmStatic
    public fun swapItemsWithHand(
        itemStackHandler: ItemStackHandler,
        outputSlot: Int,
        player: Player,
        hand: InteractionHand,
    ) {
        val count = 1
        val playerItem = player.getItemInHand(hand)
        val inventoryItem = itemStackHandler.getStackInSlot(outputSlot)

        itemStackHandler.setStackInSlot(outputSlot, playerItem.copyWithCount(count))
        if (playerItem.count > 1) {
            ItemHelpers.throwOrAddItem(player, inventoryItem.copyWithCount(count))
            playerItem.shrink(1)
            return
        }

        player.setItemInHand(hand, inventoryItem.copyWithCount(count))
    }

    @JvmStatic
    @JvmName("swapFromPlayer")
    public fun Player.swapItemsWithHand(
        itemStackHandler: ItemStackHandler,
        outputSlot: Int,
        hand: InteractionHand,
        count: Int = 0
    ) {
        val playerItem = this.getItemInHand(hand)
        val inventoryItem = itemStackHandler.getStackInSlot(outputSlot)
        fun getCorrectCount(itemStack: ItemStack) = if(count == 0) itemStack.count else count

        itemStackHandler.setStackInSlot(outputSlot, playerItem.copyWithCount(getCorrectCount(playerItem)))
        if (playerItem.count > 1) {
            ItemHelpers.throwOrAddItem(this, inventoryItem.copyWithCount(getCorrectCount(inventoryItem)))
            playerItem.shrink(1)
            return
        }

        this.setItemInHand(hand, inventoryItem.copyWithCount(getCorrectCount(inventoryItem)))
    }

    @JvmStatic
    public fun removeItemsFromHandToSlot(
        itemStackHandler: ItemStackHandler,
        outputSlot: Int,
        player: Player,
        itemCount: Int,
        interactionHand: InteractionHand,
    ): Boolean {
        val mainHandItems = player.getItemInHand(interactionHand)
        if (mainHandItems.isEmpty) return false

        itemStackHandler.setStackInSlot(outputSlot, mainHandItems.copyWithCount(1))
        /* if (!player.isCreative) */ mainHandItems.shrink(itemCount)
        return true
    }

    @JvmStatic
    public fun removeItemsFromSlotToHand(
        itemStackHandler: ItemStackHandler,
        outputSlot: Int,
        player: Player,
        interactionHand: InteractionHand,
    ): Boolean {
        val outputSlotTotal = itemStackHandler.getStackInSlot(outputSlot)
        if (outputSlotTotal.isEmpty) return false

        val mainHandItem = ItemHelpers.getUsedItem(player)
        return when {
            mainHandItem.isEmpty -> {
                player.setItemInHand(
                    interactionHand,
                    itemStackHandler.extractItem(
                        outputSlot,
                        outputSlotTotal.count,
                        false
                    )
                )
                true
            }

            mainHandItem.`is`(outputSlotTotal.item) && mainHandItem.count < 64 -> {
                mainHandItem.grow(outputSlotTotal.count - mainHandItem.count)
                true
            }

            else -> false
        }
    }

    // Kept the original method name (capital R) for compatibility
    @JvmStatic
    public fun removeItemsFromSlotToHand(
        itemStackHandler: ItemStackHandler,
        outputSlot: Int,
        player: Player,
        interactionHand: InteractionHand,
        level: Level,
        blockPos: BlockPos,
        soundEvents: SoundEvent,
        volume: Float,
        pitch: Float,
    ) {
        val outputSlotTotal = itemStackHandler.getStackInSlot(outputSlot)

        if (!outputSlotTotal.isEmpty) {
            if (ItemHelpers.getUsedItem(player).isEmpty && player.isShiftKeyDown) {
                player.setItemInHand(interactionHand, outputSlotTotal)
                itemStackHandler.extractItem(outputSlot, outputSlotTotal.count, false)
                level.playLocalSound(
                    blockPos,
                    soundEvents,
                    SoundSource.NEUTRAL,
                    volume,
                    pitch,
                    false
                )
            }
        }
    }

    @JvmStatic
    public fun getItemHandlerAt(
        worldIn: Level,
        x: Double,
        y: Double,
        z: Double,
        side: Direction,
    ): Optional<Pair<IItemHandler, Any>> {
        val blockPos = BlockPos.containing(x, y, z)
        val state = worldIn.getBlockState(blockPos)
        val blockEntity = if (state.hasBlockEntity()) worldIn.getBlockEntity(blockPos) else null

        val blockCap = worldIn.getCapability(
            Capabilities.ItemHandler.BLOCK,
            blockPos,
            state,
            blockEntity,
            side
        )

        if (blockCap != null) {
            return Optional.of(Pair.of(blockCap, blockEntity))
        }

        val bounding = AABB(
            x - 0.5, y - 0.5, z - 0.5,
            x + 0.5, y + 0.5, z + 0.5
        )

        val list = worldIn.getEntities(null, bounding, ENTITY_STILL_ALIVE)

        if (list.isNotEmpty()) {
            list.shuffle()
            for (entity in list) {
                val entityCap =
                    entity.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, side)
                if (entityCap != null) {
                    return Optional.of(Pair.of(entityCap, entity))
                }
            }
        }

        return Optional.empty()
    }

    @JvmStatic
    public fun stackHandlerWithFeedBack(
        itemHandler: ItemStackHandler,
        itemStack: ItemStack,
        item: Item,
        inputSlotNumber: Int,
        maxSize: Int,
        player: Player,
    ): Boolean {
        val inputSlot = itemHandler.getStackInSlot(inputSlotNumber)

        if (!itemStack.isEmpty && itemStack.`is`(item)) {
            if (inputSlot.isEmpty || itemStack.`is`(inputSlot.item)) {
                val remainingSpace = maxSize - inputSlot.count
                if (remainingSpace > 0) {
                    val amountToAdd = minOf(remainingSpace, itemStack.count)
                    val itemStackCopy = itemStack.copyWithCount(amountToAdd)

                    if (!player.abilities.instabuild) {
                        itemStack.shrink(amountToAdd)
                    }

                    itemHandler.insertItem(inputSlotNumber, itemStackCopy, false)
                    return true
                }
            }
        }

        return false
    }

    @JvmStatic
    public fun placeDoubleTallBlock(state: BlockState, level: Level, pos: BlockPos) {
        val half = BlockStateProperties.DOUBLE_BLOCK_HALF
        if (state.getValue(half) == DoubleBlockHalf.LOWER) {
            level.setBlockAndUpdate(
                pos.above(1),
                state.setValue(half, DoubleBlockHalf.UPPER)
            )
        }
    }

    @JvmStatic
    public fun saveBlockPosNBT(tag: CompoundTag, returnLocation: BlockPos) {
        val bPos = CompoundTag()
        bPos.putInt("x", returnLocation.x)
        bPos.putInt("y", returnLocation.y)
        bPos.putInt("z", returnLocation.z)
        tag.put("block_pos", bPos)
    }

    @JvmStatic
    public fun loadBlockPosNBT(tag: CompoundTag): BlockPos {
        val returnLocation = tag.getCompound("block_pos")
        return BlockPos(returnLocation.getInt("x"), returnLocation.getInt("y"), returnLocation.getInt("z"))
    }


}
package org.shaydee.shaydeeapi.helpers

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntitySelector.ENTITY_STILL_ALIVE
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DropExperienceBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.level.levelgen.SurfaceRules.state
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.fml.ModList
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemHandlerHelper
import net.neoforged.neoforge.items.ItemStackHandler
import org.apache.commons.lang3.Range
import org.apache.commons.lang3.tuple.Pair
import java.util.*

public object BlockHelpers {

    @JvmStatic
    public fun loadBlockPosNBT(tag: CompoundTag): BlockPos {
        val returnLocation = tag.getCompound("block_pos")
        return BlockPos(returnLocation.getInt("x"), returnLocation.getInt("y"), returnLocation.getInt("z"))
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
    public fun destroySpeed(blockPos: BlockPos, level: Level): Float {
        return level.getBlockState(blockPos).getDestroySpeed(level, blockPos)
    }

    @JvmStatic
    public fun dropItemsOrBlock(
        entity: Player,
        pos: BlockPos,
        breakSpeed: Float,
        fortuneLevel: Int,
        isSilkTouch: Boolean,
        voidBlocks: Boolean,
        smelt: Boolean,
        autoCollect: Boolean,
    ) {
        val level = entity.level() as? ServerLevel ?: return
        val bState = level.getBlockState(pos)
        if (bState.isAir) return

        val fluidState = level.getFluidState(pos)
        val canInteract = Range.of(0.0f, 10 + breakSpeed).contains(destroySpeed(pos, level)) || !fluidState.isEmpty
        if (!canInteract) return

        val centre = pos.center
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)

        if(!voidBlocks) {
            val block = bState.block
            when(isSilkTouch) {
                true -> {
                    val getBlock = ItemStack(block)
                    val iEntity = ItemEntity(level, centre.x, centre.y, centre.z, getBlock)
                    collectOrDrop(entity, autoCollect, iEntity, level)
                }
                else -> {
                    val tool = getDiamondPickaxe(fortuneLevel, level)
                    val drops: MutableList<ItemStack> = lootBuilder(pos, level, bState, level, tool)
                    drops.forEach {
                        val canBurn: ItemStack = smeltable(level, it)
                        val item = ItemEntity(level, centre.x, centre.y, centre.z, if (smelt) canBurn else it)
                        if(block is DropExperienceBlock){
                            val xp =  EnchantmentHelper.processBlockExperience(level, tool, bState.getExpDrop(level, pos, null, entity, tool))
                            val exp = ExperienceOrb(level, centre.x, centre.y, centre.z, xp)
                            level.addFreshEntity(exp)
                        }

                        collectOrDrop(entity, autoCollect, item, level)
                    }
                }
            }
        }

        val blockPart = BlockParticleOption(ParticleTypes.BLOCK, bState)
        ParticleHelpers.sendParticles(level, blockPart, centre, 5, 0.0, 0.0, 0.0, 1.0)
        level.removeBlock(pos, false)
    }

    @JvmStatic
    private fun smeltable(level: Level, stack: ItemStack): ItemStack {
        if (stack.isEmpty) return stack

        return level.recipeManager
            .getRecipeFor(RecipeType.SMELTING, SingleRecipeInput(stack), level)
            .map { holder ->
                val result = holder.value.getResultItem(level.registryAccess())
                if (!result.isEmpty) result.copyWithCount(stack.count * result.count)
                else stack
            }
            .orElse(stack) ?: stack
    }

    @JvmStatic
    private fun getDiamondPickaxe(fortuneLevel: Int, serverLevel: ServerLevel): ItemStack {
        val value = ItemStack(Items.DIAMOND_PICKAXE)
        EnchantHelpers.enchant(value, serverLevel.registryAccess(), Enchantments.FORTUNE, fortuneLevel)
        return value
    }

    @JvmStatic
    private fun lootBuilder(
        pos: BlockPos,
        serverLevel: ServerLevel,
        bState: BlockState,
        level: Level,
        value: ItemStack,
    ): MutableList<ItemStack> {
        val lootBuilder = LootParams.Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.TOOL, value)
            .withOptionalParameter(LootContextParams.BLOCK_STATE, bState)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos))

        return bState.getDrops(lootBuilder)
    }

    @JvmStatic
    private fun collectOrDrop(entity: Player, autoCollect: Boolean, item: ItemEntity, level: Level) {
        if (autoCollect) {
            handlePlayerPickup(item, entity)
        } else {
            level.addFreshEntity(item)
        }
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
        hand: InteractionHand = this.usedItemHand,
        count: Int = 0,
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

    @JvmStatic
    public fun externalOutputInventory(pos: BlockPos, direction: String, level: Level, itemEntity: ItemEntity): Boolean {
        val directionFromName = Direction.byName(direction) ?: return false
        val getPos = pos.relative(directionFromName)

        if(level.getBlockEntity(getPos) == null) return false
        val itemHandler = level.getCapability(ItemHandler.BLOCK, getPos, directionFromName) ?: return false

        val sourceStack = itemEntity.item
        var remaining = sourceStack.copy()
        val originalCount = remaining.count

        for (i in 0..<itemHandler.slots) {
            val slotStack = itemHandler.getStackInSlot(i)
            if (!slotStack.isEmpty && ItemStack.isSameItemSameComponents(slotStack, remaining)) {
                remaining = itemHandler.insertItem(i, remaining, false)
                if (remaining.isEmpty) break
            }
        }

        for (i in 0 until itemHandler.slots) {
            if (remaining.isEmpty) break
            if (itemHandler.getStackInSlot(i).isEmpty) {
                remaining = itemHandler.insertItem(i, remaining, false)
            }
        }

        val inserted = originalCount - remaining.count
        if (inserted > 0) {
            sourceStack.shrink(inserted)
            if (sourceStack.isEmpty) itemEntity.discard()
            return true
        }

        return false
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
            ItemHandler.BLOCK,
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
                    entity.getCapability(ItemHandler.ENTITY_AUTOMATION, side)
                if (entityCap != null) {
                    return Optional.of(Pair.of(entityCap, entity))
                }
            }
        }

        return Optional.empty()
    }

    private fun insertIntoContainers(
        player: Player,
        stack: ItemStack,
        amount: Int
    ): Int {
        var remaining = stack.copyWithCount(amount)

        for (slot in 0 until player.inventory.containerSize) {
            val containerStack = player.inventory.getItem(slot)
            if (containerStack.isEmpty) continue

            val handler = containerStack.getCapability(ItemHandler.ITEM) ?: continue
            remaining = ItemHandlerHelper.insertItem(handler, remaining, false)

            if (remaining.isEmpty) return 0
        }

        return remaining.count
    }

    public fun handlePlayerPickup(itemEntity: ItemEntity, player: Player): Boolean {
        val inv = player.inventory
        val maxStackSize = 64
        var remainingAmount = itemEntity.item.count
        val availSlots = inv.containerSize - 5
        var pickedUpItems = false

        if(player.isCreative){
            player.addItem(itemEntity.item)
            itemEntity.discard()
            return true
        }

        for (i in 0 until availSlots) {
            if (remainingAmount <= 0) break

            val slotStack = inv.getItem(i)
            val slotSpace = maxStackSize - slotStack.count
            val isStack = slotStack.item === itemEntity.item.item && slotStack.count < maxStackSize
            val emptySlot = slotStack.isEmpty

            when {
                isStack -> {
                    val addAmount = minOf(remainingAmount, slotSpace)
                    slotStack.grow(addAmount)
                    remainingAmount -= addAmount
                    itemEntity.item.shrink(addAmount)
                    pickedUpItems = true
                }

                emptySlot -> {
                    val addAmount = minOf(remainingAmount, maxStackSize)
                    inv.setItem(i, itemEntity.item.copy().split(addAmount))
                    remainingAmount -= addAmount
                    itemEntity.item.shrink(addAmount)
                    pickedUpItems = true
                }
            }
        }

        if (remainingAmount > 0) {
            itemEntity.moveTo(player.position())
            player.level().addFreshEntity(itemEntity)
            return pickedUpItems
        }

        return pickedUpItems
    }

//    public fun handlePlayerPickup(itemEntity: ItemEntity, player: Player): Boolean {
//        val inv = player.inventory
//        val entityStack = itemEntity.item
//        var remainingAmount = entityStack.count
//        val availSlots = inv.containerSize - 5
//        var pickedUpItems = false
//
//        if (player.isCreative) {
//            player.addItem(entityStack)
//            return true
//        }
//
//        for (i in 0 until availSlots) {
//            if (remainingAmount <= 0) break
//
//            val slotStack = inv.getItem(i)
//            val maxStackSize = entityStack.maxStackSize
//            val isSameItem = !slotStack.isEmpty && ItemStack.isSameItemSameComponents(slotStack, entityStack)
//            val hasSpace = slotStack.count < maxStackSize
//
//            if (isSameItem && hasSpace) {
//                val addAmount = minOf(remainingAmount, maxStackSize - slotStack.count)
//                slotStack.grow(addAmount)
//                remainingAmount -= addAmount
//                entityStack.shrink(addAmount)
//                pickedUpItems = true
//            } else if (slotStack.isEmpty) {
//                val addAmount = minOf(remainingAmount, maxStackSize)
//                inv.setItem(i, entityStack.split(addAmount))
//                remainingAmount -= addAmount
//                pickedUpItems = true
//            }
//        }
//
//        if (remainingAmount > 0) {
//            itemEntity.item = entityStack
//            itemEntity.moveTo(player.position())
//
//            if (!itemEntity.isAlive) player.level().addFreshEntity(itemEntity)
//        }
//
//        return pickedUpItems
//    }
}
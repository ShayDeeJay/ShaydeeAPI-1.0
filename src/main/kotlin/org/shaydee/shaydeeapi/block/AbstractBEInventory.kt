package org.shaydee.shaydeeapi.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler

public abstract class AbstractBEInventory(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
) : SyncedBlockEntity(type, pos, state) {

    protected var data: ContainerData = object : ContainerData {
        public override fun get(index: Int): Int = 0
        public override fun set(index: Int, value: Int) {}
        public override fun getCount(): Int = 0
    }

    public val inputItemHandler: ItemStackHandler = setStackHandler(setInputSlots(), getMaxSlotSizeInput())
    public val outputItemHandler: ItemStackHandler = setStackHandler(setOutputSlots(), getMaxSlotSizeOutput())

    public abstract fun setInputSlots(): Int

    public open fun getMaxSlotSizeInput(): Int = 64
    public open fun setOutputSlots(): Int = 0
    public open fun getMaxSlotSizeOutput(): Int = 0

    override fun applyImplicitComponents(componentInput: DataComponentInput) {
        super.applyImplicitComponents(componentInput)
    }

    public override fun onDataPacket(net: Connection, pkt: ClientboundBlockEntityDataPacket, provider: HolderLookup.Provider) {
        super.onDataPacket(net, pkt, provider)
        level ?: return
        level!!.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_ALL)
    }

    public fun sendBlockUpdate(level: Level?, blockPos: BlockPos, blockState: BlockState, setChanged: Runnable) {
        level ?: return
        setChanged.run()
        level.sendBlockUpdated(blockPos, blockState, blockState, 3)
    }

    public override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("inputInventory", inputItemHandler.serializeNBT(registries))
        tag.put("outputInventory", outputItemHandler.serializeNBT(registries))
    }

    public override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        inputItemHandler.deserializeNBT(registries, tag.getCompound("inputInventory"))
        outputItemHandler.deserializeNBT(registries, tag.getCompound("outputInventory"))
    }

    public fun setStackHandler(slots: Int, slotStackLimit: Int): ItemStackHandler {
        return object : ItemStackHandler(slots) {
            public override fun onContentsChanged(slot: Int) {
                sendBlockUpdate(level, worldPosition, blockState) { setChanged() }
            }

            public override fun getSlotLimit(slot: Int): Int {
                return getMaxSlotSizeInput()
            }
        }
    }

    public fun dropsAllInventory(level: Level) {
        val inputInventory = SimpleContainer(setInputSlots())
        val outputInventory = SimpleContainer(setOutputSlots())

        for (i in 0 until inputItemHandler.slots) {
            if (i < inputInventory.containerSize) {
                val stackInSlot = inputItemHandler.getStackInSlot(i)
                inputInventory.setItem(i, stackInSlot)
            }
        }

        for (i in 0 until outputItemHandler.slots) {
            if (i < outputInventory.containerSize) {
                val stackInSlot = outputItemHandler.getStackInSlot(i)
                outputInventory.setItem(i, stackInSlot)
            }
        }

        Containers.dropContents(level, worldPosition, inputInventory)
        Containers.dropContents(level, worldPosition, outputInventory)
    }
}
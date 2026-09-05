package org.shaydee.shaydeeapi.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

public open class SyncedBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState,
) : BlockEntity(type, pos, blockState) {
    public var privateTicks : Int = 0
    public var saveInt: Int = 0

    public fun incrementPrivateTicks(): Int = privateTicks++

    public fun decrementPrivateTicks(): Int = privateTicks--

    public open fun serverTick(level: Level, pos: BlockPos, blockState: BlockState) {}

    public open fun clientTick(level: Level, pos: BlockPos, blockState: BlockState) {}

    public override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? =
        ClientboundBlockEntityDataPacket.create(this)

    public override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("private_ticks", privateTicks)
        tag.putInt("save_int", this.saveInt)
    }

    public override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        this.privateTicks = tag.getInt("private_ticks")
        this.saveInt = tag.getInt("save_int")
    }

    public override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        this.saveAdditional(tag, registries)
        return tag
    }

    public override fun onDataPacket(
        net: Connection,
        pkt: ClientboundBlockEntityDataPacket,
        lookupProvider: HolderLookup.Provider,
    ) {
        super.onDataPacket(net, pkt, lookupProvider)
        handleUpdateTag(pkt.tag, lookupProvider)
    }

    public fun updateBlock() {
        val level = getLevel()
        if (level != null) {
            val state = level.getBlockState(worldPosition)
            level.sendBlockUpdated(worldPosition, state, state, 3)
            setChanged()
        }
    }

}
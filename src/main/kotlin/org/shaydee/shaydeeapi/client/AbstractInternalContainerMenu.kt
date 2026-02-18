package org.shaydee.shaydeeapi.client

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.shaydee.shaydeeapi.block.AbstractBEInventory

public abstract class AbstractInternalContainerMenu : AbstractContainerMenu {

    public companion object {
        public const val SLOTS_IN_ROW: Int = 9
        public const val INV_ROWS: Int = 3
        public const val INV_SIZE: Int = SLOTS_IN_ROW * INV_ROWS
        public const val SLOT_SIZE: Int = SLOTS_IN_ROW + INV_SIZE
        public const val SLOT_A: Int = 0
        public const val INV_SLOT_A: Int = SLOT_A + SLOT_SIZE
    }

    protected val blockEntity: AbstractBEInventory
    protected val level: Level

    public constructor(
        menuType: MenuType<*>,
        containerId: Int,
        inv: Inventory,
        extraData: FriendlyByteBuf
    ) : this(
        menuType,
        containerId,
        inv,
        inv.player.level().getBlockEntity(extraData.readBlockPos()) as AbstractBEInventory,
        SimpleContainerData(0)
    )

    public constructor(
        menuType: MenuType<*>,
        containerId: Int,
        inv: Inventory,
        entity: AbstractBEInventory,
        data: ContainerData
    ) : super(menuType, containerId) {
        val heightDiff = 55

        blockEntity = entity
        level = inv.player.level()

        addDataSlots(data)
        addPlayerInventory(inv, heightDiff)
        addPlayerHotbar(inv, heightDiff)
    }

    protected abstract fun getAllSlots(): Int

    protected abstract fun getAssociatedBlock(): Block

    public open fun adjustInventoryY(): Int = -33

    public open fun adjustInventoryX(): Int = 0

    public override fun stillValid(player: Player): Boolean {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.blockPos),
            player,
            getAssociatedBlock()
        )
    }

    public fun addPlayerHotbar(playerInventory: Inventory, heightDiff: Int) {
        for (hotbarX in 0 until 9) {
            addSlot(
                InventorySlots(
                    playerInventory,
                    hotbarX,
                    8 + hotbarX * 18 + adjustInventoryX(),
                    142 + heightDiff + adjustInventoryY()
                )
            )
        }
    }

    public fun addPlayerInventory(playerInventory: Inventory, heightDiff: Int) {
        for (playerInvY in 0 until 3) {
            for (playerInvX in 0 until 9) {
                addSlot(
                    InventorySlots(
                        playerInventory,
                        playerInvX + playerInvY * 9 + 9,
                        8 + playerInvX * 18 + adjustInventoryX(),
                        84 + playerInvY * 18 + heightDiff + adjustInventoryY()
                    )
                )
            }
        }
    }

    public override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        val vanilla = SLOT_A + SLOT_SIZE
        val beInventory = INV_SLOT_A + getAllSlots()

        val sourceSlot = slots[slotIndex]
        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()
        val empty = ItemStack.EMPTY

        if (!sourceSlot.hasItem()) {
            return empty
        } else if (slotIndex < vanilla) {
            if (!moveItemStackTo(sourceStack, INV_SLOT_A, beInventory, false)) return empty
        } else if (slotIndex < beInventory) {
            if (!moveItemStackTo(sourceStack, SLOT_A, vanilla, false)) return empty
        } else {
            return empty
        }

        if (sourceStack.count == 0) {
            sourceSlot.set(empty)
        } else {
            sourceSlot.setChanged()
        }

        sourceSlot.onTake(player, sourceStack)
        return copyOfSourceStack
    }
}

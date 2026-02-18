package org.shaydee.shaydeeapi.helpers
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantment.enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.item.enchantment.ItemEnchantments
import java.util.stream.Stream
import kotlin.random.Random

public object EnchantHelpers {

    @JvmStatic
    public fun randomApplicableEnchantment(itemStack: ItemStack): List<EnchantmentInstance?> =
        EnchantmentHelper.selectEnchantment(
            RandomSource.create(),
            itemStack,
            Random.nextInt(0, 5),
            Stream.builder<Holder<Enchantment?>?>().build()
        )

    @JvmStatic
    public fun enchant(
        stack: ItemStack,
        access: RegistryAccess,
        enchantmentKey: ResourceKey<Enchantment?>?,
        level: Int
    ) {
        val enchantment = enchantmentFromKey(access, enchantmentKey)
        if (enchantment != null) stack.enchant(enchantment, level)
    }

    @JvmStatic
    public fun enchantmentFromKey(
        registryAccess: RegistryAccess,
        enchantmentKey: ResourceKey<Enchantment?>?,
    ): Holder<Enchantment?>? {
        val reg = registryAccess.registry(Registries.ENCHANTMENT).orElse(null) ?: return null
        val enchantment = reg.get(enchantmentKey) ?: return null

        return reg.wrapAsHolder(enchantment)
    }

    @JvmStatic
    public fun applyBookEnchantsToItem(
        enchantedBook: ItemStack,
        enchantableItem: ItemStack,
        checkOnly: Boolean
    ): Boolean {
        val lookUp = enchantedBook.getComponents().get(DataComponents.STORED_ENCHANTMENTS) ?: return false
        var canApplyAnyEnchants = false

        for (enchant in lookUp.entrySet()) {
            if (enchant.key.value().canEnchant(enchantableItem)) {
                if (!checkOnly) enchantableItem.enchant(enchant.key, enchant.intValue)
                canApplyAnyEnchants = true
            }
        }

        return canApplyAnyEnchants
    }

}
package org.shaydee.shaydeeapi

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

public object Helpers {

    @JvmStatic
    public fun <T> listRandom(collection: MutableList<T>): T = collection.random()

    public fun Level.sLevel() : ServerLevel? =
        this as? ServerLevel

    public fun Level.cLevel() : ClientLevel? =
        this as? ClientLevel

    public fun res(location: String, modId: String): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(modId, location)

    public fun getAttributeValue(player: Player, attribute: Holder<Attribute?>): Double =
        player.getAttribute(attribute)?.value ?: -1.0

    @JvmStatic
    public fun nbtDoubleList(vararg pNumbers: Double): ListTag {
        val listTag = ListTag()
        pNumbers.forEach { listTag.add(DoubleTag.valueOf(it)) }
        return listTag
    }

    public fun filterList(
        collection: List<Component>,
        vararg item: String
    ): List<Component> {
        val filter = item.toSet()
        @Suppress("UNCHECKED_CAST")
        return collection.filter { component -> filter.any { component.string.contains(it) } }
    }

    public fun addTransientAttribute(player: Player, value: Double, location: String, id: String, attributeHolder: Holder<Attribute?>?) {
        val modifier = AttributeModifier(
            res(location, id),
            value,
            AttributeModifier.Operation.ADD_VALUE
        )
        val multiMap: Multimap<Holder<Attribute?>?, AttributeModifier?> = HashMultimap.create()
        multiMap.put(attributeHolder, modifier)
        player.attributes.addTransientAttributeModifiers(multiMap)
    }

    public fun getRandomParticleVelocity(speed: Double): Vec3 {
        val theta = Random.nextDouble() * 2 * Math.PI
        val phi = Random.nextDouble() * Math.PI

        val x = sin(phi) * cos(theta)
        val y = cos(phi)
        val z = sin(phi) * sin(theta)

        return Vec3(x, y, z).normalize().scale(speed)
    }

    @JvmStatic
    public fun getInflate(pos: BlockPos, xSize: Int, ySize: Int, zSize: Int): AABB {
        val aabb = AABB(pos)
        return AABB(pos)
            .setMinX(aabb.minX + xSize)
            .setMaxX(aabb.maxX - xSize)
            .setMinY(aabb.minY + ySize)
            .setMaxY(aabb.maxY - ySize)
            .setMinZ(aabb.minZ + zSize)
            .setMaxZ(aabb.maxZ - zSize)
    }

}
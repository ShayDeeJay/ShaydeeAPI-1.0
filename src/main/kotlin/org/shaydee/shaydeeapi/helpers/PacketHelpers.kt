package org.shaydee.shaydeeapi.helpers

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

public object PacketHelpers {

    public fun sendPacketsToPlayer(level: Level?, payloads: CustomPacketPayload) {
        if ((level is ServerLevel)) {
            level.players().indices
            for (j in level.players().indices) {
                val serverPlayer = level.players()[j]
                PacketDistributor.sendToPlayer(serverPlayer, payloads)
            }
        }
    }

    public fun sendPacketsToPlayerDistance(pos: Vec3, distance: Int, level: Level?, payloads: CustomPacketPayload) {
        if ((level is ServerLevel)) {
            for (j in level.players().indices) {
                val serverPlayer = level.players()[j]
                if (pos.closerThan(serverPlayer.position(), distance.toDouble())) {
                    PacketDistributor.sendToPlayer(serverPlayer, payloads)
                }
            }
        }
    }

    public fun sendPacketsToPlayerDistance(
        pos: Vec3,
        distance: Int,
        level: Level?,
        serverPlayerConsumer: (ServerPlayer) -> Unit,
    ) {
        if ((level is ServerLevel)) {
            for (j in level.players().indices) {
                val serverPlayer = level.players()[j]
                if (pos.closerThan(serverPlayer.position(), distance.toDouble())) {
                    serverPlayerConsumer(serverPlayer)
                }
            }
        }
    }

    public fun sendEffectPacketsToPlayerDistance(
        pos: Vec3,
        distance: Int,
        level: Level?,
        entityId: Int,
        effectInstance: MobEffectInstance,
    ) {
        if ((level is ServerLevel)) {
            for (j in level.players().indices) {
                val serverPlayer = level.players()[j]
                if (pos.closerThan(serverPlayer.position(), distance.toDouble())) {
                    serverPlayer.connection.send(ClientboundUpdateMobEffectPacket(entityId, effectInstance, true))
                }
            }
        }
    }

    public fun sendEffectPacketsToPlayer(level: Level?, entityId: Int, effectInstance: MobEffectInstance) {
        if ((level is ServerLevel)) {
            for (j in level.players().indices) {
                val serverPlayer = level.players()[j]
                serverPlayer.connection.send(ClientboundUpdateMobEffectPacket(entityId, effectInstance, true))
            }
        }
    }

}
package org.shaydee.shaydeeapi.events

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import org.shaydee.shaydeeapi.helpers.ColourHelpers
import org.shaydee.shaydeeapi.helpers.ParticleHelpers
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.genericParticle
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.sendParticles
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.spiralParticle
import org.shaydee.shaydeeapi.particle.GenericParticleOption
import org.shaydee.shaydeeapi.particle.ParticleStore
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg
import kotlin.random.Random

public class ServerEvents {

    @SubscribeEvent
    public fun onPlayerTick(event: PlayerTickEvent.Post){
        val player = event.entity
        val level = player.level() as? ServerLevel ?: return
        if(level.server.tickCount%10 != 0) return
        val center = BlockPos.containing(8.0, -60.0, 8.0)
        val pPos = Vec3(center.x - player.x, center.y - player.y, center.z - player.z)

        repeat(14) {
//            val x = ParticleHelpers.getNonBakedParticles(ColourHelpers.uniqueA, ColourHelpers.uniqueB, 20, 2F)


            level.spiralParticle(center.center, 2.0, 1.0, ColourHelpers.getRgb())

//            level.addParticle(generic, player.x, player.y , player.z, player.bbWidth.toDouble()*2, 1.0, pPos.z + Random.nextDouble(0.3, 0.7))


//            ParticleHelpers.sendParticles(
//                level,
//                generic,
//                Vec3(8.0, -61.0, 8.0),
//                5,
//                player.x + Random.nextDouble(0.3, 0.7) - 0.5,
//                player.y - Random.nextDouble(0.3, 0.7) - 1.6,
//                player.z + Random.nextDouble(0.3, 0.7) - 0.5,
//                10.toDouble()
//            )
        }
    }


}
package org.shaydee.shaydeeapi.events

import ca.weblite.objc.Client
import net.minecraft.advancements.critereon.MovementPredicate.speed
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import org.shaydee.shaydeeapi.helpers.ColourHelpers
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.hoverParticle
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.moveToParticle
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.sendParticles
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.spiralParticle
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.standardParticle
import org.shaydee.shaydeeapi.particle.GenericParticleOption
import org.shaydee.shaydeeapi.particle.ParticleStore
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg
import kotlin.random.Random

public class ServerEvents {

    @SubscribeEvent
    public fun onPlayerTick(event: PlayerTickEvent.Post){
        val player = event.entity
        val level = player.level() as? ClientLevel ?: return
        val center = BlockPos.containing(8.0, -60.0, 8.0)

    }

    @SubscribeEvent
    public fun levelTickEvent(tickEvent: LevelTickEvent.Pre?) {
        val level = tickEvent?.level as? ClientLevel ?: return

        val start = BlockPos.containing(8.0, -56.0, 8.0)
        val endRange = BlockPos.containing(8.0, -60.0, 8.0)

        level.hoverParticle(
            ShaydeeAPIReg.ALTAR,
            endRange.center,
            radius = 2.0,
            size = 0.2F,
            colour = ColourHelpers.negativeRed,
            staticSize = true,
            lifetime = 30,
            rotationSpeed = 0.2
        )
        if(level.gameTime % 30 == 0L){

//            level.standardParticle(
//                ShaydeeAPIReg.ALTAR,
//                endRange.center,
//                size = 0.1F,
//                colour = ColourHelpers.headerColour,
//                fade = ColourHelpers.negativeRed,
//                staticSize = true,
//                lifetime = 30,
//                yOffset = 0.5
//            )
        }

    }


}
package org.shaydee.shaydeeapi.debug

import net.minecraft.client.multiplayer.ClientLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import org.shaydee.shaydeeapi.ShaydeeAPI
import org.shaydee.shaydeeapi.helpers.ColourHelpers
import org.shaydee.shaydeeapi.helpers.ParticleHelpers
import kotlin.random.Random

@EventBusSubscriber(modid = ShaydeeAPI.ID)
public object ServerEvents {

    @SubscribeEvent
    public fun useEvent(event: PlayerInteractEvent.RightClickItem) {
        val player = event.entity
        val level = player.level() as? ClientLevel ?: return

        repeat(10) {
            val x = ParticleHelpers.getNonBakedParticles(ColourHelpers.uniqueA, ColourHelpers.uniqueB, 20, 2F)
            ParticleHelpers.sendParticles(
                level,
                x,
                player.position(),
                5,
                Random.nextDouble() - 0.5,
                1.toDouble(),
                Random.nextDouble() - 0.5,
                50.toDouble()
            )
        }
    }

}
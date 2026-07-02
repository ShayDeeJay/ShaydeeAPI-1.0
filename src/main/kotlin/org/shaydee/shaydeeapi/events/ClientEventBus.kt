package org.shaydee.shaydeeapi.events

import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import org.shaydee.shaydeeapi.ShaydeeAPI
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.genericParticle
import org.shaydee.shaydeeapi.particle.GenericParticle
import org.shaydee.shaydeeapi.particle.GenericParticle.GenericProvider
import org.shaydee.shaydeeapi.particle.MovingParticle
import org.shaydee.shaydeeapi.particle.MovingParticle.EnchantProvider
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.ENCHANT
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.GENERIC
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.MAGIC
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SOFT
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SQUARE
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.VITALITY

@EventBusSubscriber(modid = ShaydeeAPI.ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
public object ClientEventBus {

    @SubscribeEvent
    public fun registerParticleFactories(event: RegisterParticleProvidersEvent) {
//        ShaydeeAPIReg.PARTICLE_REGISTRY.entries.forEach {
//            event.registerSpriteSet(it.get(), ::GenericProvider)
//        }

        event.registerSpriteSet(MAGIC, ::GenericProvider)
        event.registerSpriteSet(SOFT, ::GenericProvider)
        event.registerSpriteSet(MAGIC,::GenericProvider)
        event.registerSpriteSet(GENERIC, ::GenericProvider)
        event.registerSpriteSet(SQUARE, ::GenericProvider)
        event.registerSpriteSet(ENCHANT, ::GenericProvider)
        event.registerSpriteSet(VITALITY, ::GenericProvider)
    }

}
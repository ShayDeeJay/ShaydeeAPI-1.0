package org.shaydee.shaydeeapi.registry

import com.mojang.serialization.MapCodec
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.registries.DeferredRegister
import org.shaydee.shaydeeapi.ShaydeeAPI
import org.shaydee.shaydeeapi.particle.GenericParticleOption
import org.shaydee.shaydeeapi.particle.GenericParticleOption.*
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

public object ShaydeeAPIReg {

    public val PARTICLE_REGISTRY: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(Registries.PARTICLE_TYPE, ShaydeeAPI.ID)

    public val GENERIC: ParticleType<GenericParticleOption> by
    register("generic", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val MAGIC: ParticleType<GenericParticleOption> by
    register("magic", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val SOFT: ParticleType<GenericParticleOption> by
    register("soft", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val SQUARE: ParticleType<GenericParticleOption> by
    register("square", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val ENCHANT: ParticleType<GenericParticleOption> by
    register("enchant", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val MAGIC_MOVE: ParticleType<GenericParticleOption> by
    register("magic_move", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val SOFT_MOVE: ParticleType<GenericParticleOption> by
    register("soft_move", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val GENERIC_MOVE: ParticleType<GenericParticleOption> by
    register("generic_move", Companion.MAP_CODEC, Companion.STREAM_CODEC)


    private fun <T : ParticleOptions> register(
        pKey: String,
        pCodecFactory: MapCodec<T>,
        streamCodec: StreamCodec<in ByteBuf, T>
    ) = PARTICLE_REGISTRY.register(pKey) { ->
        object : ParticleType<T>(true) {
            public override fun codec(): MapCodec<T> = pCodecFactory

            public override fun streamCodec(): StreamCodec<in ByteBuf, T> = streamCodec
        }
    }
}
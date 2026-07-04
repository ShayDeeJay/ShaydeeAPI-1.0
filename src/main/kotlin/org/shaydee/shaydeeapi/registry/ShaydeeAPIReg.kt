package org.shaydee.shaydeeapi.registry

import com.mojang.serialization.MapCodec
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.registries.DeferredHolder
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

    public val SOUL: ParticleType<GenericParticleOption> by
    register("soul", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val SPARK: ParticleType<GenericParticleOption> by
    register("spark", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val SPELL: ParticleType<GenericParticleOption> by
    register("spell", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val GLITTER: ParticleType<GenericParticleOption> by
    register("glitter", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public val ALTAR: ParticleType<GenericParticleOption> by
    register("altar", Companion.MAP_CODEC, Companion.STREAM_CODEC)

    public fun <T : ParticleOptions> register(
        pKey: String,
        pCodecFactory: MapCodec<T>,
        streamCodec: StreamCodec<in ByteBuf, T>
    ): DeferredHolder<ParticleType<*>, out ParticleType<T>> = PARTICLE_REGISTRY.register(pKey) { ->
        object : ParticleType<T>(true) {
            public override fun codec(): MapCodec<T> = pCodecFactory

            public override fun streamCodec(): StreamCodec<in ByteBuf, T> = streamCodec
        }
    }
}
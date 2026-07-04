package org.shaydee.shaydeeapi.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.block.Rotation

public data class GenericParticleOption(
    val pType: ParticleType<*>,
    val colour: Int = 0,
    val fade: Int = 0,
    val lifetime: Int,
    val size: Float,
    val setStaticSize: Boolean,
    val speed: Double,
    val animationType: Int,
    val rotation: Double
) : ParticleOptions {

    override fun getType(): ParticleType<*> = pType

    public companion object {

        private val TYPE_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ParticleType<*>> =
            ByteBufCodecs.registry(Registries.PARTICLE_TYPE)

        public val STREAM_CODEC: StreamCodec<in ByteBuf, GenericParticleOption> =
            StreamCodec.of(
                { buf, option ->
                    TYPE_STREAM_CODEC.encode(buf as RegistryFriendlyByteBuf, option.pType)
                    buf.writeInt(option.colour)
                    buf.writeInt(option.fade)
                    buf.writeInt(option.lifetime)
                    buf.writeFloat(option.size)
                    buf.writeBoolean(option.setStaticSize)
                    buf.writeDouble(option.speed)
                    buf.writeInt(option.animationType)
                    buf.writeDouble(option.rotation)
                },
                { buf ->
                    GenericParticleOption(
                        TYPE_STREAM_CODEC.decode(buf as RegistryFriendlyByteBuf),
                        buf.readInt(),
                        buf.readInt(),
                        buf.readInt(),
                        buf.readFloat(),
                        buf.readBoolean(),
                        buf.readDouble(),
                        buf.readInt(),
                        buf.readDouble(),
                    )
                }
            )

        public val MAP_CODEC: MapCodec<GenericParticleOption> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("type").forGetter(GenericParticleOption::pType),
                    Codec.INT.fieldOf("colour").forGetter(GenericParticleOption::colour),
                    Codec.INT.fieldOf("fade").forGetter(GenericParticleOption::fade),
                    Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOption::lifetime),
                    Codec.FLOAT.fieldOf("size").forGetter(GenericParticleOption::size),
                    Codec.BOOL.fieldOf("setStaticSize").forGetter(GenericParticleOption::setStaticSize),
                    Codec.DOUBLE.fieldOf("speed").forGetter(GenericParticleOption::speed),
                    Codec.INT.fieldOf("animation").forGetter(GenericParticleOption::animationType),
                    Codec.DOUBLE.fieldOf("rotation").forGetter(GenericParticleOption::rotation),
                ).apply(instance, ::GenericParticleOption)
            }
    }
}
package org.shaydee.shaydeeapi.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.codec.StreamCodec
import org.shaydee.shaydeeapi.particle.ParticleStore.Companion.getColouredParticle

public data class GenericParticleOption(
    val type: Int,
    val colour: Int,
    val fade: Int,
    val lifetime: Int,
    val size: Float,
    val setStaticSize: Boolean,
    val speed: Double
) : ParticleOptions {

    public override fun getType(): ParticleType<*> =
        getColouredParticle[type]

    public companion object {

        public val STREAM_CODEC: StreamCodec<in ByteBuf, GenericParticleOption> =
            StreamCodec.of(
                { buf, option ->
                    buf.writeInt(option.type)
                    buf.writeInt(option.colour)
                    buf.writeInt(option.fade)
                    buf.writeInt(option.lifetime)
                    buf.writeFloat(option.size)
                    buf.writeBoolean(option.setStaticSize)
                    buf.writeDouble(option.speed)
                },
                { buf ->
                    GenericParticleOption(
                        buf.readInt(),
                        buf.readInt(),
                        buf.readInt(),
                        buf.readInt(),
                        buf.readFloat(),
                        buf.readBoolean(),
                        buf.readDouble()
                    )
                }
            )

        public val MAP_CODEC: MapCodec<GenericParticleOption> =
            RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.INT.fieldOf("type").forGetter(GenericParticleOption::type),
                    Codec.INT.fieldOf("colour").forGetter(GenericParticleOption::colour),
                    Codec.INT.fieldOf("fade").forGetter(GenericParticleOption::fade),
                    Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOption::lifetime),
                    Codec.FLOAT.fieldOf("size").forGetter(GenericParticleOption::size),
                    Codec.BOOL.fieldOf("setStaticSize").forGetter(GenericParticleOption::setStaticSize),
                    Codec.DOUBLE.fieldOf("speed").forGetter(GenericParticleOption::speed)
                ).apply(instance, ::GenericParticleOption)
            }
    }
}
package org.shaydee.shaydeeapi.particle

import net.minecraft.core.particles.ParticleType
import org.shaydee.shaydeeapi.helpers.ParticleHelpers
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.ENCHANT
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.GENERIC
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.GENERIC_MOVE
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.MAGIC
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.MAGIC_MOVE
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SOFT
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SOFT_MOVE
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SQUARE

public data class ParticleStore(val r: Int, val g: Int, val b: Int) {

    public companion object {
        public const val GENERIC_PARTICLE: Int = 0
        public const val MAGIC_PARTICLE: Int = 1
        public const val SOFT_PARTICLE: Int = 2
        public const val ENCHANT_PARTICLE: Int = 3
        public const val MAGIC_MOVE_PARTICLE: Int = 4
        public const val GENERIC_MOVE_PARTICLE: Int = 5
        public const val SOFT_MOVE_PARTICLE: Int = 6
        public const val SQUARE_PARTICLE: Int = 7

        public val getColouredParticle: List<ParticleType<*>> = listOf(
            GENERIC,
            MAGIC,
            SOFT,
            ENCHANT,
            MAGIC_MOVE,
            GENERIC_MOVE,
            SOFT_MOVE,
            SQUARE
        )

        public fun genericParticleFast(colour: Int, fade: Int): GenericParticleOption =
            ParticleHelpers.genericParticle(
                GENERIC_PARTICLE,
                6,
                0.55f,
                colour,
                fade
            )

        public fun genericParticleSlow(colour: Int, fade: Int): GenericParticleOption =
            ParticleHelpers.genericParticle(
                GENERIC_PARTICLE,
                20,
                1f,
                colour,
                fade
            )

        public fun rgbToInt(red: Int, green: Int, blue: Int): Int {
            require(red in 0..255 && green in 0..255 && blue in 0..255) {
                "RGB components must be in the range 0–255"
            }
            return (red shl 16) or (green shl 8) or blue
        }
    }
}
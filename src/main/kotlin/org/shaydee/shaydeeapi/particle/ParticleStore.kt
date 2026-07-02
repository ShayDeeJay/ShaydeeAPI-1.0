package org.shaydee.shaydeeapi.particle

import net.minecraft.core.particles.ParticleType
import org.shaydee.shaydeeapi.helpers.ParticleHelpers
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.ENCHANT
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.GENERIC
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.MAGIC
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SOFT
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg.SQUARE

public object ParticleStore {
    public const val STANDARD: Int = 0
    public const val MOVE_TO: Int = 1
    public const val SPIRAL: Int = 2
    public const val FLOAT_AROUND: Int = 3

}
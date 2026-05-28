package org.shaydee.shaydeeapi.data

import net.minecraft.sounds.SoundEvent

public data class MultiSound(
    val audio: SoundEvent,
    val volume: Float = 1F,
    val pitch: Float = 1F
)
package org.shaydee.shaydeeapi.helpers
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

public object SoundHelpers{

    @JvmStatic
    @JvmOverloads
    public fun getSoundWithPosition(level: Level, position: BlockPos, audio: SoundEvent, source: SoundSource = SoundSource.BLOCKS, volume: Float = 1F, pitch: Float = 1F) {
        level.playSound(
            null,
            position.x.toDouble(),
            position.y.toDouble(),
            position.z.toDouble(),
            audio,
            source,
            volume,
            pitch
        )
    }

    @JvmStatic
    @JvmOverloads
    public fun getSoundWithPosition(level: Level, position: Vec3, audio: SoundEvent, source: SoundSource = SoundSource.PLAYERS, volume: Float = 1F, pitch: Float = 1F) {
        level.playSound(
            null,
            position.x,
            position.y,
            position.z,
            audio,
            source,
            volume,
            pitch
        )
    }

}
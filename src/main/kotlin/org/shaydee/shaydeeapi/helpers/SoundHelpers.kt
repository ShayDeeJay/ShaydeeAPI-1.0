package org.shaydee.shaydeeapi.helpers
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.openjdk.nashorn.internal.objects.NativeRegExp.source

public data class MultiSound(
    val audio: SoundEvent,
    val volume: Float = 1F,
    val pitch: Float = 1F
)

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

    @JvmStatic
    public fun multiSound(level: Level, position: Vec3, source: SoundSource, vararg multiSound: MultiSound) {
        if(multiSound.isEmpty()) return
        multiSound.forEach {
            level.playSound(
                null,
                position.x,
                position.y,
                position.z,
                it.audio,
                source,
                it.volume,
                it.pitch
            )
        }

    }

    @JvmStatic
    @JvmOverloads
    public fun uiSound(sound: SoundEvent, volume: Float = 1F, pitch: Float = 1F) {
        val mc = ClientHelpers.getMinecraft()
        val ui = SimpleSoundInstance.forUI(sound, volume, pitch)
        mc.soundManager.play(ui)
    }

}
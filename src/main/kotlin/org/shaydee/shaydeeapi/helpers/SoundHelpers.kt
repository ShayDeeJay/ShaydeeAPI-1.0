package org.shaydee.shaydeeapi.helpers
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import org.openjdk.nashorn.internal.objects.NativeRegExp.source
import org.shaydee.shaydeeapi.data.MultiSound
import org.shaydee.shaydeeapi.networking.S2C.ClientSoundS2CP

public object SoundHelpers{

    public fun sendClientSound(
        serverPlayer: ServerPlayer,
        soundEvent: SoundEvent,
        volume: Float = 1F,
        pitch: Float = 1F,
        isLooping: Boolean = false,
    ) {
        PacketDistributor.sendToPlayer(serverPlayer, ClientSoundS2CP(soundEvent, volume, pitch, isLooping))
    }

    @JvmStatic
    @JvmOverloads
    public fun LivingEntity.entitySound(audio: SoundEvent, volume: Float = 1F, pitch: Float = 1F): Unit =
        level().playSound(null, x,y, z, audio, SoundSource.PLAYERS, volume, pitch)

    @JvmStatic
    @JvmOverloads
    public fun LivingEntity.soundWithPosition(sound: SoundEvent, source: SoundSource = SoundSource.PLAYERS, volume: Float = 1F, pitch: Float = 1F){
        getSoundWithPosition(this.level(), this.position(), sound, source, volume, pitch)
    }

    @JvmStatic
    @JvmOverloads
    public fun getSoundWithPosition(level: Level, position: Vec3, audio: SoundEvent, source: SoundSource = SoundSource.NEUTRAL, volume: Float = 1F, pitch: Float = 1F) {
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
    public fun uiSound(sound: SoundEvent, volume: Float = 1F, pitch: Float = 1F) {
        val mc = ClientHelpers.getMinecraft()
        val ui = SimpleSoundInstance.forUI(sound, pitch, volume)
        mc.soundManager.play(ui)
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
    @JvmName("multiSoundWithPosition")
    public fun multiSound(level: Level, position: Vec3, vararg multiSound: MultiSound, source: SoundSource = SoundSource.PLAYERS) {
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
    @JvmName("soundWithPositions")
    public fun Entity.multiSound(vararg multiSound: MultiSound) {
        if(multiSound.isEmpty()) return
        multiSound.forEach { this.playSound(it.audio, it.volume, it.pitch) }
    }

}
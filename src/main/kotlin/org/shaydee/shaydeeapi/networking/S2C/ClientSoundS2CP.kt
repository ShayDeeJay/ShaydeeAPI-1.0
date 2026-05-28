package org.shaydee.shaydeeapi.networking.S2C
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.Holder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.shaydee.shaydeeapi.Helpers
import org.shaydee.shaydeeapi.Helpers.res
import org.shaydee.shaydeeapi.ShaydeeAPI

public class ClientSoundS2CP(
    public val soundEvents: SoundEvent,
    public val volume: Float,
    public val pitch: Float,
    public val isLooping: Boolean
) : CustomPacketPayload {

    public companion object {
        public val TYPE: Type<ClientSoundS2CP> = Type(Helpers.apiRes("play_local_sound"))

        public val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ClientSoundS2CP> =
            CustomPacketPayload.codec(ClientSoundS2CP::toBytes, ::ClientSoundS2CP)
    }

    override fun type(): Type<out CustomPacketPayload> = TYPE

    public constructor(buf: FriendlyByteBuf) : this(
        buf.readJsonWithCodec(SoundEvent.CODEC).value(),
        buf.readFloat(),
        buf.readFloat(),
        buf.readBoolean()
    )

    public fun toBytes(buf: FriendlyByteBuf) {
        buf.writeJsonWithCodec(SoundEvent.CODEC, Holder.Direct(soundEvents))
        buf.writeFloat(volume)
        buf.writeFloat(pitch)
        buf.writeBoolean(isLooping)
    }

    public fun handle(ctx: IPayloadContext): Boolean {
        ctx.enqueueWork {
            val player = ctx.player()
            if (player is LocalPlayer) {
                if (!isLooping) {
                    player.playSound(soundEvents, volume, pitch)
                } else {
                    val clientLevel = player.level()
                    if (clientLevel.isClientSide) {
                        val sound = SimpleSoundInstance(
                            soundEvents.location, SoundSource.MUSIC, volume, pitch,
                            SoundInstance.createUnseededRandom(),
                            true, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true
                        )

                        val soundManager = Minecraft.getInstance().soundManager
                        soundManager.stop()
                        soundManager.play(sound)
                    }
                }
            }
        }
        return true
    }
}
package org.shaydee.shaydeeapi.networking
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import org.shaydee.shaydeeapi.ShaydeeAPI
import org.shaydee.shaydeeapi.networking.S2C.ClientSoundS2CP


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ShaydeeAPI.ID)
public object Network {
    @SubscribeEvent
    public fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(ShaydeeAPI.ID)
            .versioned("1.0.0")
            .optional()

        // S2C Packets
        registrar.playToClient(
            ClientSoundS2CP.TYPE,
            ClientSoundS2CP.STREAM_CODEC,
            ClientSoundS2CP::handle
        )
    }
}
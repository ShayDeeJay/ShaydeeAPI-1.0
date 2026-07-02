@file:Suppress("unused")
package org.shaydee.shaydeeapi

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.shaydee.shaydeeapi.events.ServerEvents
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(ShaydeeAPI.ID)
public class ShaydeeAPI{

    public companion object{
        public val LOGGER: Logger = LogManager.getLogger(ID)
        public const val ID : String = "shaydeeapi"
    }

    init {
        LOGGER.log(Level.INFO, "Initializing ShaydeeAPI!")
        ShaydeeAPIReg.PARTICLE_REGISTRY.register(MOD_BUS)
        EVENT_BUS.register(ServerEvents())
    }

    public fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    public fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    @SubscribeEvent
    public fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.log(Level.INFO, "Hello! This is working!")
    }

}

@file:Suppress("unused")
package org.shaydee.shaydeeapi

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.shaydee.shaydeeapi.registry.ShaydeeAPIReg
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(ShaydeeAPI.ID)
public class ShaydeeAPI{

    public companion object{
        public const val ID : String = "shaydeeapi"
    }

    public val logger: Logger = LogManager.getLogger(ID)

    init {
        ShaydeeAPIReg.PARTICLE_REGISTRY.register(MOD_BUS)
    }

    public fun onClientSetup(event: FMLClientSetupEvent) {
        logger.log(Level.INFO, "Initializing client...")
    }

    public fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        logger.log(Level.INFO, "Server starting...")
    }

    @SubscribeEvent
    public fun onCommonSetup(event: FMLCommonSetupEvent) {
        logger.log(Level.INFO, "Hello! This is working!")
    }

}

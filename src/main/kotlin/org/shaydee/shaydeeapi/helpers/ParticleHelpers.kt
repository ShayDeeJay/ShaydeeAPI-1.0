package org.shaydee.shaydeeapi.helpers

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.shaydee.shaydeeapi.particle.GenericParticleOption
import org.shaydee.shaydeeapi.particle.ParticleStore
import kotlin.math.*
import kotlin.random.Random

public object ParticleHelpers {

    @JvmStatic
    public fun genericParticle(
        lifetime: Int,
        size: Float,
        colourPrimary: Int,
        colourSecondary: Int,
    ): GenericParticleOption = GenericParticleOption(
        ParticleStore.MAGIC_PARTICLE,
        colourPrimary,
        colourSecondary,
        lifetime,
        size,
        false,
        1.0
    )

    @JvmStatic
    public fun genericParticle(
        particleType: Int,
        colourPrimary: Int,
        colourFade: Int,
        lifetime: Int,
        size: Float,
        staticSize: Boolean,
        speed: Double,
    ): GenericParticleOption = GenericParticleOption(
        particleType,
        colourPrimary,
        colourFade,
        lifetime,
        size,
        staticSize,
        speed
    )

    @JvmStatic
    public fun genericParticle(
        particleType: Int,
        lifetime: Int,
        size: Float,
        colourPrimary: Int,
        colourSecondary: Int,
    ): GenericParticleOption = GenericParticleOption(
        particleType,
        colourPrimary,
        colourSecondary,
        lifetime,
        size,
        false,
        1.0
    )

    @JvmStatic
    public fun genericParticle(
        particleType: Int,
        lifetime: Int,
        size: Float,
        colourPrimary: Int,
        colourSecondary: Int,
        setStaticSize: Boolean,
    ): GenericParticleOption = GenericParticleOption(
        particleType,
        colourPrimary,
        colourSecondary,
        lifetime,
        size,
        setStaticSize,
        1.0
    )

    @JvmStatic
    public fun particleBurst(
        world: Level,
        pos: Vec3,
        particleCount: Int,
        particleOptions: ParticleOptions,
        x: Double,
        y: Double,
        z: Double,
        speed: Float,
    ) {
        repeat(10) {
            sendParticles(world, particleOptions, pos, particleCount, x, y, z, speed.toDouble())
        }
    }

    @JvmStatic
    public fun particleBurst(
        world: Level,
        pos: Vec3,
        particleCount: Int,
        particleOptions: ParticleOptions,
        x: Double,
        y: Double,
        z: Double,
        speed: Float,
        totalPoofs: Int,
    ) {
        repeat(totalPoofs) {
            sendParticles(world, particleOptions, pos, particleCount, x, y, z, speed.toDouble())
        }
    }

    @JvmStatic
    public fun invisibleLight(
        world: Level,
        loc: Vec3,
        particleOptions: ParticleOptions,
        bound1: Double,
        bound2: Double,
        speed: Int,
    ) {
        repeat(3) {
            sendParticles(
                world,
                particleOptions,
                loc,
                0,
                0.0,
                Random.nextDouble(bound1, bound2),
                0.0,
                speed.toDouble()
            )
        }
    }

    @JvmStatic
    public fun getNonBakedParticles(
        colour1: Int,
        colour2: Int,
        lifetime: Int,
        size: Float,
        speed: Double = 1.0,
    ): ParticleOptions {
        val generic = genericParticle(ParticleStore.GENERIC_PARTICLE, colour1, colour2, lifetime, size, false, speed)
        val magic = genericParticle(ParticleStore.MAGIC_PARTICLE, colour1, colour2, lifetime, size, false, speed)
        val soft = genericParticle(ParticleStore.SOFT_PARTICLE, colour1, colour2, lifetime, size, false, speed)
        val square = genericParticle(ParticleStore.SQUARE_PARTICLE, colour1, colour2, lifetime, size, false, speed)

        val collectTypes = listOf(generic, magic, soft, square)
        return collectTypes.random()
    }

    @JvmStatic
    public fun particleBurst(
        world: Level,
        pos: Vec3,
        particleCount: Int,
        particleOptions: ParticleOptions,
    ) {
        repeat(5) {
            sendParticles(
                world, particleOptions, pos, particleCount,
                (Random.nextFloat() - 0.5) / 3,
                (Random.nextFloat() - 0.5) / 3,
                (Random.nextFloat() - 0.5) / 3,
                0.1
            )
        }
    }

    public fun getRandomParticleVelocity(speed: Double): Vec3 {
        val theta: Double = Random.nextDouble() * 2 * Math.PI
        val phi: Double = Random.nextDouble() * Math.PI
        val x = sin(phi) * cos(theta)
        val y = cos(phi)
        val z = sin(phi) * sin(theta)
        return Vec3(x, y, z).normalize().scale(speed)
    }

    @JvmStatic
    public fun particleBurst(
        world: Level,
        pos: Vec3,
        particleCount: Int,
        particleOptions: ParticleOptions,
        speed: Float,
    ) {
        repeat(5) {
            sendParticles(
                world, particleOptions, pos, particleCount,
                (Random.nextFloat() - 0.5) / 3,
                (Random.nextFloat() - 0.5) / 3,
                (Random.nextFloat() - 0.5) / 3,
                speed.toDouble()
            )
        }
    }

    @JvmStatic
    public fun genericProjPart(
        world: Level,
        pos: Vec3,
        particleCount: Int,
        particleOptions: ParticleOptions,
        speed: Float,
    ) {
        repeat(2) {
            val spread = 0.2
            sendParticles(
                world, particleOptions, pos, particleCount,
                (Random.nextFloat() - spread) / 3,
                (Random.nextFloat() - spread) / 3,
                (Random.nextFloat() - spread) / 3,
                speed.toDouble()
            )
        }
    }

    @JvmStatic
    public fun <T : ParticleOptions> sendParticles(
        level: Level,
        type: T,
        positions: Vec3,
        pParticleCount: Int,
        xOff: Double,
        yOff: Double,
        zOff: Double,
        speed: Double,
    ): Int {
        if (level is ServerLevel) {
            val packet = ClientboundLevelParticlesPacket(
                type,
                false,
                positions.x,
                positions.y,
                positions.z,
                xOff.toFloat(),
                yOff.toFloat(),
                zOff.toFloat(),
                speed.toFloat(),
                pParticleCount
            )
            var sentCount = 0
            for (serverPlayer in level.players()) {
                if (sendParticles(serverPlayer, positions.x, positions.y, positions.z, packet)) {
                    sentCount++
                }
            }
            return sentCount
        } else {
            level.addParticle(type, positions.x, positions.y, positions.z, xOff / 100 * speed, yOff / 100 * speed, zOff / 100 * speed)
            return 0
        }
    }

    @JvmStatic
    public fun sendParticles(
        player: ServerPlayer,
        posX: Double,
        posY: Double,
        posZ: Double,
        packet: Packet<*>,
    ): Boolean {
        if (player.level().isClientSide) return false
        val blockPos = player.blockPosition()
        return if (blockPos.closerToCenterThan(Vec3(posX, posY, posZ), 64.0)) {
            player.connection.send(packet)
            true
        } else false
    }

    @JvmStatic
    public fun spawnElectrifiedParticles(
        level: Level,
        position: Vec3,
        particleType: ParticleOptions,
        count: Int,
        livingEntity: LivingEntity,
        speed: Double,
    ) {
        repeat(count) {
            val offsetX = (Random.nextDouble() - 0.5) * livingEntity.bbWidth
            val offsetY = Random.nextDouble() * livingEntity.bbHeight
            val offsetZ = (Random.nextDouble() - 0.5) * livingEntity.bbWidth

            val speedX = (Random.nextDouble() - 0.5) * 0.1
            val speedY = (Random.nextDouble() - 0.5) * 0.1
            val speedZ = (Random.nextDouble() - 0.5) * 0.1

            sendParticles(level, particleType, position.add(offsetX, offsetY, offsetZ), 1, speedX, speedY, speedZ, speed)
        }
    }

    @JvmStatic
    public fun spawnElectrifiedParticles(
        level: ServerLevel,
        position: Vec3,
        particleType: ParticleOptions,
        count: Int,
        livingEntity: LivingEntity,
        speed: Double,
        ySpeed: Double,
    ) {
        repeat(count) {
            val offsetX = (Random.nextDouble() - 0.5) * livingEntity.bbWidth
            val offsetY = Random.nextDouble() * livingEntity.bbHeight
            val offsetZ = (Random.nextDouble() - 0.5) * livingEntity.bbWidth

            val speedX = (Random.nextDouble() - 0.5) * 0.1
            val speedY = (Random.nextDouble() - 0.5) * 0.1
            val speedZ = (Random.nextDouble() - 0.5) * 0.1

            sendParticles(level, particleType, position.add(offsetX, offsetY, offsetZ), 1, speedX, speedY + ySpeed, speedZ, speed)
        }
    }

    @JvmStatic
    public fun playParticles(
        particleOptions: ParticleOptions,
        projectile: Projectile,
        getX: Double,
        getY: Double,
        getZ: Double,
    ) {
        val deltaX = getX - projectile.xOld
        val deltaY = getY - projectile.yOld
        val deltaZ = getZ - projectile.zOld
        val dist = ceil(sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 5)
        val maxDist = max(dist, 5.0).toInt()
        for (j in 0 until maxDist) {
            val coeff = j / dist
            val position = Vec3(
                (projectile.xo + deltaX * coeff),
                (projectile.yo + deltaY * coeff) + 0.1f,
                (projectile.zo + deltaZ * coeff)
            )
            sendParticles(
                projectile.level(),
                particleOptions,
                position,
                1,
                0.0125f * (Random.nextFloat() - 0.5),
                0.0125f * (Random.nextFloat() - 0.5),
                0.0125f * (Random.nextFloat() - 0.5),
                0.0
            )
        }
    }

    @JvmStatic
    public fun playParticles2(
        particleOptions: ParticleOptions,
        projectile: Projectile,
        getX: Double,
        getY: Double,
        getZ: Double,
        speed: Double,
    ) {
        val deltaX = getX - projectile.xOld
        val deltaY = getY - projectile.yOld
        val deltaZ = getZ - projectile.zOld
        val dist = ceil(sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 5)
        for (j in 0 until dist.toInt()) {
            val coeff = j / dist
            val position = Vec3(
                (projectile.xo + deltaX * coeff),
                (projectile.yo + deltaY * coeff) + 0.1,
                (projectile.zo + deltaZ * coeff)
            )
            sendParticles(
                projectile.level(),
                particleOptions,
                position,
                2,
                0.0125f * (Random.nextFloat() - 0.5),
                0.0125f * (Random.nextFloat() - 0.5),
                0.0125f * (Random.nextFloat() - 0.5),
                speed
            )
        }
    }

    @JvmStatic
    public fun playParticles3(
        particleOptions: ParticleOptions,
        projectile: Projectile,
        multiplier: Int,
        speed: Double,
    ) {
        val deltaX = projectile.x - projectile.xOld
        val deltaY = projectile.y - projectile.yOld
        val deltaZ = projectile.z - projectile.zOld
        val dist = ceil(sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * multiplier)
        if (projectile.level() is ServerLevel) {
            val serverLevel = projectile.level() as ServerLevel
            for (j in 0 until dist.toInt()) {
                val coeff = j / dist
                val position = Vec3(
                    (projectile.xo + deltaX * coeff),
                    (projectile.yo + deltaY * coeff) + 0.1f,
                    (projectile.zo + deltaZ * coeff)
                )
                sendParticles(
                    serverLevel,
                    particleOptions,
                    position,
                    1,
                    0.0125f * (Random.nextFloat() - 0.5),
                    0.0125f * (Random.nextFloat() - 0.5),
                    0.0125f * (Random.nextFloat() - 0.5),
                    speed
                )
            }
        }
    }

    @JvmStatic
    public fun genericProjectile(
        projectile: Projectile,
        particleMain: ParticleOptions,
        particleTrail: ParticleOptions,
        speed: Double,
    ) {
        if (projectile.tickCount > 1) {
            val directionX = projectile.x - projectile.xOld
            val directionY = projectile.y - projectile.yOld
            val directionZ = projectile.z - projectile.zOld
            val magnitude = sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ)
            val normalizedX = directionX / magnitude
            val normalizedY = directionY / magnitude
            val normalizedZ = directionZ / magnitude
            val offsetDistance = 0.8
            val offsetX = projectile.x - normalizedX * offsetDistance
            val offsetY = projectile.y - normalizedY * offsetDistance
            val offsetZ = projectile.z - normalizedZ * offsetDistance

            playParticles(particleMain, projectile, offsetX, offsetY, offsetZ)
            playParticles2(particleTrail, projectile, offsetX, offsetY, offsetZ, speed)
        }
    }

}
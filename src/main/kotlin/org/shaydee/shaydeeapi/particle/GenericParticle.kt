package org.shaydee.shaydeeapi.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SimpleAnimatedParticle
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

import kotlin.random.Random

public open class GenericParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xSpeed: Double,
    ySpeed: Double,
    zSpeed: Double,
    sprite: SpriteSet
) : SimpleAnimatedParticle(level, x, y, z, sprite, 0.0125f) {

    init {
        xd = xSpeed
        yd = ySpeed
        zd = zSpeed
        quadSize *= 0.55f
        lifetime = 10 + random.nextInt(10)
        pickSprite(sprite)
        hasPhysics = false
    }

    public override fun getLightColor(pPartialTick: Float): Int = FULL_BRIGHT

    public override fun getRenderType(): ParticleRenderType = ParticleRenderTypes.ABILITY_RENDERER

    @OnlyIn(Dist.CLIENT)
    public class GenericProvider(private val sprites: SpriteSet) : ParticleProvider<GenericParticleOption> {
        public override fun createParticle(
            type: GenericParticleOption,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val genericParticle = object : GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites) {
                var tick = 0

                public override fun tick() {
                    super.tick()
                    tick++
                    if (!type.setStaticSize) this.quadSize *= 0.9f
                    this.speedUpWhenYMotionIsBlocked = true
                    if (tick % 4 == 0) setSprite(sprites.get(random.fork()))
                }
            }

            if (type.setStaticSize) genericParticle.quadSize = type.size else genericParticle.quadSize *= type.size
            genericParticle.setColor(type.colour)
            genericParticle.setFadeColor(type.fade)
            genericParticle.lifetime = type.lifetime + Random.nextInt(type.lifetime)
            return genericParticle
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class PlusParticle(private val sprites: SpriteSet) : ParticleProvider<GenericParticleOption> {
        public override fun createParticle(
            type: GenericParticleOption,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val genericParticle = object : GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites) {
                var tick = 0

                public override fun tick() {
                    super.tick()
                    tick++
                    if (!type.setStaticSize) this.quadSize *= 0.96f
                    this.speedUpWhenYMotionIsBlocked = true
                }
            }

            genericParticle.gravity = 0f
            genericParticle.setColor(type.colour)
            genericParticle.setFadeColor(type.fade)
            genericParticle.lifetime = type.lifetime + Random.nextInt(type.lifetime)
            return genericParticle
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class ElectricalParticle(private val sprites: SpriteSet) : ParticleProvider<GenericParticleOption> {
        public override fun createParticle(
            type: GenericParticleOption,
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double
        ): Particle {
            val genericParticle = object : GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites) {
                public override fun tick() {
                    super.tick()
                    this.quadSize *= 0.9f

                    val randX = (random.nextDouble() - 0.5) * 0.5
                    val randY = (random.nextDouble() - 0.5) * 0.5
                    val randZ = (random.nextDouble() - 0.5) * 0.5
                    this.setPos(this.x + randX, this.y + randY, this.z + randZ)

                    this.xd += (random.nextDouble() - 0.5) * 0.6
                    this.yd += (random.nextDouble() - 0.5) * 0.6
                    this.zd += (random.nextDouble() - 0.5) * 0.6

                    this.xd *= type.speed
                    this.yd *= type.speed
                    this.zd *= type.speed

                    this.speedUpWhenYMotionIsBlocked = true
                }
            }

            if (type.setStaticSize) genericParticle.quadSize = type.size else genericParticle.quadSize *= type.size
            genericParticle.setColor(type.colour)
            genericParticle.setFadeColor(type.fade)
            genericParticle.lifetime = type.lifetime + Random.nextInt(type.lifetime)
            return genericParticle
        }
    }


}

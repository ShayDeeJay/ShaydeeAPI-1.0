package org.shaydee.shaydeeapi.particle

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.advancements.critereon.MovementPredicate.speed
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.*
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SimpleAnimatedParticle
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import org.shaydee.shaydeeapi.helpers.ParticleHelpers.genericParticle
import org.shaydee.shaydeeapi.particle.MovingParticle.Companion.intToRGB
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

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
            return when(type.animationType){
                ParticleStore.MOVE_TO -> moving(level, x, y, z, xSpeed, ySpeed, zSpeed, type)
                ParticleStore.SPIRAL -> spiral(level, x, y, z, xSpeed, ySpeed, zSpeed, type)
                ParticleStore.FLOAT_AROUND -> hoverAround(level, x, y, z, xSpeed, ySpeed, zSpeed, type)
                else -> standard(level, x, y, z, xSpeed, ySpeed, zSpeed, type)
            }
        }

        private fun standard(
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double,
            type: GenericParticleOption,
        ): GenericParticle {
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
            setColour(type, genericParticle)
            genericParticle.lifetime = type.lifetime + Random.nextInt(type.lifetime)
            return genericParticle
        }

        private fun setColour(
            type: GenericParticleOption,
            genericParticle: GenericParticle,
        ) {
            if (type.colour != 0) {
                genericParticle.setColor(type.colour)
                genericParticle.setFadeColor(type.fade)
            }
        }

        private fun moving(
            level: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double,
            type: GenericParticleOption,
            lifetimeAlpha: LifetimeAlpha = LifetimeAlpha.ALWAYS_OPAQUE
        ): GenericParticle {
            val genericParticle = object : GenericParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites) {
                public val xStart: Double = x
                public val yStart: Double = y
                public val zStart: Double = z

                init {
                    alpha = lifetimeAlpha.startAlpha()
                    xd = xSpeed
                    yd = ySpeed
                    zd = zSpeed
                    xo = x + xSpeed
                    yo = y + ySpeed
                    zo = z + zSpeed
                    this.x = xo
                    this.y = yo
                    this.z = zo
                    quadSize = 0.1f * (random.nextFloat() * 0.5f + 0.2f)
                    val f = random.nextFloat() * 0.6f + 0.4f
                    rCol = f
                    gCol = f
                    bCol = f
                    hasPhysics = false
                    lifetime = (Random.nextDouble() * 10.0).toInt() + 30
                }

                public override fun move(x: Double, y: Double, z: Double) {
                    boundingBox = boundingBox.move(x, y, z)
                    this.setLocationFromBoundingbox()
                }

                public override fun render(buffer: VertexConsumer, renderInfo: Camera, partialTicks: Float) {
                    alpha = lifetimeAlpha.currentAlphaForAge(age, lifetime, partialTicks)
                    super.render(buffer, renderInfo, partialTicks)
                }

                public override fun tick() {
                    xo = this.x
                    yo = this.y
                    zo = this.z
                    if (age++ >= lifetime) {
                        remove()
                    } else {
                        var f = age.toFloat() / lifetime.toFloat()
                        f = 1.0f - f
                        var f1 = 1.0f - f
                        f1 *= f1
                        f1 *= f1
                        this.x = xStart + xd * f
                        this.y = yStart + yd * f - (f1 * 1.2f)
                        this.z = zStart + zd * f
                        quadSize = type.size / 2 * f
                    }
                    speedUpWhenYMotionIsBlocked = true

                }
            }

            if (type.setStaticSize) {
                genericParticle.quadSize = type.size
            } else {
                genericParticle.quadSize *= type.size
            }

            genericParticle.alpha = 1f
            setColour(type, genericParticle)
            genericParticle.lifetime = type.lifetime
            return genericParticle
        }

        public fun spiral(
            level: ClientLevel,
            x: Double, y: Double, z: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double,
            type: GenericParticleOption,
        ): Particle {

            val startAngle = Random.nextDouble(0.0, Math.PI * 2)
            val radius = xSpeed
            val startX = x + kotlin.math.cos(startAngle) * radius
            val startZ = z + kotlin.math.sin(startAngle) * radius
            val particle = object : GenericParticle(level, startX, y, startZ, 0.0, 0.0, 0.0, sprites) {

                private val centerX = x
                private val centerZ = z

                private var currentAngle = startAngle
                private val rotationSpeed = 0.45 * type.speed  // How fast it spins (radians per tick)
                private val upwardSpeed = 0.05 * ySpeed     // How fast it floats up

                override fun tick() {
                    currentAngle += rotationSpeed

                    val targetX = centerX + kotlin.math.cos(currentAngle) * radius
                    val targetZ = centerZ + kotlin.math.sin(currentAngle) * radius

                    this.xd = targetX - this.x
                    this.zd = targetZ - this.z
                    this.yd = upwardSpeed

                    super.tick()

                    if (!type.setStaticSize) {
                        this.quadSize *= 0.95f
                    }
                }
            }

            when (type.setStaticSize) {
                true -> particle.quadSize = type.size
                else -> particle.quadSize *= type.size
            }

            setColour(type, particle)
            particle.lifetime = type.lifetime + Random.nextInt(type.lifetime)
            particle.gravity = 0f
            return particle
        }

        public fun hoverAround(
            level: ClientLevel,
            centerX: Double,
            centerY: Double,
            centerZ: Double,
            xSpeed: Double,
            ySpeed: Double,
            zSpeed: Double,
            type: GenericParticleOption,
        ): Particle {
            val axis = Vec3(
                Random.nextDouble(-1.0, 1.0),
                Random.nextDouble(-1.0, 1.0),
                Random.nextDouble(-1.0, 1.0)
            ).normalize()

            // Build a fixed orthonormal basis (u, v) spanning the plane perpendicular to axis.
            // Use a helper vector that's never near-parallel to axis, to avoid a degenerate cross product.
            val helper = if (kotlin.math.abs(axis.y) < 0.99) Vec3(0.0, 1.0, 0.0) else Vec3(1.0, 0.0, 0.0)
            val u = axis.cross(helper).normalize()
            val v = axis.cross(u).normalize() // already unit length since axis ⟂ u

            val rotationSpeed = 0.25 * (if (Random.nextBoolean()) ySpeed else -ySpeed)
            var currentAngle = Random.nextDouble(0.0, Math.PI * 2)

            fun pointAt(angle: Double): Vec3 {
                val c = kotlin.math.cos(angle)
                val s = kotlin.math.sin(angle)
                return Vec3(
                    centerX + (u.x * c + v.x * s) * xSpeed,
                    centerY + (u.y * c + v.y * s) * xSpeed,
                    centerZ + (u.z * c + v.z * s) * xSpeed
                )
            }

            val start = pointAt(currentAngle)

            val particle = object : GenericParticle(
                level, start.x, start.y, start.z, 0.0, 0.0, 0.0, sprites
            ) {
                override fun tick() {
                    currentAngle += rotationSpeed
                    val target = pointAt(currentAngle)

                    this.xd = target.x - this.x
                    this.yd = target.y - this.y
                    this.zd = target.z - this.z

                    super.tick()

                    if (!type.setStaticSize) {
                        this.quadSize *= 0.95f
                    }
                }
            }

            if (type.setStaticSize) {
                particle.quadSize = type.size
            } else {
                particle.quadSize *= type.size
            }

            setColour(type, particle)

            particle.lifetime = type.lifetime + Random.nextInt(type.lifetime)
            particle.gravity = 0f

            return particle
        }

    }

}

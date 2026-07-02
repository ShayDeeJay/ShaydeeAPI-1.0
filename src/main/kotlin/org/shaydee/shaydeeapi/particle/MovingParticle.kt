package org.shaydee.shaydeeapi.particle

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.LightTexture.FULL_BRIGHT
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import kotlin.random.Random

@OnlyIn(Dist.CLIENT)
public open class MovingParticle(
    level: ClientLevel,
    x: Double,
    y: Double,
    z: Double,
    xSpeed: Double,
    ySpeed: Double,
    zSpeed: Double,
    private val isGlowing: Boolean = false,
    private val lifetimeAlpha: LifetimeAlpha = LifetimeAlpha.ALWAYS_OPAQUE
) : TextureSheetParticle(level, x, y, z) {

    private val xStart: Double = x
    private val yStart: Double = y
    private val zStart: Double = z

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

    public override fun getRenderType(): ParticleRenderType = ParticleRenderTypes.ABILITY_RENDERER

    public override fun move(x: Double, y: Double, z: Double) {
        boundingBox = boundingBox.move(x, y, z)
        this.setLocationFromBoundingbox()
    }

    public override fun getLightColor(partialTick: Float): Int = FULL_BRIGHT

    public override fun tick() {
        xo = x
        yo = y
        zo = z
        if (age++ >= lifetime) {
            remove()
        } else {
            var f = age.toFloat() / lifetime.toFloat()
            f = 1.0f - f
            var f1 = 1.0f - f
            f1 *= f1
            f1 *= f1
            x = xStart + xd * f
            y = yStart + yd * f - (f1 * 1.2f)
            z = zStart + zd * f
        }
    }

    public override fun render(buffer: VertexConsumer, renderInfo: Camera, partialTicks: Float) {
        alpha = lifetimeAlpha.currentAlphaForAge(age, lifetime, partialTicks)
        super.render(buffer, renderInfo, partialTicks)
    }

    public companion object {
        public fun intToRGB(color: Int): IntArray {
            val red = (color shr 16) and 0xFF
            val green = (color shr 8) and 0xFF
            val blue = color and 0xFF
            return intArrayOf(red, green, blue)
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class EnchantProvider(private val sprite: SpriteSet) : ParticleProvider<GenericParticleOption> {
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

            val movingParticle = object : MovingParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {
                var tick = 0
                public override fun tick() {
                    super.tick()
                    tick++
                    if (!type.setStaticSize) quadSize *= 0.9f
                    quadSize = kotlin.math.min(type.size, tick / 100f)
                    speedUpWhenYMotionIsBlocked = true
                }
            }

            val (r, g, b) = intToRGB(type.colour)

            if (type.setStaticSize) {
                movingParticle.quadSize = type.size
            } else {
                movingParticle.quadSize *= type.size
            }

            movingParticle.alpha = 1f
            movingParticle.pickSprite(sprite)
            movingParticle.setColor(r / 255f, g / 255f, b / 255f)
            movingParticle.lifetime = type.lifetime
            return movingParticle
        }
    }
}

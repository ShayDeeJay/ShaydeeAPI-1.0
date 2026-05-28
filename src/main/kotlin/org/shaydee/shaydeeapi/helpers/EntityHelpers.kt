package org.shaydee.shaydeeapi.helpers

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.CommonHooks
import java.util.function.Consumer
import kotlin.math.*
import kotlin.random.Random

public object EntityHelpers {

    @JvmStatic
    public fun canPathfindToTarget(finder: Mob, target: LivingEntity): Boolean {
        val path = finder.getNavigation().createPath(target, 0)
        return path != null
    }

    @JvmStatic
    public fun hasLineOfSight(pathfinder: Entity, target: Entity): Boolean {
        if (target.level() !== pathfinder.level()) return false

        val vec3 = Vec3(pathfinder.x, pathfinder.eyeY, pathfinder.z)
        val vec31 = Vec3(target.x, target.y, target.z)
        val context = ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pathfinder)
        return !(vec31.distanceTo(vec3) > 128.0) && pathfinder.level().clip(context).type == HitResult.Type.MISS
    }

    @JvmStatic
    public fun Entity.entitiesInRange(
        bounding: AABB = this.boundingBox,
        classType: Class<out LivingEntity> = LivingEntity::class.java,
        conditions: TargetingConditions = TargetingConditions.forCombat(),
        doOnCall: (LivingEntity) -> Unit,
    ): Unit = this.level().getNearbyEntities(classType, conditions, this as? LivingEntity, bounding).forEach(doOnCall)

    @JvmStatic
    public fun Entity.nearestEntity(
        pos: Vec3,
        bounding: AABB = this.boundingBox,
        classType: Class<out LivingEntity> = LivingEntity::class.java,
        conditions: TargetingConditions = TargetingConditions.forCombat(),
    ): LivingEntity? = this.level().getNearestEntity(classType, conditions, null, pos.x, pos.y, pos.z, bounding)

    @JvmStatic
    public fun knockback(targetEntity: LivingEntity, pStrength: Double, xRatio: Double, zRatio: Double) {
        val event = CommonHooks.onLivingKnockBack(targetEntity, pStrength.toFloat(), xRatio, zRatio)
        if (event.isCanceled) return

        val finalX = event.ratioX
        val finalZ = event.ratioZ
        val resist = targetEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE).coerceIn(0.0, 1.0)

        val resistanceMultiplier = 1.0 - (resist * 0.9)
        val newVal = pStrength * resistanceMultiplier

        targetEntity.hasImpulse = true
        val currentMotion = targetEntity.deltaMovement
        val knockbackVec = Vec3(finalX, 0.0, finalZ).normalize().scale(newVal)

        targetEntity.setDeltaMovement(
            currentMotion.x / 2.0 - knockbackVec.x,
            if (targetEntity.onGround()) {
                min(0.8, currentMotion.y / 2.0 + newVal)
            } else {
                currentMotion.y
            },
            currentMotion.z / 2.0 - knockbackVec.z
        )
    }

    public fun getSphericalPositions(
        position: Vec3,
        radius: Double,
        numPoints: Int,
        action: Consumer<Vec3>,
    ) {
        val phiIncrement = PI * (3.0 - sqrt(5.0))

        for (i in 0 until numPoints) {
            val t = i.toDouble() / (numPoints - 1)
            val y = 1.0 - t * 2.0

            val radiusAtHeight = sqrt(1.0 - y * y) * radius

            val phi = i * phiIncrement

            val x = cos(phi) * radiusAtHeight
            val z = sin(phi) * radiusAtHeight

            val pos = Vec3(
                position.x + x,
                position.y + (y * radius),
                position.z + z
            )
            action.accept(pos)
        }
    }

    public fun Entity.moveEntitiesRelative(numberOfPoints: Int, method: (Vec3) -> Unit) {
        for (i in 0..numberOfPoints) {
            val angle = 2 * PI * i / numberOfPoints

            val x = direction.stepX + numberOfPoints * cos(angle)
            val y = direction.stepY.toDouble()
            val z = direction.stepZ + numberOfPoints * sin(angle)

            method(Vec3(x, y, z))
        }
    }

    public fun getRandomSphericalPositions(
        position: Vec3,
        radius: Double,
        numPoints: Int,
        action: Consumer<Vec3>,
    ) {
        repeat(numPoints) {
            val theta = 2.0 * PI * Random.nextDouble()
            val cosPhi = 2.0 * Random.nextDouble() - 1.0
            val sinPhi = sqrt(1.0 - cosPhi * cosPhi)

            val x = radius * sinPhi * cos(theta)
            val y = radius * sinPhi * sin(theta)
            val z = radius * cosPhi

            action.accept(Vec3(position.x + x, position.y + y, position.z + z))
        }
    }

    @JvmStatic
    public fun entityMoverCenter(
        receiver: Entity,
        target: Entity,
        velocity: Double = 1.0,
        resistance: Double = 0.86,
        yOffset: Double = 0.4
    ) {
        var directionX = receiver.x - target.x
        var directionY = receiver.y - target.y - yOffset
        var directionZ = receiver.z - target.z

        val directionLength = sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ)

        if (directionLength > 0.0) {
            directionX /= directionLength
            directionY /= directionLength
            directionZ /= directionLength
        }

        directionX *= velocity
        directionY *= velocity
        directionZ *= velocity

        val currentSpeed = target.deltaMovement
        val newSpeedX = directionX * (1 - resistance) + currentSpeed.x * resistance
        val newSpeedY = directionY * (1 - resistance) + currentSpeed.y * resistance
        val newSpeedZ = directionZ * (1 - resistance) + currentSpeed.z * resistance

        target.setDeltaMovement(newSpeedX, newSpeedY, newSpeedZ)
    }
}
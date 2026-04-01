package org.shaydee.shaydeeapi.helpers

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import java.util.function.Consumer
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

public object EntityHelpers {

    public fun getSphericalPositions(
        position: Vec3,
        radius: Double,
        numPoints: Int,
        action: Consumer<Vec3>
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

    public fun getRandomSphericalPositions(
        position: Vec3,
        radius: Double,
        numPoints: Int,
        action: Consumer<Vec3>
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
    public fun hasLineOfSight(pathfinder: Entity, target: Entity): Boolean {
        if (target.level() !== pathfinder.level()) return false

        val vec3 = Vec3(pathfinder.x, pathfinder.eyeY, pathfinder.z)
        val vec31 = Vec3(target.x, target.y, target.z)
        val context = ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pathfinder)
        return !(vec31.distanceTo(vec3) > 128.0) && pathfinder.level().clip(context).type == HitResult.Type.MISS
    }

    @JvmStatic
    public fun canPathfindToTarget(finder: Mob, target: LivingEntity): Boolean {
        val path = finder.getNavigation().createPath(target, 0)
        return path != null
    }
}
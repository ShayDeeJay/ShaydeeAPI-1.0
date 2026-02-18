package org.shaydee.shaydeeapi.helpers

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

public object EntityHelpers {

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
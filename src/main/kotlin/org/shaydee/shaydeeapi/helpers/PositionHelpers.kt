package org.shaydee.shaydeeapi.helpers
import net.minecraft.core.BlockPos
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.*
import kotlin.random.Random

public object PositionHelpers {

    public fun getOuterRingOfRadiusList(position: Vec3, radius: Double, points: Int): List<Vec3> = buildList {
        getOuterRingOfRadius(position, radius, points) { add(it) }
    }

    public inline fun innerRadiusRandom(blockPos: BlockPos, radius: Double, numPoints: Int, action: (Vec3) -> Unit) {
        innerRadiusRandom(blockPos.center, radius, numPoints, action)
    }

    public fun innerRadiusRandomList(position: Vec3, radius: Double, numPoints: Int): List<Vec3> = buildList {
        innerRadiusRandom(position, radius, numPoints) { add(it) }
    }

    public inline fun getRandomSphericalPositions(entity: Entity, radius: Double, numPoints: Int, action: (Vec3) -> Unit) {
        getRandomSphericalPositions(entity.position(), radius, numPoints, action)
    }

    public fun getRandomSphericalPositionsList(position: Vec3, radius: Double, numPoints: Int): List<Vec3> = buildList {
        getRandomSphericalPositions(position, radius, numPoints) { add(it) }
    }

    public fun nbtDoubleList(vararg pNumbers: Double): ListTag {
        val tagList = ListTag()
        for (d0 in pNumbers) tagList.add(DoubleTag.valueOf(d0))
        return tagList
    }

    public inline fun getOuterRingOfRadius(position: Vec3, radius: Double, points: Int, action: (Vec3) -> Unit) {
        val stepSize = 2 * PI / points
        for (i in 0 until points) {
            val theta = i * stepSize
            action(Vec3(position.x + radius * cos(theta), position.y, position.z + radius * sin(theta)))
        }
    }

    public inline fun getOuterRingOfRadiusRandom(position: Vec3, radius: Double, pointsDensity: Double, action: (Vec3) -> Unit) {
        val numberOfPoints = (pointsDensity * radius).toInt()
        for (i in 0 until numberOfPoints) {
            val theta = Random.nextDouble() * 2 * PI
            action(Vec3(position.x + radius * cos(theta), position.y, position.z + radius * sin(theta)))
        }
    }

    public inline fun innerRadiusRandom(position: Vec3, radius: Double, numPoints: Int, action: (Vec3) -> Unit) {
        val sectorAngle = 2 * PI / numPoints
        for (i in 0 until numPoints) {
            val angle = i * sectorAngle + Random.nextDouble() * sectorAngle
            val distance = sqrt(Random.nextDouble()) * radius
            action(Vec3(position.x + distance * cos(angle), position.y, position.z + distance * sin(angle)))
        }
    }

    public inline fun getOuterSquareOfRadius(position: Vec3, radius: Double, points: Int, action: (Vec3) -> Unit) {
        val pointsPerSide = points / 4
        val stepSize = (2 * radius) / pointsPerSide

        for (i in 0 until pointsPerSide) {
            val offset = i * stepSize
            action(Vec3(position.x - radius, position.y, position.z - radius + offset))
            action(Vec3(position.x - radius + offset, position.y, position.z + radius))
            action(Vec3(position.x + radius, position.y, position.z + radius - offset))
            action(Vec3(position.x + radius - offset, position.y, position.z - radius))
        }
    }

    public inline fun getSphericalPositions(entity: Entity, radius: Double, numPoints: Int, action: (Vec3) -> Unit) {
        val entityPos = entity.position()
        val phiIncrement = PI * (3.0 - sqrt(5.0))

        for (i in 0 until numPoints) {
            val y = 1 - (i / (numPoints - 1.0)) * 2
            val radiusAtHeight = sqrt(1 - y * y) * radius
            val phi = i * phiIncrement

            action(Vec3(entityPos.x + cos(phi) * radiusAtHeight, entityPos.y + y * radius, entityPos.z + sin(phi) * radiusAtHeight))
        }
    }

    public fun getRandomSphericalBlockPositions(center: BlockPos, radius: Double, numPoints: Int): List<BlockPos> = buildList {
        for (i in 0 until numPoints) {
            val theta = 2 * PI * Random.nextDouble()
            val phi = acos(2 * Random.nextDouble() - 1)

            val x = radius * sin(phi) * cos(theta)
            val y = radius * sin(phi) * sin(theta)
            val z = radius * cos(phi)

            add(center.offset(x.roundToInt(), y.roundToInt(), z.roundToInt()))
        }
    }


    public fun getInnerRingOfRadius(entity: Entity, radius: Double): List<Vec3> {
        val playerPos = entity.position()
        val radiusInt = Mth.floor(radius)
        val radiusSq = radius * radius

        return buildList {
            for (x in -radiusInt..radiusInt) {
                for (z in -radiusInt..radiusInt) {
                    if (x * x + z * z <= radiusSq) {
                        add(Vec3(playerPos.x + x, playerPos.y, playerPos.z + z))
                    }
                }
            }
        }
    }

    public fun AABB.getBlocksInAABB(): List<BlockPos> {
        val min = BlockPos.containing(minX, minY, minZ)
        val max = BlockPos.containing(maxX, maxY, maxZ)

        return BlockPos.betweenClosed(min, max)
            .map { it.immutable() }
    }

    public fun AABB.getBlockStatesInAABB(level: Level): Map<BlockPos, BlockState> =
        BlockPos.betweenClosed(
            BlockPos.containing(minX, minY, minZ),
            BlockPos.containing(maxX, maxY, maxZ)
        )
        .map { it.immutable() to level.getBlockState(it) }
        .filter { (_, state) -> !state.isAir }
        .toMap()

    public inline fun getRandomSphericalPositions(position: Vec3, radius: Double, numPoints: Int, action: (Vec3) -> Unit) {
        for (i in 0 until numPoints) {
            val theta = 2 * PI * Random.nextDouble()
            val phi = acos(2 * Random.nextDouble() - 1)

            val x = radius * sin(phi) * cos(theta)
            val y = radius * sin(phi) * sin(theta)
            val z = radius * cos(phi)

            action(Vec3(position.x + x, position.y + y, position.z + z))
        }
    }

    public inline fun getSphericalBlockPositions(entity: Entity, radius: Double, chanceToBreak: Double = 0.90, action: (BlockPos) -> Unit) {
        val entityPos = entity.position()
        val radiusCeil = ceil(radius).toInt()
        val radiusSq = radius * radius
        val blockPos = entity.blockPosition()

        for (x in -radiusCeil..radiusCeil) {
            for (y in -radiusCeil..radiusCeil) {
                for (z in -radiusCeil..radiusCeil) {
                    val currentPos = blockPos.offset(x, y, z)
                    if (entityPos.distanceToSqr(Vec3.atCenterOf(currentPos)) <= radiusSq) {
                        if (Random.nextDouble() < chanceToBreak) {
                            action(currentPos)
                        }
                    }
                }
            }
        }
    }

    public fun getSemicircle(
        position: Vec3,
        radius: Double,
        pointsDensity: Double,
        yaw: Float,
        range: Int,
    ): List<Vec3> {
        val numberOfPoints = (pointsDensity * radius).toInt()
        val offsetAngleRad = Math.toRadians((yaw + 90).toDouble())

        val centerX = position.x + radius * cos(offsetAngleRad)
        val centerZ = position.z + radius * sin(offsetAngleRad)

        val startAngle = Math.toRadians((yaw + 90 - range).toDouble())
        val endAngle = Math.toRadians((yaw + 90 + range).toDouble())

        return buildList {
            for (i in 0 until numberOfPoints) {
                val theta = startAngle + Random.nextDouble() * (endAngle - startAngle)
                add(Vec3(centerX + radius * cos(theta), position.y, centerZ + radius * sin(theta)))
            }
        }
    }

    public inline fun getCubePositions(entityPos: Vec3, radius: Double, numPoints: Int, action: (Vec3) -> Unit) {
        val pointsPerFace = numPoints / 6.0
        val gridSize = sqrt(pointsPerFace).toInt()
        val gridSizeDivisor = (gridSize - 1.0).coerceAtLeast(1.0)

        for (face in 0..5) {
            for (i in 0 until gridSize) {
                for (j in 0 until gridSize) {
                    val u = -1.0 + (2.0 * i / gridSizeDivisor)
                    val v = -1.0 + (2.0 * j / gridSizeDivisor)

                    var x = 0.0; var y = 0.0; var z = 0.0

                    when (face) {
                        0 -> { x = radius;  y = u * radius; z = v * radius }
                        1 -> { x = -radius; y = u * radius; z = v * radius }
                        2 -> { y = radius;  x = u * radius; z = v * radius }
                        3 -> { y = -radius; x = u * radius; z = v * radius }
                        4 -> { z = radius;  x = u * radius; y = v * radius }
                        5 -> { z = -radius; x = u * radius; y = v * radius }
                    }

                    action(Vec3(entityPos.x + x, entityPos.y + y, entityPos.z + z))
                }
            }
        }
    }

    public inline fun getCubeCornersAndFaceCenters(blockPos: BlockPos, distance: Double, action: (Vec3) -> Unit) {
        val faceNormals = arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0), doubleArrayOf(-1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0), doubleArrayOf(0.0, -1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0), doubleArrayOf(0.0, 0.0, -1.0)
        )

        val centerX = blockPos.x + 0.5
        val centerY = blockPos.y + 0.5
        val centerZ = blockPos.z + 0.5
        val gridStep = distance / 2

        for (normal in faceNormals) {
            val u: DoubleArray
            val v: DoubleArray

            when {
                normal[0] != 0.0 -> { u = doubleArrayOf(0.0, 1.0, 0.0); v = doubleArrayOf(0.0, 0.0, 1.0) }
                normal[1] != 0.0 -> { u = doubleArrayOf(1.0, 0.0, 0.0); v = doubleArrayOf(0.0, 0.0, 1.0) }
                else             -> { u = doubleArrayOf(1.0, 0.0, 0.0); v = doubleArrayOf(0.0, 1.0, 0.0) }
            }

            for (i in -1..1) {
                for (j in -1..1) {
                    action(Vec3(
                        centerX + normal[0] * distance + i * gridStep * u[0] + j * gridStep * v[0],
                        centerY + normal[1] * distance + i * gridStep * u[1] + j * gridStep * v[1],
                        centerZ + normal[2] * distance + i * gridStep * u[2] + j * gridStep * v[2]
                    ))
                }
            }
        }
    }
}
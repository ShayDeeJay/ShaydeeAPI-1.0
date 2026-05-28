package org.shaydee.shaydeeapi.helpers

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.components.ImageWidget.texture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.AABB
import org.checkerframework.checker.units.qual.g
import java.awt.Color
import java.util.OptionalDouble

public object RenderHelpers {

    @Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    public val LINES_NO_DEPTH: RenderType = RenderType.create(
        "lines_no_depth",
        DefaultVertexFormat.POSITION_COLOR_NORMAL,
        VertexFormat.Mode.LINES,
        256,
        false,
        false,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
            .setLineState(RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .createCompositeState(false)
    )

    @Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    public fun getGlowTranslucent(texture: ResourceLocation): RenderType {
        return RenderType.create(
            "glow_translucent",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.PARTICLES_TARGET)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(true)
        )
    }

    @Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    public fun getGlowTranslucent2(texture: ResourceLocation): RenderType {
        return RenderType.create(
            "glow_translucent",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                .setTextureState(RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.PARTICLES_TARGET)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                // ADD THIS LINE:
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(true)
        )
    }


    @JvmStatic
    public fun colourToRGB(color: Int): FloatArray {
        val r = ((color shr 16) and 0xFF) / 255f
        val g = ((color shr 8) and 0xFF) / 255f
        val b = (color and 0xFF) / 255f

        return listOf(r,g,b).toFloatArray()
    }

    @JvmStatic
    @JvmOverloads
    public fun drawTexture(
        pose: PoseStack.Pose,
        bufferSource: MultiBufferSource,
        light: Int,
        width: Float,
        texture: ResourceLocation,
        colour: Int,
        alpha: Float = 1F,
        renderType: RenderType = getGlowTranslucent2(texture)
    ) {
        val consumer = bufferSource.getBuffer(renderType)
        val halfWidth = width * 0.5f
        val rgb = colourToRGB(colour)

        fun buildVertex(x: Float, z: Float, u: Float, v: Float) {
            consumer.addVertex(pose.pose(), x, 0.05f, z)
                .setColor(rgb[0], rgb[1], rgb[2], alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(0f, 1f, 0f)
        }

        buildVertex(-halfWidth, -halfWidth, 0f, 1f)
        buildVertex(halfWidth,  -halfWidth, 1f, 1f)
        buildVertex(halfWidth,   halfWidth, 1f, 0f)
        buildVertex(-halfWidth,  halfWidth, 0f, 0f)
    }

    public fun renderBoundingBox(
        matrix: PoseStack,
        aabb: AABB,
        color: Int,
        buffer: MultiBufferSource,
        type: RenderType = RenderType.lines()
    ) {
        val x = aabb.minX.toFloat()
        val y = aabb.minY.toFloat()
        val z = aabb.minZ.toFloat()
        val dx = aabb.maxX.toFloat()
        val dy = aabb.maxY.toFloat()
        val dz = aabb.maxZ.toFloat()
        val builder = buffer.getBuffer(type)

        matrix.pushPose()
        val matrix4f = matrix.last().pose()
        val matrix3f = matrix.last()

        fun addVertexes(x: Float, y: Float, z: Float, nX: Float, nY: Float, nZ: Float): VertexConsumer {
            return builder
                .addVertex(matrix4f, x, y, z)
                .setColor(color)
                .setNormal(matrix3f, nX, nY, nZ)
        }

        val nX = 1.0f
        val nY = 0.0f
        addVertexes(x,  y,  z, nX, nY, nY)
        addVertexes(dx, y,  z, nX, nY, nY)
        addVertexes(x,  y,  z, nY, nX, nY)
        addVertexes(x,  dy, z, nY, nX, nY)

        addVertexes(x,  y,  z, nY, nY, nX)
        addVertexes(x,  y,  dz, nY, nY, nX)
        addVertexes(dx, y,  z, nY, nX, nY)
        addVertexes(dx, dy, z, nY, nX, nY)

        addVertexes(dx, dy, z, -nX, nY, nY)
        addVertexes(x,  dy, z, -nX, nY, nY)
        addVertexes(x,  dy, z, nY, nY, nX)
        addVertexes(x,  dy, dz, nY, nY, nX)

        addVertexes(x,  dy, dz, nY, -nX, nY)
        addVertexes(x,  y,  dz, nY, -nX, nY)
        addVertexes(x,  y,  dz, nX, nY, nY)
        addVertexes(dx, y,  dz, nX, nY, nY)

        addVertexes(dx, y,  dz, nY, nY, -nX)
        addVertexes(dx, y,  z, nY, nY, -nX)
        addVertexes(x,  dy, dz, nX, nY, nY)
        addVertexes(dx, dy, dz, nX, nY, nY)

        addVertexes(dx, y,  dz, nY, nX, nY)
        addVertexes(dx, dy, dz, nY, nX, nY)
        addVertexes(dx, dy, z, nY, nY, nX)
        addVertexes(dx, dy, dz, nY, nY, nX)

        matrix.popPose()
    }

}
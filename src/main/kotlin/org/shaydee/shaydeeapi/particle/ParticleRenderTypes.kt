package org.shaydee.shaydeeapi.particle
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.renderer.texture.TextureManager
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
public object ParticleRenderTypes {

    public val ABILITY_RENDERER: ParticleRenderType = object : ParticleRenderType {
        public override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer()
            RenderSystem.enableBlend()
            RenderSystem.depthMask(false)
            RenderSystem.setShader(GameRenderer::getParticleShader)
            RenderSystem.enableCull()
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES)
            RenderSystem.enableDepthTest()
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE)
        }

        public override fun toString(): String = "ABILITY_PART_RENDER"
    }

    public val ABILITY_RENDERER_ALT: ParticleRenderType = object : ParticleRenderType {
        public override fun begin(tesselator: Tesselator, textureManager: TextureManager): BufferBuilder {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer()
            RenderSystem.enableBlend()
            RenderSystem.depthMask(false)
            RenderSystem.setShader(GameRenderer::getParticleShader)
            RenderSystem.enableCull()
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES)
            RenderSystem.enableDepthTest()
            RenderSystem.depthMask(true)
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE)
        }

        public override fun toString(): String = "ABILITY_PART_RENDER"
    }
}

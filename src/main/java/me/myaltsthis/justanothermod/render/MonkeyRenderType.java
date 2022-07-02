package me.myaltsthis.justanothermod.render;

import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class MonkeyRenderType extends RenderLayer {
    private static final RenderPhase.DepthTest noDepth =
            new RenderPhase.DepthTest("always", GL11.GL_ALWAYS);

    static RenderLayer OVERLAY_LINES = refreshOverlayLines();

    public static RenderLayer refreshOverlayLines() {
        return OVERLAY_LINES = of(
                "overlay_lines",
                VertexFormats.LINES,
                VertexFormat.DrawMode.LINES,
                256,
                RenderLayer.MultiPhaseParameters.builder()
                        .shader(Shader.LINES_SHADER)
                        .lineWidth(new LineWidth(OptionalDouble.of(MyGameOptions.scanLineWidth.getValue())))
                        .layering(VIEW_OFFSET_Z_LAYERING)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .texture(NO_TEXTURE)
                        .depthTest(MonkeyRenderType.noDepth)
                        .depthTest(ALWAYS_DEPTH_TEST)
                        .cull(DISABLE_CULLING)
                        .lightmap(DISABLE_LIGHTMAP)
                        .writeMaskState(RenderPhase.ALL_MASK)
                        .build(false));
    }

    public MonkeyRenderType(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.DrawMode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            Runnable startAction,
            Runnable endAction) {
        super(
                name,
                vertexFormat,
                drawMode,
                expectedBufferSize,
                hasCrumbling,
                translucent,
                startAction,
                endAction);
    }
}
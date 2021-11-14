package me.myaltsthis.justanothermod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.myaltsthis.justanothermod.MyGameOptions;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BackgroundRenderer.class)
public class FogMixin {
    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V"))
    private static void increaseFogMax(float f) {
        if (MyGameOptions.fog)
            RenderSystem.setShaderFogEnd(f * 200);
        else
            RenderSystem.setShaderFogEnd(f);
    }

    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"))
    private static void increaseFogStart(float f) {
        if (MyGameOptions.fog)
            RenderSystem.setShaderFogStart(f * 200);
        else
            RenderSystem.setShaderFogStart(f);
    }
}

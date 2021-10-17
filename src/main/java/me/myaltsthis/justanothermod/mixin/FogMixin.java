package me.myaltsthis.justanothermod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.myaltsthis.justanothermod.client.JustAnotherModClient;
import net.minecraft.client.render.BackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BackgroundRenderer.class)
public class FogMixin {
    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V"))
    private static void increaseFogMax(float f) {
        if (JustAnotherModClient.isFogToggled())
            RenderSystem.setShaderFogEnd(f * 200);
        else
            RenderSystem.setShaderFogEnd(f);
    }

    @Redirect(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"))
    private static void increaseFogStart(float f) {
        if (JustAnotherModClient.isFogToggled())
            RenderSystem.setShaderFogStart(f * 200);
        else
            RenderSystem.setShaderFogStart(f);
    }
}

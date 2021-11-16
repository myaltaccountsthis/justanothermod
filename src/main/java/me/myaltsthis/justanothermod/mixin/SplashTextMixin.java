package me.myaltsthis.justanothermod.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {
    @Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;", at = @At("RETURN"), cancellable = true, remap = false)
    private void changeSplashText(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> cir) {
        File file = new File("customsplash.txt");
        if (file.isFile()) {
            List<String> arr = new ArrayList<>();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
                arr = bufferedReader.lines().map(String::trim).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            cir.setReturnValue(arr);
        }
    }
}

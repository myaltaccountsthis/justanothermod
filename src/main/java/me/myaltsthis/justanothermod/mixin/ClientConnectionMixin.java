package me.myaltsthis.justanothermod.mixin;

import me.myaltsthis.justanothermod.hud.LoggerHud;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static void logReceivedPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        LoggerHud.logReceivedPacket(packet, listener.getConnection().getSide());
    }
}

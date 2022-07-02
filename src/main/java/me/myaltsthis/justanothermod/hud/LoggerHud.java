package me.myaltsthis.justanothermod.hud;

import me.myaltsthis.justanothermod.JustAnotherModClient;
import me.myaltsthis.justanothermod.mixin.CustomPayloadS2CPacketAccessor;
import net.fabricmc.fabric.mixin.networking.accessor.CustomPayloadC2SPacketAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;

public class LoggerHud extends ChatHud {
    public LoggerHud(MinecraftClient client) {
        super(client);
        System.out.println(client.inGameHud);
        this.addMessage(Text.literal("Loaded")));
    }

    @Override
    public void render(MatrixStack matrices, int tickDelta) {
        super.render(matrices, tickDelta);
    }

    @Override
    public int getHeight() {
        return 180;
    }


    private static String getSideName(NetworkSide side) {
        if (side == NetworkSide.CLIENTBOUND) return "client";
        if (side == NetworkSide.SERVERBOUND) return "server";

        return side.name();
    }

    private static String getChannel(Packet<?> packet) {
        if (packet instanceof CustomPayloadC2SPacketAccessor) {
            return ((CustomPayloadC2SPacketAccessor) packet).getChannel().toString();
        } else if (packet instanceof CustomPayloadS2CPacketAccessor) {
            return ((CustomPayloadS2CPacketAccessor) packet).getChannel().toString();
        }
        return packet.getClass().getSimpleName();
    }
    public static void logReceivedPacket(Packet<?> packet, NetworkSide side) {
        JustAnotherModClient.loggerHud.addMessage(Text.literal("Received packet with channel '" + getChannel(packet) + "' (" + getSideName(side) + ")")));
    }
}

package net.beholderface.ephemera.fabric

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.beholderface.ephemera.networking.ParticleBurstPacket
import java.util.function.Consumer
import java.util.function.Function

object FabricPacketHandler {
    fun initClientBound(){
        ClientPlayNetworking.registerGlobalReceiver(ParticleBurstPacket.ID, makeClientBoundHandler(ParticleBurstPacket::deserialise, ParticleBurstPacket::handle))
    }

    private fun <T> makeClientBoundHandler(decoder: Function<PacketByteBuf, T>, handler: Consumer<T>): ClientPlayNetworking.PlayChannelHandler {
        return ClientPlayNetworking.PlayChannelHandler { _: MinecraftClient, _: ClientPlayNetworkHandler, buf: PacketByteBuf, _: PacketSender ->
            handler.accept(decoder.apply(buf))
        }
    }
}
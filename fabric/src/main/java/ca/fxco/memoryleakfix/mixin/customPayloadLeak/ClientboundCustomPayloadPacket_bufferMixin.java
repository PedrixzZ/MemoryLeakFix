package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.fabric.MemoryLeakFixFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class ClientboundCustomPayloadPacket_bufferMixin {

    private static final Set<FriendlyByteBuf> BUFFERS_TO_CLEAR = new HashSet<>();

    /*
     * The issue here is that for Custom Payload packets, the netty buffer is never freed.
     * Unlike the bug report states [MC-121884](https://bugs.mojang.com/browse/MC-121884)
     * You cannot simply release the packet once it's been applied, since packets are sometimes used between
     * multiple apply calls, this can lead to multiple issues.
     *
     * Therefore, what we do is store them (they are not going to unload anyway) and then free them later in the tick.
     *
     * By Fx Morin
     */

    /*
     * Forge fixes this memory leak, so we put it fabric-side.
     */

    @Shadow
    @Final
    private FriendlyByteBuf data;

    @Inject(
            method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V",
            at = @At("RETURN")
    )
    private void memoryLeakFix$storeBufferToClear(CallbackInfo ci) {
        synchronized (BUFFERS_TO_CLEAR) {
            if (!BUFFERS_TO_CLEAR.contains(this.data)) {
                BUFFERS_TO_CLEAR.add(this.data);
            }
        }
    }

    // Método para limpar os buffers armazenados
    public static void clearBuffers() {
        synchronized (BUFFERS_TO_CLEAR) {
            BUFFERS_TO_CLEAR.forEach(FriendlyByteBuf::release);
            BUFFERS_TO_CLEAR.clear();
        }
    }
}

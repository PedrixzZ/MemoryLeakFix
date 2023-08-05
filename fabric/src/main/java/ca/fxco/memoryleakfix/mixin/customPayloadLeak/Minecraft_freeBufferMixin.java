package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.fabric.MemoryLeakFixFabric;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class Minecraft_freeBufferMixin {

    /*
     * Free the packets at the end of the tick
     */

    private boolean memoryLeakFix$tryRelease(FriendlyByteBuf buffer) {
        // Check if the buffer is already released
        if (buffer.refCnt() == 0) {
            return false;
        }

        // Check if the buffer is an AbstractReferenceCountedByteBuf
        if (!(buffer instanceof AbstractReferenceCountedByteBuf)) {
            buffer.release();
            return true;
        }

        return true;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void memoryLeakFix$releaseBuffersAfterTick(CallbackInfo ci) {
        // Use Iterator for direct removal of released buffers
        var iterator = MemoryLeakFixFabric.BUFFERS_TO_CLEAR.iterator();
        while (iterator.hasNext()) {
            FriendlyByteBuf buffer = iterator.next();
            if (!memoryLeakFix$tryRelease(buffer)) {
                iterator.remove();
            }
        }
    }
}

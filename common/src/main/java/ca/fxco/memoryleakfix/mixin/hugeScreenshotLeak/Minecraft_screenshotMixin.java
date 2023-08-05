package ca.fxco.memoryleakfix.mixin.hugeScreenshotLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.GlUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

@MinecraftRequirement(@VersionRange(minVersion = "1.20.1"))
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class Minecraft_screenshotMixin {

    @ModifyExpressionValue(
            method = "grabHugeScreenshot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlUtil;allocateMemory(I)Ljava/nio/ByteBuffer;"
            )
    )
    private ByteBuffer memoryLeakFix$captureByteBuffer(ByteBuffer byteBuf,
                                                       @Share("memoryLeakFix$byteBuf") LocalRef<ByteBuffer> bufRef) {
        bufRef.set(byteBuf);
        return byteBuf;
    }

    @Inject(
            method = "grabHugeScreenshot",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=screenshot.failure"
            )
    )
    private void memoryLeakFix$freeByteBuffer(CallbackInfoReturnable<Component> cir,
                                              @Share("memoryLeakFix$byteBuf") LocalRef<ByteBuffer> bufRef) {
        GlUtil.freeMemory(bufRef.get());
    }
}

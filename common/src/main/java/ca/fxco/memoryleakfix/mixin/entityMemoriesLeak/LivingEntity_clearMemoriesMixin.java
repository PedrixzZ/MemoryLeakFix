package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import ca.fxco.memoryleakfix.extensions.ExtendBrain;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MinecraftRequirement(@VersionRange(maxVersion = "1.20.1"))
@Mixin(LivingEntity.class)
public abstract class LivingEntity_clearMemoriesMixin extends Entity_clearMemoriesMixin {

    @Shadow
    protected Brain<?> brain;

    @Override
    protected void memoryLeakFix$OnEntityRemoved(CallbackInfo ci) {
        ((ExtendBrain) this.brain).memoryLeakFix$clearMemories();
    }
}

package silence.simsool.lucent.mixin.mixins.events.world;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import silence.simsool.lucent.events.impl.LucentEvent;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk_BLOCK_UPDATE_EVENT {

	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);

	@Inject(method = "setBlockState", at = @At("HEAD"))
	private void onSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
		BlockState old = this.getBlockState(pos);
		if (old != state) LucentEvent.BLOCK_UPDATE_EVENT.invoker().onBlockUpdate(new LucentEvent.BlockUpdateEventData(pos, old, state));
	}

}
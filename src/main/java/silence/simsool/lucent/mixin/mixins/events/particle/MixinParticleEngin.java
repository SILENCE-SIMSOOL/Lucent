package silence.simsool.lucent.mixin.mixins.events.particle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.LucentEvent.ParticleSpawnEvent;

@Mixin(ParticleEngine.class)
public class MixinParticleEngin {

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void addParticle(Particle particle, CallbackInfo ci) {
		ParticleSpawnEvent event = new ParticleSpawnEvent(particle);
		LucentEvent.PARTICLE_SPAWN_EVENT.invoker().onParticleSpawn(event);
		if (event.isCanceled()) ci.cancel();
	}

}
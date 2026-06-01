package silence.simsool.lucent.mixin.mixins.events.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import silence.simsool.lucent.events.impl.EntityEvent;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setPose(Lnet/minecraft/world/entity/Pose;)V"))
	private void onDeath(DamageSource damageSource, CallbackInfo ci) {
		Level world = this.level();
		if (!world.isClientSide()) return;
		EntityEvent.EntityDeathEvent event = new EntityEvent.EntityDeathEvent(this, (ClientLevel) world);
		EntityEvent.ENTITY_DEATH_EVENT.invoker().onEntityDeath(event);
	}

}
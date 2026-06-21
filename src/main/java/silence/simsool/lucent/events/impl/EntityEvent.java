package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IEntityDeathEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IEntityEquipmentEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IEntityInteractEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IEntityJoinEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IEntityLeaveEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IExtractRenderStatePostEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IExtractRenderStatePreEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IRenderEntityAllowEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IRenderEntityColorEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IRenderEntityPreEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IEntityDataEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.INameChangeEvent;
import silence.simsool.lucent.general.utils.Pair;

public final class EntityEvent {

	public static final Map<EntityRenderState, Entity> RENDER_STATE_ENTITIES = new WeakHashMap<>();

	public static final Event<IEntityJoinEvent> ENTITY_JOIN_EVENT = createArrayBacked(
		IEntityJoinEvent.class, listeners -> event -> {
			for (IEntityJoinEvent listener : listeners) {
				listener.onEntityJoin(event);
			}
		}
	);

	public static final Event<IEntityLeaveEvent> ENTITY_LEAVE_EVENT = createArrayBacked(
		IEntityLeaveEvent.class, listeners -> event -> {
			for (IEntityLeaveEvent listener : listeners) {
				listener.onEntityLeave(event);
			}
		}
	);

	public static final Event<IEntityDeathEvent> ENTITY_DEATH_EVENT = createArrayBacked(
		IEntityDeathEvent.class, listeners -> event -> {
			for (IEntityDeathEvent listener : listeners) {
				listener.onEntityDeath(event);
			}
		}
	);

	public static final Event<IRenderEntityAllowEvent> RENDER_ENTITY_ALLOW_EVENT = createArrayBacked(
		IRenderEntityAllowEvent.class, listeners -> event -> {
			for (IRenderEntityAllowEvent listener : listeners) {
				listener.onRenderEntityAllow(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IRenderEntityPreEvent> RENDER_ENTITY_PRE_EVENT = createArrayBacked(
		IRenderEntityPreEvent.class, listeners -> event -> {
			for (IRenderEntityPreEvent listener : listeners) {
				listener.onRenderEntity(event);
			}
		}
	);

	public static final Event<IRenderEntityColorEvent> RENDER_ENTITY_COLOR_EVENT = createArrayBacked(
		IRenderEntityColorEvent.class, listeners -> event -> {
			for (IRenderEntityColorEvent listener : listeners) {
				listener.onRenderEntityColor(event);
			}
		}
	);

	public static final Event<IExtractRenderStatePreEvent> EXTRACT_RENDER_STATE_PRE = createArrayBacked(
		IExtractRenderStatePreEvent.class, listeners -> event -> {
			for (IExtractRenderStatePreEvent listener : listeners) {
				listener.onExtractRenderStatePre(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IExtractRenderStatePostEvent> EXTRACT_RENDER_STATE_POST = createArrayBacked(
		IExtractRenderStatePostEvent.class, listeners -> event -> {
			for (IExtractRenderStatePostEvent listener : listeners) {
				listener.onExtractRenderStatePost(event);
			}
		}
	);

	public static final Event<INameChangeEvent> NAME_CHANGE_EVENT = createArrayBacked(
		INameChangeEvent.class, listeners -> event -> {
			for (INameChangeEvent listener : listeners) {
				listener.onNameChange(event);
			}
		}
	);

	public static final Event<IEntityDataEvent> ENTITY_DATA_EVENT = createArrayBacked(
		IEntityDataEvent.class, listeners -> event -> {
			for (IEntityDataEvent listener : listeners) {
				listener.onEntityData(event);
			}
		}
	);

	public static final Event<IEntityEquipmentEvent> ENTITY_EQUIPMENT_EVENT = createArrayBacked(
		IEntityEquipmentEvent.class, listeners -> event -> {
			for (IEntityEquipmentEvent listener : listeners) {
				listener.onEntityEquipment(event);
			}
		}
	);

	public static final Event<IEntityInteractEvent> ENTITY_INTERACT_EVENT = createArrayBacked(
		IEntityInteractEvent.class, listeners -> event -> {
			for (IEntityInteractEvent listener : listeners) {
				listener.onEntityInteract(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static class EntityJoinEvent {
		public final Entity entity;

		public EntityJoinEvent(Entity entity) {
			this.entity = entity;
		}
	}

	public static class EntityLeaveEvent {
		public final Entity entity;

		public EntityLeaveEvent(Entity entity) {
			this.entity = entity;
		}
	}

	public static class EntityDeathEvent {
		public final Entity entity;
		public final ClientLevel level;

		public EntityDeathEvent(Entity entity, ClientLevel level) {
			this.entity = entity;
			this.level = level;
		}
	}

	public static class RenderEntityPreEvent {
		public final EntityRenderState entityState;
		public final CameraRenderState cameraState;
		public final PoseStack matrix;
		public final SubmitNodeCollector submitter;

		public RenderEntityPreEvent(EntityRenderState entityState, CameraRenderState cameraState, PoseStack matrix, SubmitNodeCollector submitter) {
			this.entityState = entityState;
			this.cameraState = cameraState;
			this.matrix = matrix;
			this.submitter = submitter;
		}
	}

	public static class RenderEntityAllowEvent {
		public final Entity entity;
		public final Frustum frustum;
		public final double x;
		public final double y;
		public final double z;
		private boolean canceled = false;

		public RenderEntityAllowEvent(Entity entity, Frustum frustum, double x, double y, double z) {
			this.entity = entity;
			this.frustum = frustum;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class ExtractRenderStatePre {
		public final Entity entity;
		public final float partialTick;
		private boolean canceled = false;

		public ExtractRenderStatePre(Entity entity, float partialTick) {
			this.entity = entity;
			this.partialTick = partialTick;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class ExtractRenderStatePost {
		public final Entity entity;
		public final EntityRenderState state;
		public final float partialTick;

		public ExtractRenderStatePost(Entity entity, EntityRenderState state, float partialTick) {
			this.entity = entity;
			this.state = state;
			this.partialTick = partialTick;
		}
	}

	public static class NameChangeEvent {
		public final int entityId;
		public final EntityType<?> type;
		public final Component nameText;
		public final String name;

		public NameChangeEvent(int entityId, EntityType<?> type, Component nameText, String name) {
			this.entityId = entityId;
			this.type = type;
			this.nameText = nameText;
			this.name = name;
		}
	}

	public static class EntityDataEvent {
		private final int entityId;
		private final EntityType<?> type;
		private final List<SynchedEntityData.DataValue<?>> data;

		public EntityDataEvent(int entityId, EntityType<?> type, List<SynchedEntityData.DataValue<?>> data) {
			this.entityId = entityId;
			this.type = type;
			this.data = data;
		}

		public int getEntityId() {
			return entityId;
		}

		public EntityType<?> getType() {
			return type;
		}

		public List<SynchedEntityData.DataValue<?>> getData() {
			return data;
		}
	}

	public static class EntityEquipmentEvent {
		public final int entityId;
		public final EntityType<?> type;
		public final Vec3 spawnPos;
		public final List<Pair<EquipmentSlot, ItemStack>> slots;

		public EntityEquipmentEvent(int entityId, EntityType<?> type, Vec3 spawnPos, List<Pair<EquipmentSlot, ItemStack>> slots) {
			this.entityId = entityId;
			this.type = type;
			this.spawnPos = spawnPos;
			this.slots = slots;
		}
	}

	public static class EntityInteractEvent {
		public final Entity entity;
		private boolean canceled = false;

		public EntityInteractEvent(Entity entity) {
			this.entity = entity;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class RenderEntityColorEvent {
		public final Entity entity;
		public final EntityRenderState state;
		private int color = 0;

		public RenderEntityColorEvent(Entity entity, EntityRenderState state) {
			this.entity = entity;
			this.state = state;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getColor() {
			return color;
		}

		public boolean hasColor() {
			return color != 0;
		}
	}

}
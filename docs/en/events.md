# Event System

Lucent provides a powerful event system built on top of Fabric's callback system. You can subscribe to these events through override methods in your mod class.

## How to Use

To use these events, inherit from the `Mod` class and use `@Override` on the event methods. 
A complete list of overrideable event methods can be found in [Mod.java](../../src/main/java/silence/simsool/lucent/general/models/abstracts/Mod.java).

### Module Activation Rules
- By default, event methods are only triggered when your module is **enabled**.
- However, some general lifecycle events trigger **regardless of the module's activation state** (often used for data resets or system setup).
- If you want the event to trigger **only when the module is enabled**, use the version of the method ending with `Mod`.
  - Example: `onWorldLoad()` (Triggers regardless of activation) vs `onWorldLoadMod()` (Triggers only when module is enabled).

---

## Events List

### ⚙️ LucentEvent
- `TickEvent` (LOW, MEDIUM, HIGH): Dispatched on client ticks.
- `EverySecondEvent`: Dispatched once every second.
- `ServerTickEvent`: Dispatched on server ticks.
- `ChatEvent`: Dispatched when a chat message is received.
- `ActionBarEvent`: Dispatched when an action bar message is received.
- `ServerJoinEvent` (Also has `ServerJoinMod`): Dispatched when joining a server.
- `ServerDisconnectEvent` (Also has `ServerDisconnectMod`): Dispatched when disconnecting from a server.
- `WorldLoadEvent` (Also has `WorldLoadMod`): Dispatched when a world finishes loading.
- `BlockUpdateEvent`: Dispatched on block updates.
- `RenderWorldEvent` & `RenderWorldLastEvent`: Dispatched during world rendering phases.
- `BlockInteractEvent`: Dispatched when interacting with a block.
- `MessageSentEvent`: Dispatched when the player sends a chat message.
- `ModMessageEvent`: Dispatched for custom mod messages.
- `TabCompletionEvent`: Dispatched when requesting tab completion suggestions.
- `RenderBossBarEvent`: Dispatched when rendering boss health bars.
- `KeybindEvent`: Registered keybinds are triggered.
- `ParticleSpawnEvent`: Dispatched when a particle is spawned.
- `DropItemEvent`: Dispatched when dropping an item.
- `ItemPickupEvent`: Dispatched when picking up an item entity.
- `SoundEvent`: Dispatched when sound events are played.
- `ScoreboardEvent`: Dispatched on scoreboard team updates.
- `UseItemOnEvent` & `UseItemEvent`: Dispatched when using items.

### 👥 EntityEvent
- `EntityJoinEvent` & `EntityLeaveEvent`: Dispatched when entities load/unload.
- `EntityDeathEvent`: Dispatched when an entity dies.
- `EntityDataEvent`: Dispatched when entity synched data updates.
- `EntityEquipmentEvent`: Dispatched when entity equipment updates.
- `EntityInteractEvent`: Dispatched when interacting with an entity.
- `RenderEntityPreEvent` & `RenderEntityAllowEvent`: Dispatched during entity rendering.
- `ExtractRenderStatePre` & `ExtractRenderStatePost`: Dispatched when extracting render state.
- `NameChangeEvent`: Dispatched when an entity's name changes.

### 📺 GUIEvent
- `RenderHUD`: Dispatched when rendering the game HUD.
- `GUIOpenEvent` (Also has `GUICloseEvent`): Dispatched when GUIs open or close.
- `GUIClickEvent` & `GUIKeyEvent`: Dispatched on mouse clicks or key presses inside GUIs.
- `SlotClickEvent`: Dispatched on GUI slot clicks.
- `SlotUpdateEvent`: Dispatched on GUI slot updates.
- `RenderSlotPreEvent` & `RenderSlotPostEvent`: Dispatched before/after slot rendering.
- `RenderHotbarPreEvent` & `RenderHotbarPostEvent`: Dispatched before/after hotbar slot rendering.
- `RenderContainer` / `RenderInventory` / `RenderChest`: Dispatched during container screen rendering.
- `TooltipEvent`: Dispatched when rendering item tooltips.

### ⌨️ InputEvent
- `MouseInputEvent` & `KeyInputEvent`: Raw mouse and keyboard input events.

### 📦 PacketEvent
- `ReceiveEvent` & `SendEvent`: Dispatched when network packets are received or sent.

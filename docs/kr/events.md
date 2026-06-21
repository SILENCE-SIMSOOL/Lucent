# 이벤트 시스템

Lucent는 Fabric의 콜백 시스템 위에 설계된 강력한 이벤트 시스템을 제공합니다. 모듈 클래스 내에서 오버라이드 메서드를 선언하여 이 이벤트들을 구독할 수 있습니다.

## 사용 방법

이벤트를 사용하려면 `Mod` 클래스를 상속받고, 원하는 이벤트 메서드를 `@Override`하여 구현합니다.
오버라이드 가능한 전체 이벤트 함수 목록은 [Mod.java](../../src/main/java/silence/simsool/lucent/general/models/abstracts/Mod.java)에서 확인할 수 있습니다.

### 모듈 활성화 여부 규칙
- 기본적으로 이벤트 함수들은 모듈이 **활성화(Enabled) 상태**일 때만 발동합니다.
- 단, 일부 라이프사이클 이벤트들은 데이터 리셋이나 공통 처리를 위해 **모듈 활성화 여부와 관계없이 항상 작동**합니다.
- 이러한 항상 작동하는 기본 함수 대신, 모듈이 활성화되었을 때만 실행되는 함수를 사용하고 싶다면 이름 뒤에 `Mod`가 붙은 함수를 사용하세요.
  - 예시: `onWorldLoad()` (모듈 활성 여부와 상관없이 무조건 실행) vs `onWorldLoadMod()` (모듈이 활성화 상태일 때만 실행)

---

## 이벤트 목록

### ⚙️ LucentEvent
- `TickEvent` (LOW, MEDIUM, HIGH): 클라이언트 틱 시 발생합니다.
- `EverySecondEvent`: 1초마다 발생합니다.
- `ServerTickEvent`: 서버 틱 시 발생합니다.
- `ChatEvent`: 채팅 메시지 수신 시 발생합니다.
- `ActionBarEvent`: 액션바 메시지 수신 시 발생합니다.
- `ServerJoinEvent` (`ServerJoinMod` 제공): 서버 접속 시 발생합니다.
- `ServerDisconnectEvent` (`ServerDisconnectMod` 제공): 서버 연결 종료 시 발생합니다.
- `WorldLoadEvent` (`WorldLoadMod` 제공): 월드 로드가 완료될 때 발생합니다.
- `BlockUpdateEvent`: 블록 업데이트 시 발생합니다.
- `RenderWorldEvent` & `RenderWorldLastEvent`: 월드 렌더링 단계에서 발생합니다.
- `BlockInteractEvent`: 블록과 상호작용 시 발생합니다.
- `MessageSentEvent`: 플레이어가 메시지를 전송할 때 발생합니다.
- `ModMessageEvent`: 커스텀 모드 메시지 이벤트입니다.
- `TabCompletionEvent`: 탭 완성을 요청할 때 발생합니다.
- `RenderBossBarEvent`: 보스 바를 렌더링할 때 발생합니다.
- `KeybindEvent`: 등록한 키바인드가 동작할 때 발생합니다.
- `ParticleSpawnEvent`: 파티클이 스폰될 때 발생합니다.
- `DropItemEvent`: 아이템을 떨어뜨릴 때 발생합니다.
- `ItemPickupEvent`: 아이템 엔티티를 획득할 때 발생합니다.
- `SoundEvent`: 사운드가 재생될 때 발생합니다.
- `ScoreboardEvent`: 스코어보드 팀 업데이트 시 발생합니다.
- `UseItemOnEvent` & `UseItemEvent`: 아이템 사용 시 발생합니다.

### 👥 EntityEvent
- `EntityJoinEvent` & `EntityLeaveEvent`: 엔티티가 로드되거나 언로드될 때 발생합니다.
- `EntityDeathEvent`: 엔티티가 사망할 때 발생합니다.
- `EntityDataEvent`: 엔티티 동기화 데이터가 업데이트될 때 발생합니다.
- `EntityEquipmentEvent`: 엔티티의 장비가 변경될 때 발생합니다.
- `EntityInteractEvent`: 엔티티와 상호작용 시 발생합니다.
- `RenderEntityPreEvent` & `RenderEntityAllowEvent`: 엔티티 렌더링 시 발생합니다.
- `ExtractRenderStatePre` & `ExtractRenderStatePost`: 렌더 상태 추출 시 발생합니다.
- `NameChangeEvent`: 엔티티 이름 변경 시 발생합니다.

### 📺 GUIEvent
- `RenderHUD`: 게임 HUD 렌더링 시 발생합니다.
- `GUIOpenEvent` (`GUICloseEvent` 제공): GUI 화면이 열리거나 닫힐 때 발생합니다.
- `GUIClickEvent` & `GUIKeyEvent`: GUI 내부 마우스 클릭 및 키 입력 시 발생합니다.
- `SlotClickEvent`: GUI 슬롯 클릭 시 발생합니다.
- `SlotUpdateEvent`: GUI 슬롯 업데이트 시 발생합니다.
- `RenderSlotPreEvent` & `RenderSlotPostEvent`: 슬롯 렌더링 전/후에 발생합니다.
- `RenderHotbarPreEvent` & `RenderHotbarPostEvent`: 핫바 슬롯 렌더링 전/후에 발생합니다.
- `RenderContainer` / `RenderInventory` / `RenderChest`: 컨테이너 화면 렌더링 시 발생합니다.
- `TooltipEvent`: 아이템 툴팁 렌더링 시 발생합니다.

### ⌨️ InputEvent
- `MouseInputEvent` & `KeyInputEvent`: 로우 마우스 및 키보드 입력 이벤트입니다.

### 📦 PacketEvent
- `ReceiveEvent` & `SendEvent`: 네트워크 패킷 수신 및 송신 시 발생합니다.

# Lucent

**Lucent**는 Minecraft 1.21.11 Fabric 환경을 위한 라이브러리 모드로, 모드 개발자에게 현대적인 Config 관리 시스템과 UI 툴킷을 제공합니다.

> 🌐 [English Documentation](./README.md)

---

## 개요

Lucent가 제공하는 것들:

- **완성도 높은 Config UI** — 모드를 등록하면 어노테이션만으로 Lucent의 내장 화면에 옵션이 자동으로 나타납니다. 화면 코드를 직접 짤 필요가 없습니다.
- **독립적인 Config 파일** — Lucent 화면에 통합하지 않고 별도의 Config 파일만 사용할 수도 있습니다.
- **완전한 UI 툴킷** — NanoVG 렌더링 유틸과 위젯 컴포넌트를 이용해 직접 화면을 만들 수 있습니다.
- **HUD 시스템** — 드래그/스케일이 가능한 HUD 요소를 `EditHudScreen`에 등록할 수 있습니다.
- **유틸리티 클래스** — 채팅, 디스플레이 정보, 마우스, 애니메이션, 색상 연산, 로깅 등의 편의 기능을 제공합니다.

---

## 요구사항

| 의존성 | 버전 |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | ≥ 0.139.5+1.21.11 |
| LWJGL NanoVG | 3.3.3 (번들 포함) |

---

## 통합

### 1. 의존성 추가

Lucent는 두 가지 JAR 빌드를 제공합니다:

| Classifier | 내용 |
|---|---|
| *(기본)* | 완전한 JIJ 빌드 — NanoVG 네이티브 포함 |
| `library` | 경량 빌드 — NanoVG 제외 (Lucent를 내부에 번들할 때 사용) |

`build.gradle`에 추가:

```groovy
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
	// 모드 jar (lucent-1.0.0.jar)를 가져올 때
	implementation 'com.github.SILENCE-SIMSOOL:Lucent:1.0.0'

	// 라이브러리 jar (lucent-1.0.0-library.jar)을 가져올 때
	implementation 'com.github.SILENCE-SIMSOOL:Lucent:1.0.0:library'
}
```

모드 jar는 플레이어가 모드를 직접 적용 시켜야하는 번거로움이 있지만 파일의 크기를 효율적으로 줄일 수 있습니다.
라이브러리 jar는 자신의 모드에 내장시켜 Lucent 모드를 따로 사용하지 않아도 됩니다.

---

## 사용법

### 방법 A — Lucent Config 화면 사용 (권장)

가장 빠른 통합 방법입니다. Lucent가 어노테이션을 분석해 Config UI를 자동으로 생성합니다.

#### Step 1 — `ModManager` 생성

```java
// 초기화 클래스에서
public static ModManager config = LucentAPI.createModManager("yourmodid");
```

`config/yourmodid/` 디렉토리에 Config 파일이 저장됩니다.

#### Step 2 — 모듈 클래스 작성

`silence.simsool.lucent.general.models.Mod`를 상속받고, Config 필드에 `@ModConfig`를 붙입니다.

```java
import silence.simsool.lucent.general.models.Mod;
import silence.simsool.lucent.general.interfaces.ModConfig;
import silence.simsool.lucent.general.enums.ConfigType;

public class MyMod extends Mod {

    public MyMod() {
        super(
            "My Mod",           // 표시 이름
            "멋진 기능을 제공합니다", // 설명
            "Utility",          // 카테고리 (사이드바 그룹)
            "utility, tool",    // 검색 태그
            null                // 아이콘 경로 (선택사항, 예: "/assets/mymod/icon.png")
        );
    }

    @ModConfig(
        type = ConfigType.SWITCH,
        name = "기능 활성화",
        description = "메인 기능을 켜거나 끕니다.",
        category = "일반",
        priority = 100
    )
    public static boolean enableFeature = true;

    @ModConfig(
        type = ConfigType.SLIDER,
        name = "속도",
        description = "이동 속도를 조절합니다.",
        category = "일반",
        min = 0.5, max = 5.0, step = 0.5,
        priority = 90
    )
    public static double speed = 1.0;

    @ModConfig(
        type = ConfigType.SELECTOR,
        name = "모드",
        options = {"빠름", "보통", "느림"},
        category = "일반",
        priority = 80
    )
    public static String mode = "보통";

    @ModConfig(
        type = ConfigType.COLOR,
        name = "하이라이트 색상",
        category = "외관",
        priority = 50
    )
    public static Color highlightColor = new Color(85, 255, 85, 200);

    @ModConfig(
        type = ConfigType.KEYBIND,
        name = "활성화 키",
        priority = 40
    )
    public KeyBind activationKey = KeyBind.ofKey(GLFW.GLFW_KEY_H, 0);

    @ModConfig(
        type = ConfigType.TEXT,
        name = "라벨",
        description = "커스텀 라벨 텍스트.",
        category = "외관",
        priority = 30
    )
    public static String label = "Hello";

    @ModConfig(
        type = ConfigType.BUTTON,
        name = "동작 실행",
        display = "클릭!",
        description = "클릭하면 동작을 실행합니다.",
        priority = 20
    )
    public void onRunAction() {
        UChat.chat("버튼이 눌렸습니다!");
    }
}
```

#### Step 3 — 등록 및 로드

```java
@Override
public void onInitializeClient() {
    config.register(new MyMod());
    config.loadGlobalConfig();
    config.loadConfigs();
}
```

#### Step 4 — Config 화면 열기

**ModMenu** 또는 키바인드를 통해:

```java
// ModMenu 연동
Screen screen = LucentAPI.createConfigScreen(config);
client.setScreen(screen);
```

---

### 방법 B — 독립적인 Config 파일 사용 (Lucent 화면 없이)

Lucent UI 없이 별도의 Config 파일만 사용하고 싶다면, 다른 디렉토리명으로 `ModManager`를 만들고 `LucentAPI.createConfigScreen(...)`을 호출하지 않으면 됩니다. 저장/로드는 직접 호출합니다:

```java
ModManager myConfig = LucentAPI.createModManager("mymod");
myConfig.register(new MyMod());
myConfig.loadConfigs();

// 필요할 때 저장:
myConfig.saveConfigs();
```

Config 파일은 `config/mymod/profiles/default/MyMod.json`으로 JSON 형식으로 저장됩니다.

---

### 방법 C — Lucent 유틸을 이용해 직접 화면 제작

Lucent Config 화면을 사용하지 않고 NanoVG 렌더러와 위젯 클래스를 이용해 완전히 직접 화면을 만들 수 있습니다.

#### NanoVG 렌더링 (`NVGRenderer`)

모든 NVG 드로우 콜은 `NVGPIPRenderer.draw(...)` 안에서 실행해야 합니다 (프레임버퍼 설정을 처리함):

```java
NVGPIPRenderer.draw(guiGraphics, x, y, width, height, () -> {
    // 사각형
    NVGRenderer.rect(x, y, w, h, color);
    NVGRenderer.rect(x, y, w, h, color, cornerRadius);
    NVGRenderer.rect(x, y, w, h, color, r1, r2, r3, r4); // 모서리별 반경

    // 외곽선 사각형
    NVGRenderer.outlineRect(x, y, w, h, thickness, color, radius);

    // 그라디언트 사각형
    NVGRenderer.gradientRect(x, y, w, h, color1, color2, GradientType.TOP_TO_BOTTOM, radius);

    // 원
    NVGRenderer.circle(cx, cy, radius, color);
    NVGRenderer.outlineCircle(cx, cy, radius, thickness, color);

    // 선 & 삼각형
    NVGRenderer.line(x1, y1, x2, y2, thickness, color);
    NVGRenderer.triangle(x1, y1, x2, y2, x3, y3, color);
    NVGRenderer.arrowTriangle(cx, cy, w, h, Direction.DOWN, color);

    // 드롭 섀도우
    NVGRenderer.dropShadow(x, y, w, h, blur, spread, radius);

    // 이미지
    NVGRenderer.image(image, x, y, w, h, radius);

    // SVG
    NVGRenderer.svg(svgImage, x, y, w, h);

    // 텍스트
    NVGRenderer.text("Hello", x, y, Fonts.PRETENDARD, color, fontSize);
    NVGRenderer.textShadow("Hello", x, y, Fonts.PRETENDARD, color, fontSize);
    NVGRenderer.centerText("Hello", cx, y, Fonts.PRETENDARD, color, fontSize);
    float w = NVGRenderer.textWidth("Hello", Fonts.PRETENDARD, 14f);

    // 상태 관리
    NVGRenderer.push();           // 상태 저장
    NVGRenderer.scale(sx, sy);
    NVGRenderer.translate(dx, dy);
    NVGRenderer.rotate(radians);
    NVGRenderer.globalAlpha(0.5f);
    NVGRenderer.pop();            // 상태 복원

    // 시저 (클리핑)
    NVGRenderer.pushScissor(x, y, w, h);
    // ... 클리핑된 콘텐츠 렌더링 ...
    NVGRenderer.popScissor();

    // 체커보드 (투명도 미리보기 등에 사용)
    NVGRenderer.drawCheckerboard(x, y, w, h, radius);
});
```

#### 사용 가능한 폰트 (`Fonts`)

```java
Fonts.PRETENDARD_EXTRALIGHT
Fonts.PRETENDARD_LIGHT
Fonts.PRETENDARD          // 기본값
Fonts.PRETENDARD_MEDIUM
Fonts.PRETENDARD_SEMIBOLD
```

#### 제공되는 위젯

`silence.simsool.lucent.ui.widget` 패키지에 위치:

| 위젯 | 설명 |
|---|---|
| `ToggleButton` | 애니메이션이 있는 ON/OFF 스위치 |
| `Slider` | 드래그 가능한 범위 입력 |
| `Selector` | 옵션을 순환하는 선택기 |
| `ColorPicker` | HSVA 색상 피커 (HEX 입력 포함) |
| `ColorPickerButton` | 클릭하면 ColorPicker를 여는 색상 미리보기 버튼 |
| `KeyBindButton` | 키보드/마우스 키바인드 입력 캡처 |
| `TextBox` | 한 줄 텍스트 입력 |
| `ActionButton` | 클릭 시 콜백을 실행하는 버튼 |

각 위젯은 입력 이벤트, 애니메이션, 렌더링을 자체적으로 처리합니다.

---

### HUD 시스템

Lucent의 `EditHudScreen`에서 드래그/스케일로 위치를 조정할 수 있는 HUD 요소를 등록합니다.

#### Step 1 — `LucentHUD` 상속

```java
import silence.simsool.lucent.general.models.LucentHUD;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;

public class MyHUD extends LucentHUD {

    public MyHUD() {
        super("my_hud", 0.01f, 0.05f, 1.0f, HUDAlignment.LEFT);
        // id, 기본 X (0~1 비율), 기본 Y (0~1 비율), 스케일, 정렬
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.NANOVG; // 또는 RenderType.MINECRAFT
    }

    @Override
    public float getPreviewWidth()  { return 160f; }

    @Override
    public float getPreviewHeight() { return 24f; }

    @Override
    public boolean isEnabled() {
        return myConfig.getModule(MyMod.class).isEnabled;
    }

    @Override
    public void draw() {
        // EditHud 화면이 열려있거나 F3 디버그 화면일 때 건너뜀
        if (LucentHUD.isEditHudOpen || UDisplay.isDebugScreen()) return;
        renderContent("실시간 데이터");
    }

    @Override
    public void preview() {
        // EditHud 화면에서 보여줄 미리보기
        renderContent("미리보기");
    }

    private void renderContent(String text) {
        float rx = getRenderX(), ry = getRenderY();
        float sw = getScaledWidth(), sh = getScaledHeight();
        NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 130), 4f * scale);
        NVGRenderer.text(text, rx + 8 * scale, ry + (sh - 14f * scale) / 2f, Fonts.PRETENDARD, UIColors.PURE_WHITE, 14f * scale);
    }
}
```

#### Step 2 — 등록

```java
HUDManager.INSTANCE.register(new MyHUD());
HUDManager.INSTANCE.loadAll();
```

HUD의 위치, 스케일, 정렬은 프로필별로 자동으로 저장됩니다.

---

## 프로필

Lucent는 이름이 있는 Config 프로필을 지원합니다. 모든 모듈 Config와 HUD 위치가 `config/<dir>/profiles/<name>/` 아래에 프로필별로 저장됩니다.

```java
ModManager config = LucentAPI.createModManager("mymod");

// 프로필 목록 가져오기 (항상 "default" 포함)
List<String> profiles = config.getProfiles();

// 프로필 전환 (모든 Config 리로드)
config.setCurrentProfile("pvp");

// 생성 / 삭제 / 이름 변경
config.createProfile("pvp");
config.deleteProfile("pvp");
config.renameProfile("pvp", "competitive");
```

---

## 테마

Config 화면은 여러 가지 시각적 테마를 지원합니다. 사용자가 UI에서 변경할 수도 있고, 코드에서 직접 적용할 수도 있습니다:

```java
ThemeManager.applyTheme(ThemeManager.findTheme("Midnight Cyan"));
```

기본 제공 테마: `Default`, `Glass Morphic`, `Midnight Cyan`, `Crimson Aurora`, `Emerald Mist`, `Amethyst Eclipse`

---

## 유틸리티 클래스

### `UChat`
```java
UChat.chat("안녕하세요!");          // 클라이언트 측 메시지 표시
UChat.chat(42);                   // int, long, double, float, boolean, Object 오버로드
UChat.say("서버로 채팅");           // 서버로 채팅 메시지 전송
```

### `UDisplay`
```java
UDisplay.getWidth()            // 프레임버퍼 너비 (픽셀)
UDisplay.getScreenWidth()      // 물리 화면 너비
UDisplay.getGuiScaledWidth()   // GUI 스케일 적용 너비
UDisplay.isFullscreen()
UDisplay.isDebugScreen()       // F3 디버그 화면 여부
```

### `UMouse`
```java
UMouse.getX()                           // 마우스 X (원시 좌표)
UMouse.getY()                           // 마우스 Y (원시 좌표)
UMouse.getScaledX(scale)               // GUI 스케일 * scale로 나눈 X
UMouse.isAreaHovered(x, y, w, h)       // 호버 체크 (원시 좌표)
UMouse.isAreaHovered(x, y, w, h, true) // 호버 체크 (GUI 스케일 적용)
UMouse.getQuadrant()                    // 화면 사분면 (1~4)
```

### `UAnimation`
```java
// 이징 함수 (입력: 0~1, 출력: 0~1)
UAnimation.Easing.easeOut(t)
UAnimation.Easing.easeInOut(t)
UAnimation.Easing.spring(t)       // 약간의 오버슈트
UAnimation.Easing.elastic(t)
UAnimation.Easing.bounce(t)

// 보간 (Lerp)
UAnimation.lerp(a, b, t)
UAnimation.lerpColor(c1, c2, t)
UAnimation.lerpSnap(current, target, speed, delta) // 목표에 가까우면 스냅

// 클램프
UAnimation.clamp(val, min, max)

// 시간 기반
UAnimation.getPulseAlpha(speed)      // 0~1 사인파
UAnimation.getWaveOffset(speed, amplitude)
UAnimation.getCycle(periodSeconds)   // 0~1 주기

// 프레임 기반 애니메이션 진행도
UAnimation.stepProgress(current, forward, speed, delta)

// 슬라이더 관련
UAnimation.snapToStep(value, step)
UAnimation.formatForStep(value, step)
```

### `UColor`
```java
UColor.argb(a, r, g, b)
UColor.rgb(r, g, b)
UColor.withAlpha(color, alpha)        // alpha: 0–255
UColor.withAlphaF(color, alphaFloat)  // alpha: 0.0–1.0
UColor.lerpColor(c1, c2, t)
UColor.darken(color, 0.2f)
UColor.lighten(color, 0.2f)
UColor.toHSV(color)                   // float[] {h, s, v}
UColor.fromHSVA(h, s, v, a)
UColor.toHex(color)                   // "#RRGGBBAA"
UColor.fromHex("#FF5500")
```

### `ULog`
```java
ULog log = new ULog("MyMod");
log.info("초기화됨");
log.warn("뭔가 이상함");
log.error("실패!", exception);
```

### `URender`
```java
// 토글 버튼 시각 렌더링 (NVGRenderer 내부 사용)
URender.drawToggleButton(x, y, w, h, onProgress, hoverProgress);
```

### `KeyBind`
```java
KeyBind kb = KeyBind.ofKey(GLFW.GLFW_KEY_H, 0);           // 키보드 키
KeyBind kb = KeyBind.ofMouse(KeyBind.MOUSE_RIGHT, 0);      // 마우스 버튼
KeyBind kb = KeyBind.none();                               // 바인딩 없음

kb.getDisplayName(); // 예: "Ctrl+Shift+H", "Mouse2", "None"
kb.isBound()
kb.isKey()
kb.isMouse()
```

---

## `@ModConfig` 레퍼런스

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `type` | `ConfigType` | 위젯 타입: `SWITCH`, `SLIDER`, `BUTTON`, `COLOR`, `SELECTOR`, `KEYBIND`, `TEXT` |
| `name` | `String` | Config UI에 표시되는 이름 |
| `display` | `String` | 버튼 라벨 텍스트 (`BUTTON` 타입에만 사용) |
| `description` | `String` | 툴팁/설명 텍스트 |
| `category` | `String` | 탭 그룹 이름 (기본값: `"General"`) |
| `min` / `max` | `double` | 슬라이더 범위 (기본: 0 / 10) |
| `step` | `double` | 슬라이더 단계 크기 (기본: 1.0) |
| `options` | `String[]` | Selector 옵션 목록 |
| `priority` | `int` | 카테고리 내 표시 순서 (높을수록 위) |
| `parent` | `String` | 부모 `SWITCH` 필드명 — 부모가 꺼지면 이 항목은 숨겨짐 |

### 카테고리 우선순위

클래스 레벨 어노테이션으로 탭 정렬 순서를 제어합니다:

```java
@ModConfig.CategoryPriority(name = "일반",   priority = 1000)
@ModConfig.CategoryPriority(name = "외관",   priority = 500)
public class MyMod extends Mod { ... }
```

### 자식 Config 항목

토글이 꺼졌을 때 의존 옵션을 숨기려면 `parent`를 사용합니다:

```java
@ModConfig(type = ConfigType.SWITCH, name = "X 활성화", priority = 100)
public static boolean enableX = true;

@ModConfig(type = ConfigType.SLIDER, name = "X 속도", parent = "enableX", priority = 90)
public static double xSpeed = 1.0;
```

---

## 라이선스

MIT License — 자세한 내용은 [LICENSE](./LICENSE)를 참고하세요.

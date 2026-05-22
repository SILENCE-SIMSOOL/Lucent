# 콘피그 사용방법 가이드

이 가이드는 Lucent를 사용하여 Fabric 모드의 설정(Config)을 구성하고, 저장/로드하며, UI 화면을 열어 설정 메뉴를 표시하는 방법을 다룹니다.

---

## 1. 콘피그 매니저 생성 및 사용

Lucent는 [ModManager](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/config/ModManager.java)를 사용하여 설정을 관리합니다. 본인의 모드를 위한 독립적인 콘피그 폴더를 생성하거나 Lucent의 기본 콘피그를 연동하여 사용할 수 있습니다.

### 독립적인 콘피그 매니저 생성 (권장)
[LucentAPI.createModManager](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/config/api/LucentAPI.java)를 사용하여 고유한 모드 ID가 적용된 매니저를 생성합니다.

```java
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;

public class MyModInitializer {
	public static ModManager config = LucentAPI.createModManager("yourmodid");
}
```
이 코드를 호출하면 `config/yourmodid/` 경로에 설정 폴더가 자동으로 구조화됩니다.

---

## 2. 모듈 클래스 만들기

모드에 설정을 적용하려면 [Mod](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/general/models/abstracts/Mod.java) 클래스를 상속받는 모듈 클래스를 작성해야 합니다. 설정값 필드에 `@ModConfig` 또는 `@ModConfigExtra` 어노테이션을 선언하여 UI와 연동할 수 있습니다.

실제 내장 예제 코드인 [ExampleMod.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java) 파일을 통해 모든 타입의 구현 템플릿을 참고할 수 있습니다.

### 모듈 작성 예시:

```java
import java.awt.Color;
import org.lwjgl.glfw.GLFW;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.models.data.KeyBind;

@ModConfig.CategoryPriority(name = "General", priority = 1000)
public class MyFeatureModule extends Mod {

	public MyFeatureModule() {
		super(
			"기능 이름",            // UI에 표시될 모듈 이름
			"기능에 대한 설명글",    // 툴팁 설명문구
			"CategoryName",        // 사이드바 카테고리 이름
			"tag1, tag2",          // 검색용 태그 키워드
			null                   // 선택적 아이콘 경로 (예: "/assets/mymod/icon.png")
		);
	}

	@ModConfig(
		type = ConfigType.SWITCH,
		name = "기능 활성화",
		description = "메인 기능을 켜고 끕니다.",
		category = "General",
		priority = 100
	)
	public static boolean enableFeature = true;

	@ModConfig(
		type = ConfigType.SLIDER,
		name = "수치 조절",
		min = 0.0, max = 5.0, step = 0.1,
		category = "General",
		priority = 90
	)
	public static double multiplier = 1.0;
}
```

---

## 3. 모드 클래스 등록 및 로드/저장

클라이언트 초기화(`onInitializeClient`) 블록 내에서 생성한 콘피그 매니저에 모듈을 등록합니다.

### 클라이언트 초기화 코드:

```java
@Override
public void onInitializeClient() {
	// 작성한 모듈 인스턴스 등록
	config.register(new MyFeatureModule());

	// 저장되어 있는 설정 파일을 디스크에서 로드
	config.loadGlobalConfig();
	config.loadConfigs();
}
```

### 콘피그 파일 저장 방법:

기본적으로 설정은 UI 변경 시 자동 저장되지만, 소스코드 내에서 직접 변경 후 즉시 강제 저장을 수행하고 싶을 때는 아래 메서드를 호출합니다.

```java
// config/yourmodid/profiles/default/<ModuleName>.json 경로로 설정을 파일로 기록합니다.
config.saveConfigs();
```

---

## 4. 콘피그 스크린 열기

Lucent가 제공하는 모던하고 미려한 설정 화면 GUI를 **ModMenu** 모드와 연동하거나, 별도 키바인드/명령어 호출 시 열리게 설정할 수 있습니다.

[LucentAPI.createConfigScreen](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/config/api/LucentAPI.java)을 통해 마인크래프트 화면(`Screen`) 인스턴스를 가져옵니다.

```java
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.config.api.LucentAPI;

// 화면 인스턴스를 가져와 마인크래프트 클라이언트의 활성 화면으로 설정합니다.
Screen configScreen = LucentAPI.createConfigScreen(config);
Minecraft.getInstance().setScreen(configScreen);
```

---

## 5. LucentAPI 정보

내장 클래스 [LucentAPI](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/config/api/LucentAPI.java)는 모드 연동 시 필요한 핵심 단축 기능들을 제공합니다:

- `createModManager(String directoryName)`: 설정 데이터를 총괄할 매니저 인스턴스를 생성합니다.
- `createConfigScreen(ModManager manager)`: 모던 설정화면 GUI 스크린 객체를 만듭니다.
- `createEditHUDScreen(ModManager manager)`: HUD 배치를 자유롭게 드래그하고 크기 조정할 수 있는 스크린 객체를 만듭니다.
- `registerHUD(ModManager manager, LucentHUD hud)`: HUD 렌더러 컴포넌트를 매니저에 등록합니다.

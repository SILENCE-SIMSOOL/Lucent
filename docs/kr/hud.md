# HUD 콘피그 작성 가이드

이 가이드는 Lucent의 HUD 시스템을 이용하여 모드 화면에 배치 가능한 커스텀 HUD 오버레이 요소(드래그, 스케일링 지원)를 만들고 등록하는 방법을 설명합니다.

---

## 1. HUD 기본 클래스 상속받기

커스텀 HUD 요소를 정의하려면 [LucentHUD](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/models/abstracts/LucentHUD.java) 클래스를 상속받는 클래스를 생성해야 합니다.

실제 내장 예제 코드인 [ExampleHUD.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java) 파일의 구조를 참고하여 쉽고 완성도 높게 구현할 수 있습니다.

### HUD 작성 예시:

```java
import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.enums.Align;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class MyStatusHUD extends LucentHUD {

	public MyStatusHUD() {
		super(
			"my_hud_id",      // 설정 파일에 영구 기록될 HUD 아이디
			MyMod.class,      // (선택사항) 의존 모듈 클래스. 이 모듈이 켜져있을 때만 오버레이를 표시함
			0.05f, 0.1f,      // 화면상 기본 좌표 비율 (0.0 ~ 1.0)
			1.0f,             // 기본 렌더 크기 배율 (스케일)
			Align.LEFT        // 기본 정렬 기준
		);
	}

	@Override
	public RenderType getRenderType() {
		// GPU 파이프라인 렌더러를 사용하려면 NANOVG, 마인크래프트 기본 그래픽스를 쓰려면 MINECRAFT 설정
		return RenderType.NANOVG;
	}

	@Override
	public float getPreviewWidth() {
		return 150f; // 에디터 화면에서 클릭 및 드래그할 수 있는 히트박스 영역의 가로 크기
	}

	@Override
	public float getPreviewHeight() {
		return 20f;  // 에디터 화면에서 클릭 및 드래그할 수 있는 히트박스 영역의 세로 크기
	}

	@Override
	public void draw(GuiGraphics graphics) {
		// HUD 배치 에디터 화면이 열려있거나 F3 디버그 화면일 때는 실시간 출력을 생략
		if (LucentHUD.isEditHudOpen) return;
		
		// 실제 인게임 플레이 화면에 렌더링할 로직
		drawHUDContent(graphics, "현재 속도: 10 m/s");
	}

	@Override
	public void preview(GuiGraphics graphics) {
		// HUD 배치 에디터 화면(`EditHUDScreen`)에서 임시로 보여줄 모양
		drawHUDContent(graphics, "HUD 미리보기");
	}

	private void drawHUDContent(GuiGraphics graphics, String text) {
		float rx = getRenderX(); // 현재 화면 해상도와 스케일이 반영되어 자동 연산된 X 좌표
		float ry = getRenderY(); // 현재 화면 해상도와 스케일이 반영되어 자동 연산된 Y 좌표
		float sw = getScaledWidth();  // 스케일 값이 반영된 히트박스 가로 길이 (width * scale)
		float sh = getScaledHeight(); // 스케일 값이 반영된 히트박스 세로 길이 (height * scale)

		// 반투명 배경 검은색 사각형 렌더링 (NVGRenderer 이용)
		NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 150), 6f * scale);

		// 안내 텍스트 드로우
		NVGRenderer.text(
			text, 
			rx + 6 * scale, 
			ry + (sh - 14f * scale) / 2f, 
			Fonts.PRETENDARD, 
			UIColors.PURE_WHITE, 
			14f * scale
		);
	}
}
```

---

## 2. HUD 등록 및 로드

클라이언트 모드 초기화 시점에 생성한 HUD 인스턴스를 설정 매니저에 연동 등록하고 위치 설정을 불러옵니다.

```java
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.config.api.LucentAPI;

@Override
public void onInitializeClient() {
	// 생성한 모드 설정 매니저에 HUD 오버레이 연동 등록
	LucentAPI.registerHUD(config, new MyStatusHUD());

	// 저장되었던 HUD 레이아웃 상태(위치, 크기 조정값) 파일들을 로드
	HUDManager.INSTANCE.loadAll();
}
```

---

## 3. 인게임 화면에서 HUD 위치 편집하기

사용자가 직접 화면 위에서 HUD의 크기와 위치를 직관적으로 마우스 조절하려면 에디터 GUI 창을 열어야 합니다.

`LucentAPI`를 통해 제공되는 마인크래프트 화면(`Screen`) 객체를 호출하여 엽니다.

```java
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.config.api.LucentAPI;

// 화면 오버레이 레이아웃 배치 에디터 스크린을 호출하여 표시합니다. (종료 시 위치가 자동 저장됨)
Screen editHudScreen = LucentAPI.createEditHUDScreen(config);
Minecraft.getInstance().setScreen(editHudScreen);
```

에디터 화면에서의 조작 방법:
- **마우스 왼쪽 클릭 드래그**: HUD의 위치를 자유롭게 이동시킵니다.
- **마우스 휠 스크롤**: 마우스 커서를 올린 상태로 스크롤하여 HUD의 표시 스케일(크기)을 세밀하게 조절합니다.
- **마우스 오른쪽 클릭**: 해당 HUD의 모서리 정렬 방식을 변경하거나 활성화 여부 등을 편집할 수 있는 컨텍스트 메뉴를 엽니다.

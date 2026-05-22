# 폰트 렌더링 사용방법 가이드

이 가이드는 Lucent 라이브러리 모드에 내장된 폰트 파일과 두 가지 방식(NanoVG GPU 가속 렌더링 및 마인크래프트 기본 그래픽스 렌더링)을 통해 화면에 글씨를 그리는 방법을 설명합니다.

---

## 1. 로드된 폰트 목록

Lucent는 모던하고 시인성이 뛰어난 한글 폰트인 **Pretendard**를 사용하며, 게임 실행 시 비동기 네트워크 다운로드를 진행한 후 메모리에 탑재합니다.

### 사용 가능한 폰트 객체 (via `Fonts` 클래스)
[Fonts](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/nvg/Fonts.java) 클래스에 정의된 스태틱 객체를 참조하여 폰트를 선택할 수 있습니다:

- `Fonts.PRETENDARD_LIGHT` (얇은 굵기)
- `Fonts.PRETENDARD` (보통 굵기 / 기본)
- `Fonts.PRETENDARD_MEDIUM` (중간 굵기)
- `Fonts.PRETENDARD_SEMIBOLD` (굵은 굵기)

내부 메타데이터 정의는 [FontList](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/enums/FontList.java) 열거형에서 확인 가능합니다.

---

## 2. 폰트 텍스트 렌더링 방법

렌더링 파이프라인 구조에 맞춰 아래의 두 가지 방법 중 하나를 선택하여 드로우할 수 있습니다.

### 방법 A: NanoVG GPU 가속 렌더링 (커스텀 GUI 화면 개발 시 추천)
`NVGPIPRenderer` 드로우 스택 내부 또는 NanoVG 프레임버퍼 렌더링을 직접 제어하는 환경에서는 [NVGRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/nvg/NVGRenderer.java)의 정적 메서드를 호출하여 텍스트를 그릴 수 있습니다.

```java
// 일반 텍스트 렌더링
NVGRenderer.text("안녕하세요 세계", x, y, Fonts.PRETENDARD, UIColors.PURE_WHITE, 14f);

// 가로 기준 중앙 정렬 텍스트
NVGRenderer.centerText("가운데 정렬 제목", centerX, y, Fonts.PRETENDARD_SEMIBOLD, UIColors.ACCENT_BLUE, 18f);

// 드롭 섀도우(그림자) 효과가 있는 텍스트
NVGRenderer.textShadow("그림자 텍스트", x, y, Fonts.PRETENDARD_MEDIUM, UIColors.PURE_WHITE, 14f);

// 레이아웃 배치 등을 위한 텍스트의 가로 폭 계산 (px 반환)
float width = NVGRenderer.textWidth("텍스트 길이 측정", Fonts.PRETENDARD, 14f);
```

---

### 방법 B: 마인크래프트 기본 Blit 렌더링 (vanilla GuiGraphics 화면 연동)
NanoVG 컨텍스트 없이 기존 마인크래프트 GUI 오버레이 화면이나 표준 `GuiGraphics`를 받아 드로우하는 구역에서는 [CFontRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/font/CFontRenderer.java) 클래스를 생성해 사용합니다. 이 렌더러는 기존 마인크래프트의 색상 코드(`§a`, `§c` 등) 포맷을 지원합니다.

#### 단계 1: 폰트 렌더러 인스턴스화
AWT `Font` 객체를 먼저 로드한 뒤 `CFontRenderer`로 감싸서 드로우 객체를 생성합니다:

```java
import silence.simsool.lucent.ui.font.CFontRenderer;
import java.awt.Font;

// TTF 폰트 객체 생성
Font systemFont = new Font("Pretendard", Font.PLAIN, 18);
CFontRenderer fontRenderer = new CFontRenderer(systemFont, true); // true = 안티앨리어싱 활성화
```

#### 단계 2: GuiGraphics 환경에서 호출

```java
import net.minecraft.client.gui.GuiGraphics;

// drawString(graphics, text, x, y, ARGB색상값, 그림자여부, 폰트크기px)
int renderedWidth = fontRenderer.drawString(
	graphics, 
	"§a초록색 텍스트 §r와 기본 포맷팅 지원", 
	x, 
	y, 
	0xFFFFFFFF, 
	true, 
	16f
);
```

#### 유용한 헬퍼 메서드:
- `fontRenderer.getStringWidth(String text, float size)`: 색상 기호(`§`)를 제외한 순수 글자들의 실제 픽셀 가로 크기를 반환합니다.
- `fontRenderer.getFontHeight(float size)`: 지정 크기에 적합한 텍스트 줄 간격(높이) 값을 반환합니다.

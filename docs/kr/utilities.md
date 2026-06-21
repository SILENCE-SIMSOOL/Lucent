# 유틸리티 클래스 및 NanoVG 사용방법 가이드

[🏠 메인 README](../../README_KR.md) | [⚙️ 콘피그](config.md) | [📺 HUD](hud.md) | [🔤 폰트](fonts.md) | [🧱 위젯](widgets.md) | [👥 프로필](profiles.md) | [🎨 테마](themes.md) | [🛠️ 유틸리티](utilities.md)

Lucent는 마인크래프트 Fabric 모드 개발 시 유용하게 가져다 쓸 수 있는 다양한 그래픽 렌더러와 유틸리티 클래스들을 탑재하고 있습니다.

---

## 1. NanoVG 렌더링 (최우선 순위)

Lucent는 하드웨어 가속 기반 벡터 그래픽 드로우를 위해 LWJGL NanoVG 바인딩을 번들링하여 제공합니다. 모든 NanoVG 그리기 연산은 마인크래프트 렌더 상태를 해치지 않도록 Picture-in-Picture 프레임버퍼 파이프라인으로 구성되어 있습니다.

### Picture-in-Picture 파이프라인 ([NVGPIPRenderer](../../src/main/java/silence/simsool/lucent/ui/utils/nvg/NVGPIPRenderer.java))
모든 NanoVG 그리기 API 호출은 반드시 `NVGPIPRenderer.draw(...)` 람다 콜백 구문 내부에 작성해야 합니다. 이 래퍼 메서드가 프레임버퍼 바인딩과 OpenGL 상태 복원을 자동으로 처리합니다.

```java
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

// 마인크래프트 표준 GuiGraphics 드로우 단계 내에서 호출
NVGPIPRenderer.draw(guiGraphics, x, y, width, height, () -> {
	// 이 콜백 내부에서의 좌표계는 [0, 0]에서 [width, height]로 로컬 랩핑됩니다.
	NVGRenderer.rect(0, 0, width, height, 0xE618181C, 8f); // 둥근 배경 상자 그리기
	NVGRenderer.outlineRect(0, 0, width, height, 1f, 0xFFFFFFFF, 8f); // 테두리 그리기
});
```

### NanoVG 그리기 메서드 목록 ([NVGRenderer](../../src/main/java/silence/simsool/lucent/ui/utils/nvg/NVGRenderer.java))
드로우 파이프라인 내에서 사용할 수 있는 핵심 드로우 기능들입니다:

```java
// 사각형 그리기 (기본, 둥근 모서리, 4개 모서리 개별 반지름 설정)
NVGRenderer.rect(x, y, w, h, color);
NVGRenderer.rect(x, y, w, h, color, radius);
NVGRenderer.rect(x, y, w, h, color, tl, tr, bl, br);

// 테두리 사각형 그리기
NVGRenderer.outlineRect(x, y, w, h, thickness, color, radius);
NVGRenderer.outlineRect(x, y, w, h, thickness, color, tl, tr, bl, br);

// 그라디언트 채우기 사각형
NVGRenderer.gradientRect(x, y, w, h, color1, color2, GradientType.TOP_TO_BOTTOM, radius);

// 원 그리기 (기본 채우기, 원 테두리)
NVGRenderer.circle(cx, cy, radius, color);
NVGRenderer.outlineCircle(cx, cy, radius, thickness, color);

// 선 및 삼각형
NVGRenderer.line(x1, y1, x2, y2, thickness, color);
NVGRenderer.triangle(x1, y1, x2, y2, x3, y3, color);
NVGRenderer.arrowTriangle(cx, cy, w, h, Direction.DOWN, color); // 아래 방향 드롭다운 화살표 등

// 부드러운 드롭 섀도우 (카드의 입체감/그림자 효과 연출 시 사용)
NVGRenderer.dropShadow(x, y, w, h, blur, spread, radius);

// 고성능 벡터 텍스트 그리기
NVGRenderer.text("텍스트 문구", x, y, Fonts.PRETENDARD, color, fontSize);
NVGRenderer.textShadow("그림자 있는 글씨", x, y, Fonts.PRETENDARD_MEDIUM, color, fontSize);
NVGRenderer.centerText("가운데 정렬", cx, y, Fonts.PRETENDARD_SEMIBOLD, color, fontSize);

// 상태 변환 제어 (스택 구조로 특정 구간에만 크기/각도 적용)
NVGRenderer.push(); // 현재 좌표 정보 저장
NVGRenderer.scale(sx, sy);
NVGRenderer.translate(dx, dy);
NVGRenderer.rotate(radians);
NVGRenderer.globalAlpha(0.5f); // 불투명도 조절
NVGRenderer.pop();  // 저장했던 좌표 정보로 복원

// 시저(Scissor) 클리핑 마스크 처리
NVGRenderer.pushScissor(x, y, w, h);
// ... 지정한 x,y,w,h 사각형 영역 외부로 삐져나가는 그림들은 자동 잘려나감 ...
NVGRenderer.popScissor();

// 격자무늬 체크 배경 그리기 (투명 컬러 선택 시 배경 무늬 등)
NVGRenderer.drawCheckerboard(x, y, w, h, radius);
```

---

## 2. UI 유틸리티 패키지 (`silence.simsool.lucent.ui.utils.*`)

이 패키지는 좌표 기하, 색상 보간, 레이아웃 정렬 및 각종 애니메이션 수식들을 제공합니다:

### [UAnimation](../../src/main/java/silence/simsool/lucent/ui/utils/UAnimation.java)
시간이나 프레임 단위의 수치 변동, 가속도 보간 및 슬라이더 스냅 함수를 담고 있습니다.
- **이징(Easing)**: `Easing.easeOut(t)`, `Easing.easeInOut(t)`, `Easing.spring(t)` (약간 튕기는 스프링 효과), `Easing.elastic(t)`.
- **보간(Lerp)**: `lerp(a, b, t)`, `lerpColor(c1, c2, t)`, `lerpSnap(current, target, speed, delta)` (목표값과 가까워지면 정확히 스냅).
- **시간 기반 효과**: `getPulseAlpha(speed)` (0~1 사인파), `getWaveOffset(speed, amplitude)`.
- **진행도 갱신**: `stepProgress(current, forward, speed, delta)` (프레임마다 0.0과 1.0 사이의 변화 진행도 계산).

### [UColor](../../src/main/java/silence/simsool/lucent/ui/utils/UColor.java)
ARGB 정수형 색상을 자유자재로 다루고 변환하는 클래스입니다.
- `argb(a, r, g, b)`, `withAlpha(color, alpha)`, `withAlphaF(color, floatAlpha)`.
- `darken(color, amount)` (어둡게), `brighten(color, amount)` (밝게).
- `toHSV(color)`, `fromHSVA(h, s, v, a)`.
- `toHex(color)` (결과값: `"#RRGGBBAA"`), `fromHex(String hex)`.

### [UCorner](../../src/main/java/silence/simsool/lucent/ui/utils/UCorner.java)
사각형의 4개 모서리(tl, tr, bl, br) 반경 데이터를 보관하는 자바 레코드 객체입니다.
- `UCorner.of(radius)` (사방 동일), `UCorner.top(radius)`, `UCorner.bottom(radius)`.
- `clampToBox(w, h)`: 사각형 크기보다 반지름이 커져 모양이 일그러지는 것을 안전하게 방지합니다.

### [UIColors](../../src/main/java/silence/simsool/lucent/ui/utils/UIColors.java)
기본 내장 컬러 색상표 및 가변 테마 색상(예: `PURE_WHITE`, `ACCENT_BLUE`, `WIN_BG`) 변수들을 보관합니다. 사용자가 테마를 변경하면 값이 실시간 동적 매핑됩니다.

### [ULayout](../../src/main/java/silence/simsool/lucent/ui/utils/ULayout.java)
화면 배치 및 좌표 정렬 계산을 돕습니다.
- `centerX(cX, cW, childW)` / `centerY(cY, cH, childH)`.
- `isHovered(mouseX, mouseY, x, y, w, h)`: 마우스 호버 영역 판정.
- `fitInside(srcW, srcH, maxW, maxH)` / `fillCover(srcW, srcH, targetW, targetH)`.
- `Insets` 레코드를 통해 외부 여백(Margin)과 패딩(Padding)을 관리합니다.

### [URender](../../src/main/java/silence/simsool/lucent/ui/utils/URender.java)
마인크래프트 기본 그래픽스 레이어 위에 그리는 유틸리티입니다.
- `drawRect(graphics, x, y, w, h, color)`.
- `drawBorder(graphics, x, y, w, h, borderW, color)`.
- `drawPlayerHead(graphics, x, y, size, UUID)`: 마인크래프트 플레이어 스킨 머리 렌더링.

---

## 3. 일반 공통 및 그래픽 렌더 유틸리티 (`silence.simsool.lucent.general.utils.*`)

모드 로직 내부 연산이나 마인크래프트 전용 백엔드 리포터 등을 보조합니다.

### 튜플 자료 구조
- **[Pair](../../src/main/java/silence/simsool/lucent/general/utils/Pair.java)**: 범용 페어 객체 `(A, B)`.
- **[Triple](../../src/main/java/silence/simsool/lucent/general/utils/Triple.java)**: 범용 트리플 객체 `(A, B, C)`.

### 코어 연산 유틸
- **[ClientHandler](../../src/main/java/silence/simsool/lucent/general/utils/ClientHandler.java)**: 마인크래프트 클라이언트 메인 스레드 상에서 안전하게 코드가 비동기/예약 실행되도록 위임합니다.
- **[MinecraftColor](../../src/main/java/silence/simsool/lucent/general/utils/MinecraftColor.java)**: 마인크래프트 서식 기호 문자(`§`)를 RGB 컬러 정수로 매핑합니다.
- **[NumberUtils](../../src/main/java/silence/simsool/lucent/general/utils/NumberUtils.java)**: 안전한 정수/실수 파싱 기능 및 단위 계산 헬퍼.
- **[ScoreboardUtils](../../src/main/java/silence/simsool/lucent/general/utils/ScoreboardUtils.java)**: 스코어보드 값, 텍스트 라인 긁어오기 및 활성 타이틀 체크 기능 제공.

### 마인크래프트 전용 그래픽스 유틸
- **[RenderUtils](../../src/main/java/silence/simsool/lucent/general/utils/render/RenderUtils.java)**: 블렌딩 모드 설정, 가위 클리핑, 포즈 좌표계 보정.
- **[DrawContextRenderer](../../src/main/java/silence/simsool/lucent/general/utils/render/DrawContextRenderer.java)** / **[DrawContextUtils](../../src/main/java/silence/simsool/lucent/general/utils/render/DrawContextUtils.java)**: 표준 마이크래프트 `GuiGraphics` 활용 유틸.
- **[ItemRenderer](../../src/main/java/silence/simsool/lucent/general/utils/render/ItemRenderer.java)**: 2D GUI 화면 상에 3D 아이템 스택 아이템 아이콘(내구도 바, 오버레이 카운트 포함)을 렌더링합니다.
- **[RoundRectPIPRenderer](../../src/main/java/silence/simsool/lucent/general/utils/render/RoundRectPIPRenderer.java)**: 화면 영역을 동그란 모서리로 가려 그릴 때 쓰는 PIP 마스크 쉐이더 렌더러입니다.

---

## 4. 유용한 인게임 단축 헬퍼 (`silence.simsool.lucent.general.utils.useful.*`)

자주 사용하는 마인크래프트 내부 API 및 시스템 명령어 접근을 간소화한 유틸들입니다:

- **[UChat](../../src/main/java/silence/simsool/lucent/general/utils/useful/UChat.java)**: 클라이언트 채팅창 메시지 출력(`chat(msg)`) 및 서버로 채팅/명령어 발송(`say(msg)`).
- **[UDesktop](../../src/main/java/silence/simsool/lucent/general/utils/useful/UDesktop.java)**: 웹 브라우저 열기, 로컬 텍스트 파일 실행(편집), 클립보드 제어 및 OS 알림 창 팝업.
- **[UDisplay](../../src/main/java/silence/simsool/lucent/general/utils/useful/UDisplay.java)**: 프레임버퍼 크기, GUI 스케일 정보 조회 및 F3 디버그 화면 활성 여부 확인.
- **[UFile](../../src/main/java/silence/simsool/lucent/general/utils/useful/UFile.java)**: 비동기 방식의 네트워크 파일 다운로드 헬퍼.
- **[UInventory](../../src/main/java/silence/simsool/lucent/general/utils/useful/UInventory.java)**: 플레이어의 아이템 소지 여부 검색, 인벤토리 스캔 및 특정 아이템 합계 개수 집계.
- **[UKeyboard](../../src/main/java/silence/simsool/lucent/general/utils/useful/UKeyboard.java)**: 특정 GLFW 키가 눌려 있는 상태인지 신속 조회.
- **[ULog](../../src/main/java/silence/simsool/lucent/general/utils/useful/ULog.java)**: 지정 모드 태그 이름 포맷으로 콘솔 로그 출력.
- **[UMouse](../../src/main/java/silence/simsool/lucent/general/utils/useful/UMouse.java)**: 마우스 커서의 좌표 획득(물리 좌표 및 GUI 스케일 반영 좌표) 및 특정 다각형 영역에 호버 상태인지 검사.
- **[UObject](../../src/main/java/silence/simsool/lucent/general/utils/useful/UObject.java)**: Null 검사 보조 및 객체의 안전한 캐스팅.
- **[UPacket](../../src/main/java/silence/simsool/lucent/general/utils/useful/UPacket.java)**: 서버로 커스텀 연결 패킷 송신.
- **[URender](../../src/main/java/silence/simsool/lucent/general/utils/useful/URender.java)**: NanoVG API를 이용하여 공통 시각 요소(예: 토글 버튼)를 정적 위치에 빠르게 그리기.
- **[UScreen](../../src/main/java/silence/simsool/lucent/general/utils/useful/UScreen.java)**: 안전하게 특정 GUI 화면으로 스크린 전환.
- **[USlot](../../src/main/java/silence/simsool/lucent/general/utils/useful/USlot.java)**: 마인크래프트 컨테이너 슬롯 상의 데이터 접근 보조.
- **[USound](../../src/main/java/silence/simsool/lucent/general/utils/useful/USound.java)**: 게임 내 음향 효과음 간편 재생.
- **[UText](../../src/main/java/silence/simsool/lucent/general/utils/useful/UText.java)**: 서식 제어 문자 제거 및 문자열 조작.
- **[UThread](../../src/main/java/silence/simsool/lucent/general/utils/useful/UThread.java)**: 백엔드 모드 스레드 풀 실행 환경 지원.
- **[UTitle](../../src/main/java/silence/simsool/lucent/general/utils/useful/UTitle.java)**: 화면 중앙에 타이틀/서브타이틀 오버레이 출력.
- **[UWorld](../../src/main/java/silence/simsool/lucent/general/utils/useful/UWorld.java)**: 현재 로드된 엔티티들 분석, 블록 정보 추출 및 플레이어 월드 좌표계 접근.

# UI 위젯 사용방법 가이드

Lucent는 모드 개발자가 GUI 화면을 자유롭게 만들 수 있도록 다양하고 현대적인 UI 컨트롤 위젯 컴포넌트를 기본 제공합니다. 모든 위젯은 자체적으로 마우스/키보드 입력 이벤트 처리, 포커스 제어, 부드러운 호버 애니메이션 기능을 완벽히 내장하고 있습니다.

모든 UI 구성요소는 상위 추상 클래스인 [UIWidget](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/UIWidget.java)을 상속받습니다.

---

## 1. 제공되는 위젯 종류

### ActionButton
클릭했을 때 지정된 콜백 람다식(`Runnable`)을 즉시 실행하는 일반적인 클릭 버튼입니다.
- **소스코드 링크**: [ActionButton.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/ActionButton.java)
- **주요 용도**: 초기화 명령 실행, 서브 메뉴 이동, 동작 실행.

---

### ToggleButton
참/거짓(`true`/`false`) 값을 스위치 슬라이더 애니메이션 형태로 제어하는 토글 스위치입니다.
- **소스코드 링크**: [ToggleButton.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/ToggleButton.java)
- **주요 용도**: 모드 기능의 켜기/끄기 설정.

---

### Slider
바 형태의 트랙을 드래그하여 수치 범위 내의 값(`int`, `float`, `double`)을 직관적으로 증감시키는 슬라이더 바입니다.
- **소스코드 링크**: [Slider.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/Slider.java)
- **주요 용도**: 투명도 비율, 곱하기 배율, 좌표 오프셋 조절.

---

### Selector
사전에 입력한 문자열 배열 리스트 중에서 클릭 시 순환하며 한 가지 값을 고르는 선택 박스입니다.
- **소스코드 링크**: [Selector.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/Selector.java)
- **주요 용도**: 렌더링 스타일 형태 선택, UI 레이아웃 모드 전환.

---

### TextBox
클릭하여 포커스를 준 후, 텍스트 입력을 키보드로 받아들이는 한 줄 글상자 입력 필드입니다.
- **소스코드 링크**: [TextBox.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/TextBox.java)
- **주요 용도**: 이름 입력, API 토큰 기록, 검색 필터 검색어 입력.

---

### KeyBindButton
사용자가 키보드 키 또는 마우스 버튼을 누르면 해당 키 값을 입력 캡처하여 저장해주는 바인딩 전용 버튼입니다.
- **소스코드 링크**: [KeyBindButton.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/KeyBindButton.java)
- **주요 용도**: 기능 단축키 지정.

---

### ColorPicker & ColorPickerButton
- **ColorPicker**: 마우스로 색상 채도/명도판을 클릭하고, 불투명도 및 색조를 아래 슬라이더로 조절하거나 직접 Hex Code 문자열을 입력하여 색상값을 추출하는 고성능 컬러 피커 캔버스입니다.
- **ColorPickerButton**: 작은 색상 스와치 버튼으로, 클릭 시 전체 `ColorPicker` 선택창 팝업을 띄웁니다.
- **소스코드 링크**:
	- [ColorPicker.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/color/ColorPicker.java)
	- [ColorPickerButton.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/color/ColorPickerButton.java)
- **주요 용도**: 텍스트 강조 컬러 설정, HUD 배경이나 테두리 색상 지정.

---

## 2. 개별 위젯 수동 드로우 예제

모듈식으로 콘피그 화면 외에 본인의 커스텀 스크린 화면 내부에 직접 위젯을 그려 활용하려면 다음과 같이 작성합니다:

```java
import silence.simsool.lucent.ui.widget.components.ToggleButton;

public class MyCustomScreen extends Screen {
	private ToggleButton myToggle;

	@Override
	protected void init() {
		// 위젯 생성 및 위치 지정 (x, y, 가로크기, 세로크기)
		myToggle = new ToggleButton(50, 50, 40, 20);
		myToggle.setOn(true); // 기본 상태 활성화
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		// NanoVG 렌더링 프레임 안에서 그립니다.
		NVGPIPRenderer.draw(graphics, 0, 0, width, height, () -> {
			myToggle.draw(mouseX, mouseY, delta);
		});
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// 위젯 클릭 범위 판정 및 클릭 액션 위임
		if (myToggle.mouseClicked(mouseX, mouseY, button)) {
			boolean checkState = myToggle.isOn();
			// 상태 변경에 대한 추가 행동 정의
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
```
각 위젯들은 독립적으로 동작하므로 위 메서드 호출만 연결해주면 호버/애니메이션/상태 제어가 완벽히 자동 구동됩니다.

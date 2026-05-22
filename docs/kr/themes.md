# 테마 설정방법 가이드

[🏠 메인 README](../../README_KR.md) | [⚙️ 콘피그](config.md) | [📺 HUD](hud.md) | [🔤 폰트](fonts.md) | [🧱 위젯](widgets.md) | [👥 프로필](profiles.md) | [🎨 테마](themes.md) | [🛠️ 유틸리티](utilities.md)

Lucent의 콘피그 화면은 개발자와 사용자 취향에 맞춰 시각적으로 변화를 줄 수 있는 여러 가지 테마들을 내장하고 있습니다. 사용자가 직접 GUI 환경설정 창에서 원하는 테마를 마우스로 교체할 수 있을 뿐만 아니라, 개발자가 코드 상에서 특정 테마를 기본으로 강제 설정할 수도 있습니다.

---

## 1. 기본 제공 테마 종류

Lucent는 다음 6가지의 테마 팔레트를 가지고 있습니다:

| 테마 이름 | 시각적 특징 / 지향하는 스타일 |
|---|---|
| **Default** | 파란색 강조 톤을 바탕으로 설계된 클래식한 무채색 다크 테마 |
| **Glass Morphic** | 뒤의 뒷배경이 흐릿하게 투과해 비치는 모던한 반투명 아크릴 스타일 |
| **Midnight Cyan** | 심연의 우주 같은 딥네이비 색상과 생동감 넘치는 청록색(Cyan) 하이라이트 |
| **Crimson Aurora** | 오로라를 연상시키는 고혹적인 진홍색과 보랏빛 그라데이션 포인트 |
| **Emerald Mist** | 차분하고 눈이 편안한 에메랄드 초록색 계열의 그리너리 스타일 |
| **Amethyst Eclipse** | 깊은 자수정 빛깔의 고급스럽고 신비로운 퍼플 색상 구성 |

---

## 2. API 사용방법

[ThemeManager](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/theme/ThemeManager.java) 클래스를 활용하여 테마를 검색하고 설정 화면에 적용할 수 있습니다.

### 코드에서 테마 지정하기
원하는 특정 테마 스타일을 강제로 스크린에 로딩시키려면 다음과 같이 작성합니다:

```java
import silence.simsool.lucent.ui.theme.ThemeManager;

// "Midnight Cyan" 테마를 적용합니다.
ThemeManager.applyTheme(ThemeManager.findTheme("Midnight Cyan"));
```

### 현재 적용된 테마 정보 확인하기
현재 UI에 로딩 중인 테마의 속성을 가져오려면 다음 코드를 사용합니다:

```java
// 현재 선택된 테마의 LucentTheme 정보를 받아옵니다.
LucentTheme activeTheme = ThemeManager.getCurrentTheme();

// 테마명 문자열 반환
String themeName = activeTheme.getName();
```
테마가 변경되면 [UIColors](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/UIColors.java)에 설정된 다이내믹 컬러 변수값들이 연동되어 실시간으로 자동 갱신됩니다.

<h3 align="center">
	<img src="https://raw.githubusercontent.com/SILENCE-SIMSOOL/Lucent/main/docs/icon.png" alt="Lucent Icon" width="128" height="128" />
</h3>

<h1 align="center">루센트</h1>

<p align="center">
	마인크래프트 Fabric 모드 개발을 위한 라이브러리로, 최신 설정 시스템, 커스터마이징 가능한 HUD 엔진, 그리고 하드웨어 가속 기반 Vector UI 툴킷을 제공합니다.
</p>

<p align="center">
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent/tree/main/docs">문서</a>
	·
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">버그 제보</a>
	·
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">기능 제안</a>
</p>

<p align="center">
	<a href="./README.md">영어 문서 보러가기</a>
</p>

---

## 문서

Lucent의 시스템과 기능들을 카테고리별 가이드 문서로 확인할 수 있습니다.

### ⚙️ 설정 시스템
- **[콘피그 통합 가이드](./docs/kr/config.md)** 어노테이션 기반 설정 매니저, 모듈 클래스, 저장/로드 시스템, 설정 GUI 연동 방법을 다룹니다.  
  예제: [`ExampleMod.java`](https://github.com/SILENCE-SIMSOOL/Lucent/blob/main/src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java)
- **[이벤트 시스템 가이드](./docs/kr/events.md)** 채팅 메시지 수신, 틱 이벤트, 렌더링, 패킷 송수신, 엔티티 로드 등 다양한 게임 내 이벤트를 구독합니다.

### 📺 HUD 시스템
- **[HUD 설정 가이드](./docs/kr/hud.md)** 드래그 및 크기 조절이 가능한 인게임 HUD 요소와 위젯 제작 방법을 설명합니다.  
  예제: [`ExampleHUD.java`](https://github.com/SILENCE-SIMSOOL/Lucent/blob/main/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java)

### 🎨 UI 및 스타일링
- **[UI 위젯 가이드](./docs/kr/widgets.md)** 스위치, 슬라이더, 드롭다운, 텍스트박스, 컬러 피커 등 즉시 사용할 수 있는 UI 위젯들을 제공합니다.

- **[테마 설정 가이드](./docs/kr/themes.md)** Midnight Cyan, Glass Morphic, Crimson Aurora 등의 UI 테마 커스터마이징 방법을 설명합니다.

### 🔤 렌더링
- **[폰트 렌더링 가이드](./docs/kr/fonts.md)** Pretendard 폰트를 NanoVG 및 Minecraft 렌더링 환경에서 사용하는 방법을 설명합니다.

- **[유틸리티 클래스 및 NanoVG 가이드](./docs/kr/utilities.md)** GPU 가속 렌더링 유틸리티, 그림자, 그라디언트, 도형 렌더링 및 Minecraft 헬퍼 클래스를 다룹니다.

### 👥 프로필
- **[프로필 관리 가이드](./docs/kr/profiles.md)** 설정 프로필 및 프로필별 HUD 레이아웃 관리 방법을 설명합니다.

## 왜 루센트를 사용해야 하나요?

**추천 환경:** 클라이언트 사이드 모드 개발, 하이픽셀 스카이블럭 모드 개발

### 👨‍💻 개발자 관점

- **모드 용량 및 중복 연산 감소.** 여러 모드가 Lucent를 공유할 경우 중복 코드와 반복 계산이 한 번에 제거됩니다.
- **스마트한 내장 이벤트.** Lucent의 이벤트들은 null 체크와 자주 쓰이는 계산들을 미리 처리해두어, 이벤트를 사용할 때마다 중복 연산을 반복할 필요가 없습니다. 성능을 높이고 코드를 더 간결하고 읽기 쉽게 만들어줍니다.
- **풍부한 유틸리티.** 개발 속도를 높이고 보일러플레이트를 줄이는 다양한 유틸리티가 내장되어 있습니다.
- **버전 호환 API.** Lucent의 유틸과 이벤트는 마인크래프트 버전이 바뀌어도 그대로 사용할 수 있도록 설계되어 유지보수 부담이 없습니다.
- **간편한 config 관리.** 어노테이션 기반의 심플한 config 시스템이 저장, 로드, GUI를 모두 처리해주어 config 걱정을 하지 않아도 됩니다.
- **모든 수준에 적합.** 간단한 기능부터 세세한 고급 기능까지 모두 갖춰져 있어 모든 규모의 모드와 개발자에게 맞습니다.

### 🎮 유저 관점

- **매우 가볍고 성능 저하 없음.** Lucent는 오버헤드를 최소화하도록 최적화되어 있습니다.
- **깔끔하고 현대적인 UI/UX.** 일반적인 config 라이브러리와 달리, 매우 가독성 높은 설정 화면으로 사용 경험을 높여줍니다.
- **통합 관리.** 여러 모드와 그 HUD 요소들을 하나의 화면에서 편리하게 관리할 수 있습니다.
- **독립적인 config 저장방식.** Lucent의 config는 독립적인 로컬 파일에 저장되어, 마인크래프트 버전이 바뀌거나 로드 프로필이 달라져도 하나의 PC에서 동일한 설정을 그대로 사용할 수 있습니다. Prism Launcher에서 여러 프로필을 생성하거나 Minecraft Launcher를 사용해도 모든 곳에서 하나의 config로 동작합니다. 또한 Lucent 프로필을 통해 여러 프로필을 생성할 수 있으며 게임 안에서 클릭 한 번으로 프로필을 바꿀 수 있습니다.

---

## 요구사항

### 🟢 Minecraft 1.21.11 환경
| 의존성 | 버전 |
| :-- | :-- |
| Minecraft | `1.21.11` |
| Fabric Loader | `>= 0.18.4` |
| Fabric API | `>= 0.141.3+1.21.11` |
| LWJGL NanoVG | `3.3.3` *(번들 포함)* |

### 🟡 Minecraft 26.2 환경
| 의존성 | 버전 |
| :-- | :-- |
| Minecraft | `26.2` |
| Fabric Loader | `>= 0.19.3` |
| Fabric API | `>= 0.152.2+26.2` |
| LWJGL NanoVG | `3.3.4` *(번들 포함)* |

---

## 통합 방법

### 빌드 타입

| 타입 | 설명 |
| :-- | :-- |
| **[0]&nbsp;Mod** | NanoVG가 포함된 독립형 모드 빌드입니다. 사용자는 Lucent를 별도로 설치해야 하며, 최종 모드 용량을 줄일 수 있습니다. |
| **[1]&nbsp;Library** | NanoVG 없이 제공되는 내장형 라이브러리 빌드입니다. Lucent가 JIJ 방식으로 모드 내부에 포함되므로 별도 설치가 필요하지 않습니다. |

### 최신 버전

[![최신 릴리즈](https://img.shields.io/github/v/release/SILENCE-SIMSOOL/Lucent?color=E0E0E0&style=flat-square)](https://github.com/SILENCE-SIMSOOL/Lucent/releases)

### 버전 형식

> **`[Lucent 버전]`**-**`[마인크래프트 버전]`**-**`[빌드 타입]`**

* **`Lucent 버전`**: 라이브러리의 고유 배포 버전 (예: `1.0.0`)
* **`마인크래프트 버전`**: 대상 마인크래프트 구동 환경 (예: `1.21.11` 또는 `26.2`)
* **`빌드 타입`**: 독립형 모드는 `0`, 내장형 라이브러리는 `1`

**💡 예시:**
* `1.0.0-1.21.11-0` (1.21.11 버전 전용 독립형 모드 빌드)
* `1.0.0-26.2-1` (26.2 버전 전용 내장형 라이브러리 빌드)

---

## Gradle 설정

`build.gradle`에 Lucent Maven 저장소를 추가하세요:

```groovy
repositories {
	maven {
		url "[https://SILENCE-SIMSOOL.github.io/maven-repo/](https://SILENCE-SIMSOOL.github.io/maven-repo/)"
	}
}

```

그다음 사용 중인 마인크래프트 대상 버전에 맞춰 아래 의존성 설정을 추가하세요.

### [0] 독립형 모드

```groovy
dependencies {
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.0.0-1.21.11-0"
}

```

### [1] 내장형 라이브러리 (JIJ)

```groovy
dependencies {
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.0.0-1.21.11-1"
	include "com.github.SILENCE-SIMSOOL:lucent:1.0.0-1.21.11-1"
}

```

---

## 크레딧

* 콘피그 설정 GUI 화면 디자인(Config Screen UI)은 Polyfrost의 **[OneConfig](https://github.com/Polyfrost/OneConfig)** 디자인 레이아웃에서 영감을 받아 구현되었습니다.
* NanoVG 렌더링 구조 및 유틸리티 드로우 시스템은 odtheking의 **[Odin](https://github.com/odtheking/Odin)** 모드에서 영감을 받았습니다.
* 일부 이벤트 기능들은 Synnerz의 **[Devonian](https://github.com/Synnerz/devonian)** 모드를 참고하여 제작되었습니다.
* **[Minecraft](https://www.minecraft.net/)** 및 **[Fabric Project](https://fabricmc.net/)** 모딩 생태계를 기반으로 제작되었습니다.

---

## 라이선스

본 프로젝트는 **MIT License**를 따릅니다.

자세한 내용은 [`LICENSE`](https://www.google.com/search?q=./LICENSE)를 참고하세요.

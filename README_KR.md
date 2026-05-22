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
	<a href="./README.md">🇺🇸 English Documentation</a>
</p>

---

## 문서

Lucent의 시스템과 기능들을 카테고리별 가이드 문서로 확인할 수 있습니다.

### ⚙️ 설정 시스템
- **[콘피그 통합 가이드](./docs/kr/config.md)**  
  어노테이션 기반 설정 매니저, 모듈 클래스, 저장/로드 시스템, 설정 GUI 연동 방법을 다룹니다.  
  예제: [`ExampleMod.java`](https://github.com/SILENCE-SIMSOOL/Lucent/blob/main/src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java)

### 📺 HUD 시스템
- **[HUD 설정 가이드](./docs/kr/hud.md)**  
  드래그 및 크기 조절이 가능한 인게임 HUD 요소와 위젯 제작 방법을 설명합니다.  
  예제: [`ExampleHUD.java`](https://github.com/SILENCE-SIMSOOL/Lucent/blob/main/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java)

### 🎨 UI 및 스타일링
- **[UI 위젯 가이드](./docs/kr/widgets.md)**  
  스위치, 슬라이더, 드롭다운, 텍스트박스, 컬러 피커 등 즉시 사용할 수 있는 UI 위젯들을 제공합니다.

- **[테마 설정 가이드](./docs/kr/themes.md)**  
  Midnight Cyan, Glass Morphic, Crimson Aurora 등의 UI 테마 커스터마이징 방법을 설명합니다.

### 🔤 렌더링
- **[폰트 렌더링 가이드](./docs/kr/fonts.md)**  
  Pretendard 폰트를 NanoVG 및 Minecraft 렌더링 환경에서 사용하는 방법을 설명합니다.

- **[유틸리티 클래스 및 NanoVG 가이드](./docs/kr/utilities.md)**  
  GPU 가속 렌더링 유틸리티, 그림자, 그라디언트, 도형 렌더링 및 Minecraft 헬퍼 클래스를 다룹니다.

### 👥 프로필
- **[프로필 관리 가이드](./docs/kr/profiles.md)**  
  설정 프로필 및 프로필별 HUD 레이아웃 관리 방법을 설명합니다.

## 요구사항

| 의존성 | 버전 |
| :-- | :-- |
| Minecraft | `1.21.11` |
| Fabric Loader | `>= 0.18.4` |
| Fabric API | `>= 0.141.3+1.21.11` |
| LWJGL NanoVG | `3.3.3` *(번들 포함)* |

---

## 통합 방법

### 빌드 타입

| 타입 | 설명 |
| :-- | :-- |
| **[0]&nbsp;Mod** | NanoVG가 포함된 독립형 모드 빌드입니다. 사용자는 Lucent를 별도로 설치해야 하며, 최종 모드 용량을 줄일 수 있습니다. |
| **[1]&nbsp;Library** | NanoVG 없이 제공되는 내장형 라이브러리 빌드입니다. Lucent가 JIJ 방식으로 모드 내부에 포함되므로 별도 설치가 필요하지 않습니다. |

### 최신 버전

`1.1.3`

### 버전 형식

`<lucent_version>-<mc_version>-<type>`

예시: `1.1.3-1.21.11-0`

---

## Gradle 설정

`build.gradle`에 Lucent Maven 저장소를 추가하세요:

```groovy
repositories {
	maven {
		url "https://SILENCE-SIMSOOL.github.io/maven-repo/"
	}
}
```

그다음 아래 둘 중 하나의 의존성 설정을 추가하세요.

### [0] 독립형 모드

```groovy
dependencies {
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-0"
}
```

### [1] 내장형 라이브러리 (JIJ)

```groovy
dependencies {
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
	include "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
}
```

---

## 크레딧

- 콘피그 설정 GUI 화면 디자인(Config Screen UI)은 Polyfrost의 **[OneConfig](https://github.com/Polyfrost/OneConfig)** 디자인 레이아웃에서 영감을 받아 구현되었습니다.
- NanoVG 렌더링 구조 및 유틸리티 드로우 시스템은 odtheking의 **[Odin](https://github.com/odtheking/Odin)** 모드에서 영감을 받았습니다.
- **[Minecraft](https://www.minecraft.net/)** 및 **[Fabric Project](https://fabricmc.net/)** 모딩 생태계를 기반으로 제작되었습니다.

---

## 라이선스

본 프로젝트는 **MIT License**를 따릅니다.  
자세한 내용은 [`LICENSE`](./LICENSE)를 참고하세요.

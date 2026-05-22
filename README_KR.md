<a id="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent">
		<img src="docs/icon.png" alt="Logo" width="128" height="128">
	</a>

	<h1 align="center">Lucent (루센트)</h1>

	<p align="center">
		Minecraft 1.21.11 Fabric 환경을 위한 라이브러리 모드로, 모드 개발자들에게 어노테이션 기반 설정 관리 시스템, 커스터마이징 가능한 HUD 오버레이 엔진, 하드웨어 가속(GPU) Vector UI 툴킷을 제공합니다.
		<br />
		<br />
		🌐 <a href="./README.md">English Documentation</a>
		·
		<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">버그 제보</a>
		·
		<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">기능 제안</a>
	</p>
</div>

---

## 카테고리별 세부 가이드

특정 기능들의 작동 원리 및 사용법을 세분화하여 개별 문서로 준비해 두었습니다:

- ⚙️ **[콘피그 사용방법 가이드](./docs/kr/config.md)** (예제: [ExampleMod.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java)) — 어노테이션 기반 설정 매니저 및 모듈 클래스 정의, 설정 파일의 저장/로드와 설정화면 GUI 화면 출력 방법.
- 📺 **[HUD 콘피그 작성 가이드](./docs/kr/hud.md)** (예제: [ExampleHUD.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java)) — 마우스 드래그와 스케일 조절을 지원하는 커스텀 인게임 오버레이(HUD) 요소 구현 방법.
- 🔤 **[폰트 렌더링 사용방법 가이드](./docs/kr/fonts.md)** — 모던 Pretendard 폰트 목록 및 마인크래프트 또는 NanoVG GPU 환경에서의 문자열 드로우 방법.
- 🧱 **[UI 위젯 사용방법 가이드](./docs/kr/widgets.md)** — 스위치, 슬라이더, 드롭다운, 글상자, 컬러피커 등 즉시 조립하여 사용하는 UI 위젯 카탈로그 및 코드 매핑.
- 👥 **[프로필 설정방법 가이드](./docs/kr/profiles.md)** — 게임 상황(PVP, 야생 등)에 맞춰 전체 설정 파일들을 프로필 단위로 나누어 관리하고 실시간 변경하는 방법.
- 🎨 **[테마 설정방법 가이드](./docs/kr/themes.md)** — UI 화면 색상 및 스타일 테마(Midnight Cyan, 아크릴 투명 테마 등) 종류와 코드 적용법.
- 🛠️ **[유틸리티 클래스 및 NanoVG 가이드](./docs/kr/utilities.md)** — 벡터 그래픽 드로우(원, 사각형, 그림자, 그라디언트) 파이프라인 사용법 및 개발 생산성을 올려주는 마인크래프트 단축 유틸 목록.

---

## 요구사항

| 의존 모드 | 버전 조건 |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | ≥ 0.141.3+1.21.11 |
| LWJGL NanoVG | 3.3.3 (번들 내장) |

---

## 프로젝트 통합 방법

### 라이브러리 종류

| 빌드 타입 | 세부 내용 |
|---|---|
| **[0] mod** | 완전하게 빌드된 모드 파일. 플레이어가 Lucent 모드를 마인크래프트에 별도로 넣어 사용해야 합니다. 모드 자체의 용량을 크게 줄일 수 있습니다. |
| **[1] library** | 경량 빌드 라이브러리. 모드 내부에 Lucent 기능이 JIJ 형태로 동적 포함되므로, 플레이어가 Lucent를 별도로 수동 설치할 필요가 없습니다. |

### 최신 버전
- **`1.1.3`**

### 버전 표기 형식

- 포맷: `<lucent_version>-<mc_version>-<type>`
- 예시: `1.1.3-1.21.11-0`

### Gradle 설정 예시 (`build.gradle`)

프로젝트 빌드 파일에 아래 메이븐 저장소와 의존성 구문을 삽입합니다:

```groovy
repositories {
	maven { url "https://SILENCE-SIMSOOL.github.io/maven-repo/" }
}

dependencies {
	// [0] mod 형식을 라이브러리로 탑재할 때
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-0"

	// [1] library 형식을 모드 내부에 내장(JIJ bundle)하여 배포할 때
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
	include "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
}
```

---

## 참고 및 오픈소스 크레딧

- 콘피그 설정 GUI 화면 디자인(Config Screen UI)은 Polyfrost의 **[OneConfig](https://github.com/Polyfrost/OneConfig)** 디자인 레이아웃에서 영감을 받아 구현되었습니다.
- 본 라이브러리는 **[Odin](https://github.com/odtheking/Odin)** 모드(Hypixel Skyblock 모딩용 모드, 개발자: `odtheking`)의 NanoVG GPU 렌더링 설계 방식과 렌더링 유틸리티 드로우 메서드 구성을 참고하여 제작되었습니다.
- **[Minecraft](https://www.minecraft.net/)** 및 **[Fabric Project](https://fabricmc.net/)** 개발 에코시스템 환경 하에서 제작되었습니다.

---

## 라이선스

MIT License — 상세 내용은 [LICENSE](./LICENSE)를 참고하세요.

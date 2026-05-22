# 프로필 설정방법 가이드

[🏠 메인 README](../../README_KR.md) | [⚙️ 콘피그](config.md) | [📺 HUD](hud.md) | [🔤 폰트](fonts.md) | [🧱 위젯](widgets.md) | [👥 프로필](profiles.md) | [🎨 테마](themes.md) | [🛠️ 유틸리티](utilities.md)

Lucent는 모드 사용자별로 설정을 다르게 저장하고 불러올 수 있는 프로필(Profiles) 기능을 제공합니다. 활성화된 모든 모듈 설정값과 HUD 위치 배치는 `config/<modid>/profiles/<profile_name>/` 디렉토리 아래에 완전히 격리되어 저장됩니다.

이를 통해 플레이어는 "PVP용", "건축용", "기본" 등 게임 플레이 상황에 맞춰 전체 설정을 손쉽게 실시간으로 전환할 수 있습니다.

---

## 1. 프로필 디렉토리 구조
사용자가 새로운 프로필을 만들면 마인크래프트 설정 폴더 하위에 다음과 같은 JSON 파일 그룹들이 생성되어 보관됩니다:

```text
config/
└── yourmodid/
    └── profiles/
        ├── default/           <-- 기본 프로필 저장 폴더
        │   ├── MyModule.json
        │   └── hud_layout.json
        └── pvp/               <-- 커스텀 생성한 "pvp" 프로필 저장 폴더
            ├── MyModule.json
            └── hud_layout.json
```

---

## 2. 프로필 API 활용 코드

콘피그 매니저([ModManager](../../src/main/java/silence/simsool/lucent/config/ModManager.java)) 인스턴스를 사용하여 프로필 정보를 확인하고, 변경하거나 신규 생성할 수 있습니다.

### 기존 프로필 목록 불러오기
모든 콘피그 데이터는 기본적으로 항상 `"default"` 프로필을 가지고 작동합니다.

```java
// 현재 모드에 보관 중인 모든 프로필 폴더 이름 리스트를 반환합니다.
List<String> profiles = config.getProfiles();
```

### 활성 프로필 실시간 전환하기
프로필을 전환하면 메모리 내 설정값들이 즉시 대상 프로필 파일의 JSON 속성들로 리로드되고 렌더링 화면에 업데이트가 바로 반영됩니다.

```java
// 활성 프로필 설정을 "pvp"로 스왑하여 적용합니다.
config.setCurrentProfile("pvp");
```

### 새로운 프로필 만들기
새 프로필 이름을 등록합니다. 이 명령을 수행하면 해당하는 전용 설정 디렉토리가 새롭게 디스크 상에 생성됩니다.

```java
// 새로운 프로필 디렉토리를 물리 생성합니다.
config.createProfile("pvp");
```

### 프로필 삭제하기
더 이상 사용하지 않는 커스텀 프로필 설정을 삭제하고 디스크 상에서 폴더를 제거합니다.

```java
// "pvp" 프로필 설정 폴더와 파일 데이터를 영구 삭제합니다.
config.deleteProfile("pvp");
```

### 프로필 이름 수정하기
기존 프로필의 값을 보존한 상태로 관리 폴더명을 바꿉니다.

```java
// "pvp" 프로필의 이름을 "competitive"로 변경합니다.
config.renameProfile("pvp", "competitive");
```

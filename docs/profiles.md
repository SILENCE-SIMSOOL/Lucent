# Profile Management Guide

Lucent supports named configuration profiles. When profiles are used, all active settings, layout properties, and HUD positions are isolated and saved under `config/<modid>/profiles/<profile_name>/`.

This makes it easy for players to swap between different setup layouts (e.g. "PVP", "Build Mode", "Default").

---

## 1. Profiles Directory Structure
When a player configures your mod, the configurations are stored as structured JSON profiles inside the configuration path:

```text
config/
└── yourmodid/
    └── profiles/
        ├── default/           <-- Default profile folder
        │   ├── MyModule.json
        │   └── hud_layout.json
        └── pvp/               <-- Custom profile folder
            ├── MyModule.json
            └── hud_layout.json
```

---

## 2. Profile API Usage

You can programmatically list, create, switch, delete, or rename profiles using your [ModManager](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/config/ModManager.java) instance.

### Listing Available Profiles
Every config directory contains at least the `"default"` profile.

```java
// Returns a list of all existing profile folder names
List<String> profiles = config.getProfiles();
```

### Switching Profiles
Swapping profiles instantly updates the configurations by reloading the newly selected profile's JSON properties from disk and triggering redraw updates on screens/HUDs.

```java
// Switches active settings to the "pvp" profile configuration
config.setCurrentProfile("pvp");
```

### Creating Profiles
Create a new profile name. This creates a profile entry which defaults to copy settings or initialize empty defaults.

```java
// Creates a new profile directory
config.createProfile("pvp");
```

### Deleting Profiles
Removes a custom profile directory from the filesystem.

```java
// Deletes the specified profile properties
config.deleteProfile("pvp");
```

### Renaming Profiles
Renames a profile folder, preserving all internal configuration values.

```java
// Renames "pvp" profile folder to "competitive"
config.renameProfile("pvp", "competitive");
```

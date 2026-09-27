# FUNdoBREW

### 🥛 Milk
FUNdoBREW adds Milk Bottles, Splash Milk Bottles, Lingering Milk Bottles, Milk Cauldrons, flowing Milk, and Milk Ice.

Milk and Water form Milk Ice only from a live adjacent contact. Replacing an isolated source is not a reaction. A real contact resolves without waiting for Milk's fluid delay, and updates the rest of a Milk flow when its source changes.

### 🎯 Potion impacts & hitboxes
Potion break particles and sound use the exact double-precision `HitResult` coordinate instead of `BlockPos` or the centre of the struck block. This addresses the positioning problem documented in [MC-189857](https://report.bugs.mojang.com/servicedesk/customer/portal/2/MC-189857). Milk bottle effects use the same impact coordinate.

Splash potion selection uses spheres: radius **4.0** for ordinary splash potions and **1.25** for Milk splash. Lingering cloud selection uses a cylinder based on the cloud's live bounds. The Potion hitboxes debug option renders the same server-sent sphere or cylinder at its exact coordinates.

### ✨ Echo Dust & infinite effects
Craft **2 Echo Dust** from an **Echo Shard**, then use it as a brewing ingredient with a supported potion. The potion keeps its form and amplifier, but its eligible effects no longer expire.

- Instant effects cannot become infinite.
- Already-infinite effects are rejected.
- Splash and lingering conversion is supported.

### ⚙️ Enhanced Milk Vision
**Enhanced Milk Vision** is an optional built-in resource pack and is disabled by default. It replaces the normal Milk fluid appearance with a shader-owned white surface and a world-space 16×16 pattern for Milk and Milk Cauldrons. The pack warns about Sodium incompatibility.

## Building & Launch
### Requirements
<a href="https://www.minecraft.net/"><img alt="Minecraft 26.3" src="https://img.shields.io/badge/Minecraft-26.3-62B47A?style=plastic&logo=minecraft&logoColor=white"><br>
<a href="https://fabricmc.net/use/installer/"><img alt="Fabric Loader 0.19.5 or newer" src="https://img.shields.io/badge/Fabric%20Loader-0.19.5%2B-DBD0B4?style=plastic&logo=fabric&logoColor=black"><br>
<a href="https://modrinth.com/mod/fabric-api"><img alt="Fabric API required" src="https://img.shields.io/badge/Fabric%20API-Required-DBD0B4?style=plastic&logo=fabric&logoColor=black"><br>
<a href="https://adoptium.net/temurin/releases/?version=25"><img alt="Java 25 or newer" src="https://img.shields.io/badge/Java-25%2B-E76F00?style=plastic&logo=openjdk&logoColor=white"><br>

_MidnightLib is bundled inside the release JAR._

### Building from source
```bash
./gradlew clean shadowJar
```

```powershell
gradlew.bat clean shadowJar
```

The release artifact is written to:

```text
build/libs/fundo_3-proof+263.jar
```

or can be downloaded from [Releases](https://github.com/stomarver/FUNdoBREW/releases).

## Configuration
| Option | Default | What it controls |
|---|:---:|---|
| Milk Additions | Yes | Milk Bottle Collection & Recipes; does not unregister Milk content |
| Brewing Additions | Yes | Echo Dust and infinite-effect recipes and brewing; preserves existing Echo Dust data |
| Brewing Speed Multiplier | 1× | Duration of newly started brewing operations |
| Increased Potion Stacking | Yes | Fallback stack limit of up to 16 for vanilla potion containers without `minecraft:max_stack_size` |
| Milk Bucket Pouring | Yes | Milk Bucket placement |
| Milk Bucket Drinking | Yes | Normal Milk Bucket drinking |
| Farmer's Delight integration | Yes | Farmer's Delight Milk Bottle when available |
| Action logs | Disabled | Server diagnostic logs |
| Potion hitboxes | OFF | Sphere and cylinder debug overlay |

Explicit max-stack components supplied by commands, data packs, or other mods take precedence. Server feature settings are synchronized to connected clients and refresh affected availability and recipes.

## Compatibility
FUNdoBREW works on its own and has optional integrations:
- **Farmer's Delight Refabricated** - reuses its Milk Bottle instead of registering a duplicate normal bottle;
- **JEI** - displays recipes and supported brewing paths;
- **Mod Menu** - adds an in-game entry point to the configuration screen.

Compatibility with small, single-function mods - such as Potion Stacking, Stacking Potions, The Splash Milk, and similar projects - is deliberately out of scope. FUNdoBREW already implements much of that territory, so compatibility work is aimed primarily at larger mods that are commonly used in modpacks.

Found an incompatibility? Please [open an issue](https://github.com/stomarver/FUNdoBREW/issues) and include the Minecraft version, FUNdoBREW version, a mod list, and the relevant log.

## Project status
FUNdoBREW has no dedicated testers: I am the sole developer, gameplay designer, and tester. The mod is currently in the **proof** stage - the project's direct equivalent of alpha. Its main functionality is stable, but major oversights and even critical bugs are still possible; some issues could damage worlds or disrupt progression. Please make world backups regularly.

I nevertheless try to design features so that the effects of incorrect behavior can be removed or recovered from whenever possible. Feedback and bug reports are welcome, but support, compatibility, balance, and release timing are handled on a best-effort basis.

## License
<a href="License"><img alt="Apache License 2.0" src="https://img.shields.io/badge/License-Apache--2.0-blue?style=plastic"></a>

_Third-party notices are listed in [Third Party](Third%20Party)._

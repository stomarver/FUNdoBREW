# FUNdoBREW

### 🥛 Milk brewing & cleansing
Milk is no longer limited to buckets. FUNdoBREW adds:
- **Milk Bottles**, **Splash Milk Bottles**, and **Lingering Milk Bottles**;
- **Milk Cauldrons** that fill and drain naturally;
- splash milk that cleanses on impact;
- lingering milk that leaves a temporary cleansing cloud;
- sensible bottle handling - a full cauldron will not consume another bottle.

The result is a portable, throwable way to remove effects without changing the role of ordinary milk.

### ✨ Echo Dust & infinite effects
Craft **2 Echo Dust** from an **Echo Shard**, then use it as a brewing ingredient with a supported potion. The potion keeps its form and amplifier, but its eligible effects no longer expire.

FUNdoBREW deliberately keeps this mechanic selective:
- instant effects cannot become infinite;
- already-infinite effects are rejected;
- normal splash and lingering conversion still works;
- regular brewing remains useful alongside permanent effects.

### ⚙️ Optional quality of life
The configuration also provides a few independent tools:
- stack regular, splash, and lingering potions up to **16**;
- speed up newly started brewing operations with a whole-number multiplier;
- expose potion-hitbox helpers for testing;
- write detailed potion and infinite-effect action logs for server diagnostics.

### 🏆 Advancements
Two advancements continue from vanilla's **Local Brewery**:
- **spilled it again...** - break a Splash or Lingering Milk Bottle;
- **Forever Young** - receive an effect that never fades.

## Configuration
| Option | Default | What it controls |
|---|:---:|---|
| Milk Additions | Yes | Milk bottles, Milk Cauldrons, projectiles, recipes, and cleansing clouds |
| Brewing Additions | Yes | Echo Dust, infinite potions, related recipes and commands |
| Brewing Speed Multiplier | 1× | Duration of newly started brewing operations |
| Increased Potion Stacking | Yes | Potion stack size of up to 16 |
| Farmer's Delight integration | Yes | Reuse of Farmer's Delight's Milk Bottle when available |
| Action logs | Disabled | Optional server-side diagnostic logs |
| Potion hitboxes | OFF | Debug visualization through Minecraft's debug options |

Some gameplay and compatibility changes require a restart. Logging options can be refreshed while a world is running.

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
FUNdoBREW is licensed under the [Apache License 2.0](https://github.com/stomarver/FUNdoBREW/blob/26.3/License).

_Third-party notices are listed in [Third Party](https://github.com/stomarver/FUNdoBREW/blob/26.3/Third%20Party)._

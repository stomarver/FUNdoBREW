# FUNdoBREW

**FUNdoBREW** is a small, vanilla-minded brewing expansion for Fabric.

It adds useful milk-brewing tools and a way to make eligible potion effects last indefinitely, while keeping the familiar Minecraft brewing flow. The mod is a hobby project made and maintained by one person; features and balance may evolve over time.

## Installation

FUNdoBREW currently requires:

- Minecraft **26.3**
- Fabric Loader **0.19.5** or newer
- Fabric API
- Java **25** or newer

Install the mod on the server and on every client joining it. MidnightLib is included in the release JAR. Mod Menu is recommended for easy access to settings, and JEI is recommended for recipe browsing.

## What it adds

### Milk brewing and cleansing

- Milk Bottles, Splash Milk Bottles, and Lingering Milk Bottles
- Milk Cauldrons and recipes for making the new bottle types
- Splash milk for cleansing at impact
- Lingering milk for a temporary cleansing cloud

Milk bottles can be used with cauldrons in the natural way: an empty cauldron can be filled, and a partially filled Milk Cauldron can be topped up. A full cauldron does not waste the bottle.

### Echo Dust and infinite effects

Brew **Echo Dust** with supported potions to create an infinite version of their eligible effect. The system follows ordinary brewing families, so it is easy to discover through gameplay and JEI.

Not every effect is intended to be permanent. Instant effects are excluded, and normal brewing remains useful alongside the infinite options.

### Optional quality-of-life settings

Depending on the configuration, FUNdoBREW can also:

- stack ordinary potion items up to 16;
- adjust the speed of newly started brewing operations;
- provide optional action logs for server operators;
- expose potion-hitbox debug helpers for testing.

## Configuration and compatibility

Settings are available through Mod Menu when installed, or in the generated MidnightLib configuration file. Most gameplay switches require a restart; logging options can be changed while a world is running.

FUNdoBREW works on its own. When Farmer's Delight Refabricated is present, it can use that mod's milk bottle instead of registering a duplicate normal milk bottle. JEI and Mod Menu are optional, but make recipe discovery and configuration more convenient.

## Advancements

After the vanilla **Local Brewery** advancement, FUNdoBREW adds:

- **spilled it again...** — use a Splash or Lingering Milk Bottle;
- **Forever Young** — receive an eligible infinite effect.

## About the project

This is an independent hobby mod. Feedback and bug reports are welcome, but support, compatibility, and release timing are handled on a best-effort basis. If you use it in a modpack or on a server, please test updates in your own environment first.

## Building from source

Use JDK 25 or newer:

```bash
bash ./gradlew clean shadowJar
```

The release artifact is written to:

```text
build/libs/fundo_1-proof+mc26.3.jar
```

## License
FUNdoBREW is licensed under [Apache-2.0](License). Third-party notices are available in [Third Party](Third%20Party).



_(ihateyougithub for req .md extending)_

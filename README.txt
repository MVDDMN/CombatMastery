# Combat Mastery
by Yorinu

**Combat Mastery** is a Minecraft Forge mod that expands the combat system with customizable combat attributes, special attack abilities, and custom projectile entities. It introduces a chance-based ability system that allows players and supported mobs to trigger powerful effects during combat.

The project focuses on extending Minecraft's combat mechanics through custom Java development, entity behavior, particle effects, sound effects, and configurable attribute values.

## Features

### Combat Attributes

Combat Mastery introduces custom attributes that determine the activation chance of special combat abilities.

| Attribute          | Description                                                          |
| ------------------ | -------------------------------------------------------------------- |
| Great Sweep Chance | Chance to activate the Great Sweep melee ability.                    |
| Aftershock         | Chance to trigger an additional combat effect after an attack.       |
| Chain Lightning    | Chance to trigger a chain-lightning attack.                          |
| Lightning Strike   | Chance to trigger a lightning-based attack.                          |
| Dice Roll          | Chance to activate a random combat effect.                           |
| Homing Shot        | Chance to launch homing projectiles.                                 |
| Rocket Shot        | Chance to launch rocket-like projectiles.                            |
| Tornado            | Chance to summon a moving tornado that damages and launches targets. |

Attribute values are designed around activation chances, allowing abilities to be tuned through attribute configuration and integration.

### Special Abilities

#### Great Sweep

A custom melee-based sweeping attack featuring a crescent-shaped visual effect, collision detection, and target-hit tracking.

#### Aftershock

A combat ability that adds an additional effect to an attack, extending the variety of possible combat interactions.

#### Chain Lightning

A lightning-based ability designed to extend combat effects beyond a single target.

#### Lightning Strike

A custom lightning-oriented combat ability that adds another chance-based offensive effect.

#### Dice Roll

A random-effect combat ability that introduces an element of unpredictability into combat.

#### Homing Shot

Launches multiple projectiles that acquire and pursue targets after being released. The project includes projectile sequencing, target tracking, and custom entity behavior.

#### Rocket Shot

Launches a custom rocket-like projectile that homes toward targets and derives its damage from the triggering attack.

#### Tornado

Creates a moving tornado entity with a rotating particle effect, a dedicated hitbox, damage, and an upward launch effect against affected targets. The entity also includes custom movement and sound effects.

### Mob Integration

Combat Mastery supports applying selected combat attributes to multiple mobs, including:

* Zombies
* Husk
* Drowned
* Skeletons
* Wither Skeletons
* Piglins
* Piglin Brutes
* Zombified Piglins
* Vindicators
* Iron Golems
* Wolves
* Polar Bears
* Zoglins

This allows supported mobs to participate in the custom combat ability system instead of limiting these mechanics exclusively to players.

### Custom Entities and Effects

The project includes custom entities, renderers, particles, and sounds to support its abilities. These components are implemented in Java and registered through the Forge mod lifecycle.

## Technical Details

| Component               | Details          |
| ----------------------- | ---------------- |
| Minecraft Version       | 1.20.1           |
| Mod Loader              | Minecraft Forge  |
| Language                | Java             |
| Development Environment | IntelliJ IDEA    |
| Build System            | Gradle           |
| Mod ID                  | `combat_mastery` |

## Project Structure

The Java source code is organized by functionality.

```text
src/main/java/com/combatmastery/
├── attribute/
│   └── ModAttributes.java
├── client/
│   ├── CombatMasteryClient.java
│   └── renderer/
├── combat/
│   └── Combat ability handlers
├── entity/
│   ├── ModEntities.java
│   └── Custom projectile entities
├── particle/
│   └── ModParticles.java
├── registry/
└── sound/
    └── ModSounds.java
```

The project also contains Minecraft resource files under `src/main/resources`, including entity textures, particle definitions, sounds, and mod metadata.

## Building From Source

### Requirements

* Java 17
* Minecraft Forge 1.20.1 development environment
* Gradle wrapper included in the repository
* IntelliJ IDEA or another compatible Java IDE

### Build Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/MVDDMN/CombatMastery.git
   ```

2. Open the project in IntelliJ IDEA.

3. Allow Gradle to import and synchronize the project.

4. Run the following command from the project root:

   **Windows:**

   ```bat
   .\gradlew.bat clean build
   ```

   **Linux/macOS:**

   ```bash
   ./gradlew clean build
   ```

5. Once the build completes, locate the generated mod JAR in:

   ```text
   build/libs/
   ```

The exact output filename depends on the project's Gradle configuration.

## Installation

1. Install Minecraft Forge for version 1.20.1.
2. Build the mod or download a compatible release.
3. Place the generated `CombatMastery` JAR in your Minecraft `mods` folder.
4. Launch Minecraft using the Forge profile.

Any additional dependencies required by the final build must also be installed.

## Development Goals

Combat Mastery is intended to expand Minecraft's combat possibilities through:

* Custom combat attributes and chance-based activation.
* New offensive abilities and projectile mechanics.
* Custom entity movement, targeting, and collision behavior.
* Distinct visual and audio feedback.
* Support for extending combat abilities to compatible mobs.

## Contributing

Suggestions, bug reports, and improvements are welcome. If you encounter an issue, include the Minecraft version, Forge version, relevant logs, and steps to reproduce the problem.

## License

No license has been specified yet. Unless a license is added to this repository, the project should not be assumed to permit redistribution, modification, or reuse.

---

**Combat Mastery** — Expanding Minecraft combat through custom attributes, abilities, and entity mechanics.

---

## Copyright and All Rights Reserved

Copyright © 2026 Karl Angelo P. Dela Cruz. All rights reserved.

The source code, original assets, custom entities, combat abilities, textures, particle effects, sounds, and other original materials contained in this repository are the intellectual property of the author, except for third-party materials that remain subject to their respective owners' licenses.

**No license is granted to use, copy, modify, distribute, sublicense, or redistribute this project's original materials without prior written permission from the copyright holder.** Republishing this project, in whole or in part, or presenting its original work as your own is prohibited without authorization.

This project is intended for personal and educational use by its author. Public availability of this repository does not constitute permission to reuse or redistribute its contents.

Minecraft and Minecraft Forge are trademarks or projects belonging to their respective owners. This project is an independent modification and is not affiliated with, endorsed by, or sponsored by Mojang Studios or Microsoft.

For permission to use, modify, or distribute this project, please contact the copyright holder through the contact information provided in this repository.

**All rights reserved.**


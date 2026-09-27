# Interoperability tag audit — FUNdoBREW 3-proof / Minecraft 26.3

**Audit scope:** 2026-09-27. I reviewed FUNdoBREW's actual semantic outputs
(Milk fluid, a drinkable Milk Bottle, and Echo Dust) against Fabric API's
`fabric-convention-tags-v2` 4.10.3 definitions and representative content/
automation mods. The result deliberately adds only tags that describe a real
interchangeable property. It does not claim that Milk Ice is ordinary ice,
that throwable Milk Bottles are ordinary potion bottles, or that Echo Dust can
substitute for arbitrary processing dust.

## Added tags

| Registry | Tag | FUNdoBREW entries | Reason / safety boundary |
|---|---|---|---|
| fluid | `c:milk` | `fundo:milk`, `fundo:flowing_milk` | Standard Milk-fluid identity. Both states are included because Minecraft registers still and flowing fluids separately. This enables tag-based fluid recipes/transport compatibility. |
| item | `c:drinks/milk` | `fundo:milk_bottle` | It is a drink-action, hunger-independent Milk consumable. The Fabric convention explicitly assigns this semantic category to milk drinks. |
| item | `c:drink_containing/bottle` | `fundo:milk_bottle` | It is a non-empty, drinkable bottle. This lets container-aware recipes recognise it without falsely treating it as a potion. |
| item | `c:dusts/echo` | `fundo:echo_dust` | A precise material-form tag for datapacks or future integrations that intentionally accept Echo Dust. **It is not added to broad `c:dusts`**, so generic dust recipes cannot silently consume a gameplay-specific brewing reagent. |

All JSON uses `"replace": false`; datapacks and other mods retain ownership of
the aggregate tags. The entries remain present independently of feature gates,
matching the stable-registry / existing-world policy.

## Explicitly not added

- `c:foods` / `c:foods/milk`: the bottle has no food component. Modern
  Farmer's Delight convention places milk bottles in `c:drinks/milk`, not food
  tags.
- `c:potions/bottle`: the normal Milk Bottle is not a potion container, and
  the splash/lingering Milk Bottles are projectiles rather than drinkable
  bottles.
- broad `c:dusts`: Echo Dust is not a fungible industrial dust.
- any ice tag: Fabric's current conventional-tag catalog supplies no generic
  ice category, and Milk Ice has purposeful Milk-fluid semantics.

## Representative-mod review

| Mod / ecosystem | What was audited | Result |
|---|---|---|
| **Fabric API conventional tags v2** | Current `c:milk`, `c:drinks/milk`, `c:drink_containing/bottle`, and dust hierarchy. | The added names exist in the shipped convention catalog. `c:milk` is intentionally a fluid tag; drink and bottle tags are item tags. |
| **Farmer's Delight (Refabricated 26.3-3.6.27)** | Its bundled tag data. | The JAR directly places `farmersdelight:milk_bottle` in `c:drinks/milk`. FUNdoBREW now expresses the same conventional identity for its native bottle; the selected FD bottle keeps its owner-provided tag. |
| **Create (Fabric ecosystem)** | Published common-tag catalogue / Milk fluid compatibility. | `c:milk` is the shared fluid contract used for Create's Milk states; no Create-specific tag is needed. |
| **Modern Industrialization** | Fluid-transport and recipe-driven automation boundary. | Generic fluid handling needs the standard Milk identity, not a hard dependency or MI-only ID. `c:milk` is the conservative bridge. |
| **Applied Energistics 2** | Fluid storage/pattern ecosystem. | It transports registered fluids; a standard Milk tag lets datapacks/pattern providers state Milk intent without coupling to a concrete fluid ID. No AE2 tag is claimed. |
| **Botania** | Milk behaviour / bucket interactions. | It operates on conventional/vanilla milk behaviour and entity actions, not a stable custom Milk-fluid tag. No Botania-specific tag is safe or required. |
| **Tech Reborn** | Material/dust processing convention. | `c:dusts/echo` is supplied as a narrow opt-in material key only. Broad dust membership was rejected to prevent arbitrary machine recipes from consuming Echo Dust. |
| **Spectrum** | Magic-resource classification. | No published shared equivalent for a cleansing Milk fluid or Echo reagent was found; the universal Milk and narrow Echo material tags are the safe extent. |

The audit therefore favours cross-loader `c:` conventions and intentionally
avoids mod-private tags. Modpacks may add recipe-level integrations on top of
these stable identities without requiring an API dependency.

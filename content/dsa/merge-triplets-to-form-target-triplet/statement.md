# Merge Triplets to Form Target Triplet

A lighting desk stores presets, each a triplet `[red, green, blue]` of channel levels. Overlaying presets lights every channel at the highest level any of them gives it: overlaying `[50,10,80]` and `[20,60,30]` gives `[50,60,80]`. Any number of presets can be overlaid, and a single preset on its own counts as an overlay of one.

Implement `Lights.canMix(int[][] presets, int[] target)` in `Lights.java` and return `true` if overlaying some of the presets gives exactly the triplet `target`, and `false` otherwise.

## Example 1

**Input:** `presets = [[40,10,90],[60,20,30],[50,80,10],[20,30,70]]`, `target = [60,30,90]`
**Output:** `true`
**Explanation:** Overlaying presets 0, 1 and 3 gives `[60,30,90]`. Preset 2 cannot be in the mix: its green level of 80 is above the target's 30, and overlaying never lowers a level.

## Example 2

**Input:** `presets = [[15,40,25],[35,10,50],[30,20,45]]`, `target = [35,40,25]`
**Output:** `false`
**Explanation:** Only preset 1 has the red level 35, and it brings a blue level of 50, above the target's 25. Without it, red never gets past 30.

## Example 3

**Input:** `presets = [[3,8,8]]`, `target = [3,8,9]`
**Output:** `false`
**Explanation:** With one preset the only overlay is the preset itself, and its blue level is 8, not 9.

## Constraints

- `1 <= presets.length <= 10^5`
- `presets[i].length == 3`
- `target.length == 3`
- `1 <= presets[i][j] <= 1000`
- `1 <= target[j] <= 1000`

## Follow-up

What if you also had to say which presets to overlay?

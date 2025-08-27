Unfinished data-driven loot crate mod. Probably contains enormous amount of bugs. This is my first mod that is bigger than few lines so for the most time I had no idea what I am doing

What is missing/was planned: (from most to least possible to be done some day)
1. Data-driven rarities
2. Logging with log levels
3. Texture/Model syncing and baking them not in the main rendering pipeline so every crate can have specific model
4. Use Text.translatable and add language support to crate names
5. Finished reward screen (CS-like maybe)
6. Code-wise improvements (structurization, optimization, etc)
7. In-game editor

///

Requirements: 
Minecraft 1.20.1 with Fabric installed
OwoLib
Note that fabric loader will probably will not give you any warnings if you forgot something because I have not set up dependencies

///

Quick guide: 

To add crate create a folder named "lootcrates" like on the screenshot
<img width="260" height="197" alt="image" src="https://github.com/user-attachments/assets/b355db35-3e1f-4f81-af1a-8d6aad550d68" />
Add a .json file. Name does not matter.

Here you can enter fields that later will be parsed to create a lootcrate in game

Heres json file that _probably_ contains all currently implemented fields. More about some field after the example

```
{
    "itemProperties": {
        "id": "1",
        "name": "Rare Loot Crate",
        "glowing": true,
        "rarity": "UNCOMMON"
    },
    "screenProperties": {
		
        "horizontalGUISize": 250,
        "spinContainerVerticalSize": 70,
        "itemsContainerVerticalSize": 50,
        "buttonContainerVerticalSize": 35,
		
        "displayGrid": {
            "rows": 30,
            "columns": 6,
            "elementMargin": 2,
            "elementSize": 30,
            "scaleOnHover": true,
            "scaleOnHoverMultiplier": 1.2,
            "showStackTooltip": true,
            "showStackCount": true,
            "sorting": "ASC",
            "outlineColor": {
                "hex": "FFFFFFFF"
            } 
        },
				
        "spinGrid": {
			"excludedEasings": ["IN_OUT_SINE", "IN_OUT_CUBIC", "IN_OUT_QUINT", "IN_OUT_CIRC", "IN_OUT_QUAD", "IN_OUT_QUART", "OUT_SINE", "OUT_QUAD", "OUT_QUART", "OUT_BACK"],
            "columns": 45,
            "elementSize": 30,
            "animationDuration": 10000,
			
			"scaleOnHover": true,
			"scaleOnHoverMultiplier": 1.2,
            "scaleWhileRolling": false,
            "showStackCount": true,
            "showStackTooltip": false,
            "slotChangeSound": "minecraft:entity.experience_orb.pickup"
        }
    },
	
    "drops": [
        {
            "id": "minecraft:stone",
            "count": 64,
            "rarity": "COMMON",
            "weight": 1
        },
        {
            "id": "minecraft:stone",
            "count": 64,
            "rarity": "MYTHIC",
            "weight": 1
        },
        {
            "id": "minecraft:diamond",
            "count": 34,
            "rarity": "RARE",
            "weight": 1
        },
        {
            "id": "minecraft:stone",
            "count": 64,
            "rarity": "RARE",
            "weight": 1
        },
		        {
            "id": "minecraft:stone",
            "count": 32,
            "rarity": "MYTHIC",
            "weight": 1
        }
    ]
}
```
^
also technically "color" is parseable in itemProps but it does nothing

id - What "caseId" NBT will crate item have. Should be unique
name - displayed name
glowing - makes item glow like enchanted
rarity - changes item name color. (Vanilla) (Possible values: "COMMON", "UNCOMMON", "RARE", "EPIC")

sorting - sorts displayed items (Possible values: "ASC", "ASCENDING, "DESC", "DESCENDING", "NONE")

outlineColor - could be in hex as in the example or in rgba like (every letter is capital, but "hex" is small - yes.)
```
"outlineColor": {
    "A":10,
    "R":20,
    "G":30,
    "B":40
} 
```
excludedEasings - already excludes all possible easings in the example. If every easing function is excluded it uses IN_OUT_SINE
slotChangeSound - self explanatory. Basically /playsound command argument

drop element:
rarity - not vanilla. Uses this enumerable. Values used for sorting. 
BLANK(Color.ofArgb(00000000), 0),
COMMON(Color.ofFormatting(Formatting.GRAY), 100),
UNCOMMON(Color.ofFormatting(Formatting.GREEN), 200),
RARE(Color.ofFormatting(Formatting.BLUE), 300),
EPIC(Color.ofFormatting(Formatting.LIGHT_PURPLE), 400),
LEGENDARY(Color.ofFormatting(Formatting.GOLD), 500),
MYTHIC(Color.ofFormatting(Formatting.RED), 600);

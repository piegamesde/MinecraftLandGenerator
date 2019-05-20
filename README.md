# Minecraft Land Generator version 3.1.0

[![](https://jitpack.io/v/Minecraft-Technik-Wiki/MinecraftLandGenerator.svg)](https://jitpack.io/#Minecraft-Technik-Wiki/MinecraftLandGenerator)

Updated May 2019

## Credits

- Original Code by Corrodias — *November 2010*
- Enhanced Code by Morlok8k — *Feb. 2011 - Jan. 2015*
- Additional Code by pr0f1x — *October 2011*
- Additional Code/Idea by jaseg — *August 2012*
- Additional Code by Gallion — *January 2015*
- Almost complete rewrite by piegames and sommerlile — *November 2018*

This is a fork of <https://github.com/Morlok8k/MinecraftLandGenerator>.

## How it works

The tool leverages Minecraft server functionality to generate chunks in a given world. To do this, it changes or fakes some data in that world to make the server behave as intended. This means on the one hand that a `server.jar` is required. On the other hand, this makes MinecraftLandGenerator compatible with almost all Minecraft versions. You can either specify a path to a `server.jar` you downloaded or just a version name. In the latter case, MinecraftLandGenerator will download it automatically and cache it in `$XDG_CACHE_HOME/minecraftlandgenerator`. If no version is specified, the latest release will be used.

There are different operation modes that use different techniques to generate the chunks:

### Spawnpoint-based generation

Minecraft has the useful property, that an area of 25×25 chunks (value changed a bit over the course of versions) around the world's spawn is always loaded, and generated if necessary. By moving the spawnpoint accordingly and repeatedly starting the server on that world, it can be used to generate a desired area of the world. In the `manual-spawnpoints` mode, specify a list of spawn coordinates and the server will be started on each one, generating the surrounding area. In the `auto-spawnpoints` mode, specify a region you want to have generated and the minimal required set of spawnpoints will be calculated automatically.

### Chunk-based generation

Since a few versions, Minecraft has the concept of force-loaded chunks. These are chunk that are always loaded by the server. While this was originally meant to be used for farms or other regions of interest, this too has the property that missing chunks will be generated. The `auto-spawnpoints` uses this by writing a list of chunks to be force-loaded into the world and then starting the server.

Since more chunks can be generated per server start (tens of thousands instead of 25×25=625), this is significantly faster. On the other hand, this requires much more RAM. If you are running out of memory, or the system starts swapping and slowing down, reduce the maximal number of force-loaded chunks per server start.

Other advantages of chunk-based control over generation are that 1) it allows to be lazy: If enabled, the world will be scanned for existing chunks first and those will be skipped, making it significantly faster if parts of the world already exist. 2) It allows to generate data in all dimensions, not only the overworld.

### Backup files

The program makes a backup of level.dat and other files it manipulates before editing, and restores the backup at the end. In the event that a backup file already exists, the program will refuse to proceed, leaving the user to determine why it ile exists and whether they would rather restore it or delete it, which must be done manually.

## Command-line usage

**Download the latest release [here](https://github.com/Minecraft-Technik-Wiki/MinecraftLandGenerator/releases).**

To show the global options and the available subcommands, use

	java -jar MinecraftLandGenerator.jar help

Currently there are three subcommands acting as operation modes, `auto-spawnpoints`, `manual-spawnpoints` and `forceload-chunks`

	java -jar MinecraftLandGenerator.jar help auto-spawnpoints
	java -jar MinecraftLandGenerator.jar help manual-spawnpoints
	java -jar MinecraftLandGenerator.jar help forceload-chunks

will tell you more about their specific options. On newer versions, `forceload-chunks` is the recommended way to use.

## Library usage

If you know JitPack and use gradle:

	dependencies {
		implementation 'com.github.Minecraft-Technik-Wiki:MinecraftLandGenerator:3.1.0'
	}

otherwise, grab the latest release [here](https://jitpack.io/#Minecraft-Technik-Wiki/MinecraftLandGenerator).

The class `MinecraftLandGenerator` contains the command-line functionality, which may be used from code as well. It also provides the `manualSpawnpoints` and `forceloadChunks` methods that are used by the command-line internally for direct use. This is all you are going to need almost every time, otherwise have a look at the documented code (it really is not that much).

The class `World` has a few static utility methods too, `generateSpawnpoints` will create a list of spawnpoints exactly covering a specific area and `availableChunks` will list all existing chunks in a world.

## Changelog

3.1.0
- Minecraft crashes are recognized.
- If interrupted, things will be cleaned up before terminating.

3.0.0
- Major version bump due to backwards-incompatible changes
- Fixed a bug that would delete part of the world (Little remainder to always make backups)
- Changed how the server folder is handled.
	- Now, the path to the world may be absolute.
- Server jar files can be downloaded automatically now.

2.0.0 (piegames/sommerlilie)
- Complete rewrite of the core logic
- New command-line interface
- Removed compatibility for older Minecraft versions
- Faster world generation using force-loading of chunks (optional)
- Removed a lot of bloat and unused stuff

1.7.6 (Morlok8k)
- Gallion: fixed null world name (minor bug)
- Morlok8k: fixed elua bug

1.7.5
- Added "save-all" to alternate mode
- Added fix for new style of java error messages

1.7.4
- Released Minecraft land Generator under the WTFPL.  (With the permission of Corrodias)

1.7.3
- Fixed a minor display bug (specifically when using Server Generation Fix Mod)
- Updated Readme text a bit.

1.7.2
- Fixed "1152 bug"
- Updated to JNBT 1.3
- adjusted archive integrity check to account for timezone-related bugs...

1.7.1
- Major Code Refactoring
- Updated to JNBT 1.2
- making code ready for a GUI

1.7.0
- Major Code Optimization
- Drastically reduced the amount of time it takes for MLG to expand a world after it has already done so before!
  (To do this, I rewrote the Main loop of the program, and add my own Coordinate object)
- Added Resume Functionality
- Updated Time Output yet again.
- Made xx% output nicer by rewriting previous existing line.
- Misc. Tweaks
- Misc. Additions

1.6.3
- Minor Code Optimization
- Finely got on the ball and added the JNBT source and everything (as an internal .zip) to be completely faithful to his license
- Also adding script files internally in the .jar for archive (or offline) purposes. (Manual Extract needed for use)
- Modified output of MLG slightly to show whats the server and whats MLG. (I may do more with this later.)

1.6.2
- Major Code Optimization
- Updated Time Output again.  Now says "1 Minute" instead of "1 Minutes".
- Updated Location Code - the center of the square is now truely centered, and it trys to get as close to the given size as possible.
- Added "-nowait" and its shorter version "-n"
- Added currently non-functional RCON code.  Will try to make functional in the future.

1.6.11
- Removed End-of-Generation ASCII-Graphic - It didn't really fit with MLG.
- Updated Time Output.
- Changed estimated time remaining to count all runs, not just the last four.
- Added the time it took to complete at the end of generation.

1.6.1
- Added some modifications for scripting  (Mainly for a new Initial setup script)
- Changed MLG's Y to Z.  Now it matches Minecraft.  Y in the game is Height.
- Renamed -y switch to -z.  MLG will remain backwards compatible if you use the old -y switch.
- Updated -printspawn to show X,Y,Z
- Added End-of-Generation ASCII-Graphic
- Slightly altered some text output

1.6.05
- MLG displays if the server is converting the Map format, when not in verbose mode. (McRegion -> Anvil, or Chunk-File -> McRegion)
- Minor fixes/edits/typos
- Added link to new MLG website to readme file
1.6.03
- added decoding of escape characters of URL's (so a space is a " " and not "%20")
- added "-downloadlist [list]" where [list] is a text file with URL's on each line

1.6.02
- small fix on caculating md5sum where old version didnt pad out to 32chars with zeros on the left side- quick Archive intergity fix after injecting source code into .jar after it compiled.- no new functionality, md5 issue doesnt affect -update on old versions.

1.6.0
- NOW DOES NOT NEED ANY SCRIPT FILES!
- Added the ability to download files from the internet
- Added a switch to download any file off the internet, if needed (useless for most people, but included it in case I wanted it in the future.)
- Added the ability to check what version the .jar is. (Using MD5 hashes, timestamps, and the BuildID file)
- Added "-update" to download new versions of MLG directly from github.
- Updated estimated time.  Now shows up on loop 2+ instead of loop 4+.
- Standard % output of the Server should look nicer now.
- Code Refactoring
- Code Formatting
- Code Optimization
- Duplicate sections of code have been turned into Methods/"Functions"

1.5.1
- pr0f1x: Added the "save-all" command to be sent to the server before shutting it down.
- pr0f1x: Added a 40 second wait before shutting down.
- Morlok8k: Made 40 second wait optional.
- Morlok8k: Changed the Dimensions code.  (I had assumed it would be DIM-1, DIM-2, etc.  but it turned out to be DIM-1 and DIM1. Change reflects Server output of "Level n")
- Morlok8k: Config file is automatically updated to reflect these changes.
- Morlok8k: Cleaned up code.

1.5.0
- Supports Server Beta 1.6.4 (& hopefully future versions as well, while remaining backward compatible.)
- Added "-a","-alt" to use alternate method (a slightly simplier version of 1.3.0's code - pure verbose only)
- Added world specific output for 9 dimensions (DIM-1 is the Nether, DIM-2 through DIM-9 dont exist yet, but if and when they do, you can configure it's text).  ("Level 0", the default world, is displayed as the worlds name)
- Updated Config File for these Dimensions.
- Reads and outputs the Seed to the output. (If you had used text for the Seed, Minecraft converts it into a number. This outputs the number.)
- Changed the default 300 blocks to 380.  The server now makes a 400x400 square block terrain instead of 320x320.  Thus it is faster because there are less loops.  To use the old way, use "-i300"
- Added total Percentage done (technically, it displays the % done once the server finishes...)
- Added debugging output vars of conf file (disabled - need to re-compile source to activate)
- (the goal is to have MLG be configureable, so it can work on any version of the server, past or present.)

1.4.4
- Added ablilty to ignore [WARNING] and [SEVERE] errors with "-w"

1.4.3
- Fixed "-ps","-printspawn" as I had forgot I had broken it in 1.4.0 - due to config file change.

1.4.2
- No New Features
- Changed non-verbose mode to display server progress on the same line, saving a lot of space.
	- This couldn't wait for 1.5.0 ...  I (Morlok8k) liked it too much.

1.4.0
- Future Proofing
- Configurble Server Message reading. (If server updates and breaks MLG, you can add the new text!)
- Updated config file, and auto updating from old format.
- Added % of spawn area to non-verbose output.
- Removed datetime stamps from server output in verbose mode
- Other Misc fixes.

1.3.0
- Fixed Problems with Minecraft Beta 1.3 -- Morlok8k

1.2.0 (Corrodias)
- land generation now centers on the spawn point instead of [0, 0]
- the server is launched once before the spawn point is changed, to verify that it can run and to create a world if one doesn't exist
- added -printspawn [-ps] switch to print the current spawn coordinates to the console
- added -x and -y switches to override the X and Y offsets
- added -v switch, does the same as -verbose
- improved status message spacing to make things easier to read
- improved time estimation algorithm: it now averages the last 3 launches

1.1.0
- added MinecraftLandGenerator.conf file to hold the java command line and the server path
- added -conf solo switch to generate a .conf file
- added -verbose switch to output server output to the console (default is to ignore it)
- added -i switch to allow customizing the block increment size (default is 300)
- added instructions output in this version, i think
- improved status message output to include current iteration and total iterations

1.0.0
- initial release

# ✨ CasieBounce ✨
<a href="https://www.spigotmc.org/resources/.90967/"><img src="https://img.shields.io/spiget/downloads/90967?label=DOWNLOAD&amp;plastic" alt="Spiget Downloads" /></a> <a href="https://github.com/CasieBarieDev/CasieBounce"><img src="https://img.shields.io/github/license/casiebariedev/CasieBounce?label=GITHUB&amp;logo=github&amp;plastic" alt="GitHub" /></a> <img src="https://img.shields.io/spiget/version/90967?label=%20&amp;plastic" alt="Spiget Version" /><img src="https://img.shields.io/spiget/stars/90967?label=%20&amp;plastic" alt="Spiget Stars" /></p>

<br/>

## Patch Notes:
### [v6.2](https://www.spigotmc.org/resources/.90967/update?update=447292)
- **Added** | Able to play custom sounds. Add `CUSTOM:` to the front and specify the sound the same way as in the /playsound command.
- **Added** | Able to run a command as a prize. The console will not be spammed with the output of plugin commands. Vanilla commands can still spam the console!
- **Added** | Permission register - Permission plugins will recognize the `CB.bounce.<REGION>` permission for each region.
- **Fix** | `/cb GetRegionSettings` not showing correct data.
- **Fix** | Another metrics bug 😉
- **Other** | Switched to maven!

### [v6.1](https://www.spigotmc.org/resources/.90967/update?update=442740)
- **Added** | Prizes per bounces.
- **Added** | '/cb ResetData' to delete players/regions from the database.
- **Improved** | Info messages.
    - Fix | 1.8 white messages.
- **Fix** | Updatechecker showing there is a update while u are using the most recent version.

### [v6.0](https://www.spigotmc.org/resources/.90967/update?update=439804)
- **Added** | 1.8+ Support!
- **Added** | Support for WorldGuard 6.
- **Added** | Placeholders using [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/):
    - cb_player_\<mode\>
    - cb_totalregion_\<region\>_\<mode\>
    - cb_playerregion_\<region\>_\<mode\>  
      **\<mode\>** - Can be 'NUMBER-FULL' or 'NUMBER-ROUNDED'
    - cb_leaderboard_\<position\>_\<mode\>
    - cb_leaderboardregion_\<region\>_\<position\>_\<mode\>  
      **\<mode\>** - Can be 'NAME', 'NUMBER-FULL', 'NUMBER-ROUNDED', 'BOTH-FULL', 'BOTH-ROUNDED'
- **Added** | Info message for Placeholders.

### [v5.5](https://www.spigotmc.org/resources/.90967/update?update=433898)
- **Added** | 1.18 Support.
- **Added** | 'CB.bounce.\<region\>' permission.
- **Added** | Region name in '/cb GetRegionSettings' message.
- **Fix** | 'RequirePermission' not working properly for 1.13+.
- **Fix** | Config error when BounceSound was set to 'NONE'.

### [v5.4](https://www.spigotmc.org/resources/.90967/update?update=430072)
- **Other** | Rewrote the info messages.
- **Other** | Metrics changes:
    - Added: | 'Amount of Bounce regions' chart.
    - Added: | 'Bounce Blocks' chart.
    - Fix: | 'Bounces' will now show the amount of bounces since the last refresh.
- **Fix** | Info TabComplete in console disabled.
- **Other** | '/cb Info WorldGuardFlags' to /cb Info WorldGuard'

### [v5.3](https://www.spigotmc.org/resources/.90967/update?update=429391)
- **Added** | Metrics using bStats.
  > https://bstats.org/plugin/bukkit/CasieBounce/13216

### [v5.2](https://www.spigotmc.org/resources/.90967/update?update=427806)
- **Fix** | Bouncing outside of WorldGuard region.

### [v5.1](https://www.spigotmc.org/resources/.90967/update?update=427752)
- **Fix** | FallDamage not working properly.
- **Fix** | BounceSound not worling properly.
- **Fix** | Some messages showing incorrect information.
- **Improved** | UpdateChecker.
- **Other** | FallDamage to boolean.
- **Removed** | 'REDUCED' function from FallDamage.

### [v5.0](https://www.spigotmc.org/resources/.90967/update?update=425347)
- **Added** | Custom WorldGuard Flags (1.13+).
  ```
  - cb-bounceforce
  - cb-deathmessage
  - cb-falldamage
  - cb-bouncesound
  - cb-bounceblocks
  - cb-stopewhencrouch
  - cb-requirepermission
  - cb-isblockblacklist
  ```
- **Added** | /cb info WorldGuardFlags.
- **Added** | /cb GetRegionSetting.
- **Added** | Fancy updatechecker.
- **Added** | Updates and Errors will be announced to players with CB.admin permission when they join
- **Other** | Info changes.
- **Other** | Config changes.
- **Removed** | WorldGuard 6 support.

### [v4.2](https://www.spigotmc.org/resources/.90967/update?update=416270)
- **Added** | Tabcomplete hider, the tabcomplete will no longer work for players without the permission for that command.
- **Other** | Code Cleanup.

### [v4.1](https://www.spigotmc.org/resources/.90967/update?update=411618)
- **Fix** | 'FallDamage' in default config was still a boolean.
- **Fix** | Config error message.

### [v4.0](https://www.spigotmc.org/resources/.90967/update?update=411565)
- **Added** | 1.17 Support
- **Added** | Falldamage types - You can set the falldamage to 'DISABLED', 'ENABLED' or 'REDUCED'.
- **Added** | Config checker
    - Disables bouncing when there is a config error.
    - Logs the error in the console.
    - Sends a warning to players with 'CB.admin' when they join.
- **Added** | Command to get the error: /cb geterror
- **Fix** | FallDamage not working properly

### [v3.3](https://www.spigotmc.org/resources/.90967/update?update=406000)
- **Fix** | Config typo's.
- **Other** | Code cleanup.

### [v3.2](https://www.spigotmc.org/resources/.90967/update?update=403456)
- **Added** | Able to play a sound when you bounce.
- **Fix** | Deathmessage not working properly.
- **Fix** | When Deathmessage is set to '' the deathmessage will now be the default deathmessage of Minecraft.

### [v3.1](https://www.spigotmc.org/resources/.90967/update?update=401841)
- **Added** | Able to toggle falldamage.
- **Added** | Custom death message.
- **Fix** | Cancel falldamage not working properly.
- **Fix** | Console spam when worldguard is not installed.

### [v3.0](https://www.spigotmc.org/resources/.90967/update?update=399693)
- **Added** | 1.13+ support!
- **Fix** | Bounceblocks not updating when '/cb reloadconfig' was used.
- **Fix** | When WorldGuard was not installed and 'IsRegionBlacklist' was 'true' you didn't bounce.

### [v2.0](https://www.spigotmc.org/resources/.90967/update?update=397430)
- **Added** | All block support.
- **Added** | Region blacklist.
- **Added** | Block blacklist.
- **Fix** | Region list not working properly.

### [v1.1](https://www.spigotmc.org/resources/.90967/update?update=396598)
- **Added** | UpdateChecker.
- **Added** | GLOBAL region. (makes it work everywhere on the server)
- **Removed** | WorldGuard Requirement. (The plugin detects when WorldGuard is installed and will use it)
- **Fix** | Reload command.
- **Fix** | Config typo.
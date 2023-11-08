# Language Library

## What is its use ?
**It allows each player to independently choose the language they want the plugin to use for messages and displays.**

## How to use it ?
### First of all, you need to integrate it into your plugin. You can do this using:

#### Maven:
Repository:
```xml
<repositories>
    <repository>
        <id>mrcubee-minecraft</id>
        <url>http://nexus.mrcubee.net/repository/minecraft/</url>
    </repository>
</repositories>
```
Dependency:
```xml
<dependencies>  
  <dependency>
    <groupId>fr.mrcubee.minecraft.library</groupId>  
    <artifactId>languagelib</artifactId>  
    <version>1.0</version>  
    <scope>compile</scope>  
  </dependency>
 </dependencies>
```
#### Gradle:
Repository:
```groovy
repositories {
    maven {
        url "http://nexus.mrcubee.net/repository/minecraft/"
    }
}
```
Dependency:
```groovy
dependencies {
    compile 'fr.mrcubee.minecraft.library:languagelib:1.0'
}
```
### Use in the plugin.
#### It is advisable to define a default language when activating your plugin.
```java
@Override  
public void onEnable() {  
    Lang.setDefaultLang("EN_us");  
}
```
*Please respect the minecraft language filename format.*
#### To retrieve a message in the default language, you can do like this.
```java
String message = Lang.getMessage("display.player.name", "&2Your name is: %s", true, player.getName());
```
Parameters:

message.id
> Is the unique message identifier format to extract from lang files.

&2Your name is: %s
> Is the fallback message if the default language file does not contain the unique message identifier.

true
> Indicates whether you want the function to apply color formatting.

player.getName()
> Is an element required by the message format. the **%s** will be replaced by the player's username.
> you can see the different parameters available in the message format [here](https://www.javatpoint.com/java-string-format)
#### To retrieve a message in the player's language, you can do like this.
```java
String message = Lang.getMessage(player, "display.player.name", "&2Your name is: %s", true, player.getName());
```
Parameters:

player
> The player whose language you want to use is configured on him. If no language is set, it will use the default language. If no default language is set, it will use the fallback format message ("&2Your name is: %s").


message.id
> Is the unique message identifier format to extract from lang files.

&2Your name is: %s
> Is the fallback message if the default language file does not contain the unique message identifier.

true
> Indicates whether you want the function to apply color formatting.

player.getName()
> Is an element required by the message format. the **%s** will be replaced by the player's username.
> you can see the different parameters available in the message format [here](https://www.javatpoint.com/java-string-format)
#### Define a language for a player.

```java
Lang.setPlayerLang(player, "FR_fr");
```
Parameters:

player
> Player on which you want to apply the language.

"FR_fr"
>Language you want to apply.
>**Please respect the minecraft language filename format.**

### Create the language files.
The language files are located in a lang folder at the root of the plugin jar file and / or in a lang folder in the plugin folder. The file extension is .lang
The external language file takes precedence over the internal language file.

#### Example language file.
```properties
############################ Scoreboard ############################  

scoreboard.gameStatus.title=&fGame Status:  
scoreboard.gameStatus.waiting=&aWaiting...  
scoreboard.gameStatus.start=&6Start in &c%s  
scoreboard.gameStatus.during=&7%s  
scoreboard.gameStatus.restart=&6Restart in &c%s  

scoreboard.rank=&fRank: &7%d#  

scoreboard.players.waiting.notEnough=&fPlayers: &c%d&7/&a%d  
scoreboard.players.waiting.enough=&fPlayers: &a%d&7/&a%d  
scoreboard.players.during=&fPlayers: &a%d  

scoreboard.kit=&fKit: &7%s  

scoreboard.worldBorder=&fBorder: &9&o+%d -%d  

scoreboard.serverIp=&e%s  

############################### Step ###############################  

step.pvpStep.broadcast=PvP will be active in &c%s second%s  
step.pvpStep.done.broadcast=PvP is enabled !  
step.pvpStep.scoreboard=&7PvP in &c%s  

step.feastStep.broadcast=Feast in &c%s  
step.feastStep.scoreboard=&cFeast in &6%s  

############################ Broadcast #############################  

broadcast.prefix=&6[&cSurvivalGames&6]  
broadcast.starting=The game starts in &c%s second%s  
broadcast.restarting=Restart the server in &c%s second%s  
broadcast.player.death=&c%s(&7%s&c) &6is Dead ! There are &c%d players left.  
broadcast.player.lastDeath=&c%s(&7%s&c) &6is Dead !  
broadcast.player.win=&c%s &6WIN THE GAME !!!  

################################ Kit ###############################  

kit.spectator.name=Spectator  
kit.noKit.name=No kit
```
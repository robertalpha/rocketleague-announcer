# Configuration
The RocketLeague Annoucner is configured via environment variables.

## MQTT

TODO

## Discord

### Bot Token
To connect to Discord, you need to create a Bot. 
 * Go to https://discord.com/developers/applications and create a new application.
 * Give your application a name and description.
 * Click on the Bot tab and create a new token.

This token should be used as environment variable `TOKEN`.

### Voice channel IDs

To retrieve the id of the voice channel, enable
Developer Mode in Discord (Settings -> Advanced -> Developer Mode).
Within a server select the voice channel you want the Rocket 
League Announcer to use. Right-click on the channel and select 
"Copy Channel ID". Use this value as environment variable 
`VOICE_CHANNEL_ID`.



## Samples

The default deployment contains a sample collection and mapping 
styled after old FPS games. It's a sample pack created by 
[jkerman](https://freesound.org/people/jkerman/) inspired by games 
like _Unreal_ _Tournament_ and _Quake_ _III_.

If you want to use your own samples, you can add them
to the directory mapped by the environment variable `SAMPLES_DIR`.
The Rocket League Announcer uses [Discord Voice](https://github.com/Janoz-NL/discord-voice) 
to load and play the samples. Read the documentation for more information about the possibilities.
To map the announcements to these samples you need to create a mapping 
file. The mapping file is explained in the [Mapping](mapping.md) section.
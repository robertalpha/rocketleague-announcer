# Configuration
The RocketLeague Annoucner is configured via environment variables.

## MQTT

TODO

## Discord

### Bot token
To connect to discord you need to create a bot. 
 * Go to https://discord.com/developers/applications and create a new appication.
 * Give your application a name and description.
 * Click on the bot tab and create a new token.

This token shouold be used as environment variable `TOKEN`.

### Voice channel Id's

To retrieve the id the voice channel, enable
developer mode in discord (settings -> advanced -> developer mode).
Within a server select the voice channel you want the Rocket 
League Announcer to use. Right click on the channel and select 
"Copy Channel ID". Use this value as environment variable 
`VOICE_CHANNEL_ID`.



## Samples

The default deployment contains an old FPS games styled sample 
collection and mapping. It's a sample pack created by 
[jkerman](https://freesound.org/people/jkerman/) inspired by games 
like Unreal Tournament and Quake III.

If you want to use your own samples, you can add these samples
to the directory mapped by the environment variable `SAMPLES_DIR`.
The Rocket League Announcer uses [Discord Voice](https://github.com/Janoz-NL/discord-voice) 
to load and play the 
samples. Read the documentation for more information about the posibilities.
To map the announcements to these samples you need to create a mapping 
file. The mapping file is explained in the [mapping](mapping.md) section.
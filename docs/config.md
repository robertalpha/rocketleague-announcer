# Configuration
The RocketLeague Annoucner is configured via environment variables.

## Discord

### Bot token
To connect to discord you need to create a bot. 
 * Go to https://discord.com/developers/applications and create a new appication.
 * Give your application a name and description.
 * Click on the bot tab and create a new token.

This token shouold be used as environment variable `TOKEN`.

### Guidl and voice channel Id's

To retrieve the id's of the guild and the voice channel enable
developer mode in discord (settings -> advanced -> developer mode).
With developer mode enabled you can right click on the server to 
select "Copy server ID". Use this value as environment variable 
`GUILD_ID`.

Within the server select the voice channel you want the Rocket 
League Announcer to use. Right click on the channel and select 
"Copy Channel ID". Use this value as environment variable 
`VOICE_CHANNEL_ID`.

## MQTT

TODO

## Samples

The default deployment contains an old FPS games styled sample 
collection and mapping. It's a sample pack created by 
[jkerman](https://freesound.org/people/jkerman/) inspired by games 
like Unreal Tournament and Quake III.

If you want to use your own samples, you can add these samples
to the directory mapped by the environment variable `SAMPLES_DIR`.
You'll need to create a mapping file to map these samples to the 
different announcements.

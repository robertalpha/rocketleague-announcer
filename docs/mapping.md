# Mapping Configuration

A mapping configuration is a json file that describes what sample 
will be sent to the channel when certain announcements are triggered.

```json
{
  "name": "Short name",
  "info": "Extended information about this mapping",
  "mapping": [
    {
      "announcement": "KILLED_BY_BOT",
      "weight": 10,
      "samples": [
        "you_re_a_looser.mp3","owned.mp3"
      ]
    },
    {
      "announcement": "AERIAL_GOAL",
      "weight": 5,
      "samples": [
        "nice_shot.mp3","youve_got_wings.mp3"
      ]
    }
  ]
}

```
## Maping info

### Name

The name of the mapping. This is what will be displayed in the UI.

### Info

the info field can be used for extra information about the mapping like
creator, sources and copyright.

## Mapping

The mapping is a list mapping an announcmement to a list of samples. 
Each mapping describes what will be sent to the channel when a 
certain announcement is triggered. Announcements are optional so 
only the announcements you want the announcer to react to need to be added. 

### Announcement

The announcement is the type of announcement that will be sent to the
channel. For a complete list of announcements and there meanings, see
[Announcements](../src/main/kotlin/model/Announcement.kt).

### Weight

Weight is used to determine which announcement will be sent. When two or
more announcements are triggered, the one with the highest weight will be
sent. Every weight must be a positive unique integer.

### Samples

Samples are the id's that will be played when the announcement is sent. 
If the list contains more than one sample, the announcer will pick one
of these samples at random. The id of the sample normally corresponds 
to the name of the file in the `samples` directory, unless its in a 
sample pack, than the id is defined in the json metadata file. For
more information about sampleIds see the
[Discord Voice documentation](https://github.com/Janoz-NL/discord-voice).
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
        "your_a_looser.mp3","owned.mp3"
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

the info field can be used for extra information about the mapping.

## Mapping

The mapping is a list of mappings. Each mapping describes what will be
sent to the channel when a certain announcement is triggered.

### Announcement

The announcement is the type of announcement that will be sent to the
channel. For a complete list of announcements and there meanings, see
[Announcements](../src/main/kotlin/model/Announcement.kt).

### Weight

Weight is used to determine which announcement will be sent. When two or
more announcements are triggered, the one with the highest weight will be
sent.

### Samples

Samples are the id's that will be played when the announcement is sent. 
The announcer will pick one of these samples at random. The id of the 
sample normally corresponds to the name of the file in the `samples` 
directory, unless its in a sample pack, than th id is defined in json 
metadata file.
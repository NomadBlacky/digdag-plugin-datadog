# digdag-plugin-datadog

This [Digdag](https://www.digdag.io) plugin provides operators of [Datadog](https://www.datadoghq.com).

[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
![Latest release badge](https://img.shields.io/maven-central/v/dev.nomadblacky/digdag-plugin-datadog_2.13)

## Getting Started

### 1. Add Datadog API key and Application key

#### From environment variables

```bash
export DATADOG_API_KEY=<your api key>
export DATADOG_APP_KEY=<your application key>
export DATADOG_SITE=<your site> # Optional, "US" or "EU", default is "US"
```

#### From Digdag secrets

```bash
digdag secrets --local --set datadog.api_key=<your api key>
digdag secrets --local --set datadog.app_key=<your apppplication key>
digdag secrets --local --set datadog.site=<your site> # Optional, "US" or "EU", default is "US"
```

If keys are set to both, the plugin use keys from secrets.

### 2. Add the plugin setting to your workflow.

The latest release version is... ![Latest release badge](https://img.shields.io/maven-central/v/dev.nomadblacky/digdag-plugin-datadog_2.13)

```yaml
_export:
  plugin:
    dependencies:
      - dev.nomadblacky:digdag-plugin-datadog_2.13:<version>
```

## Operators

There are full examples in the [`examples`](./examples) directory.

### datadog_event>: Send an event to Datadog.

```yaml
+example:
  datadog_event>:
    title: "[TEST] digdag-plugin-datadog"
    text: "Digdag meets Datadog!!"
```

![events.png](images/events.png)

#### Options

+ **title**: [required, string] The event title. Limited to 100 characters.
+ **text**: [required, string] The body of the event. Limited to 4000 characters. The text supports markdown.
+ **tags**: [optional, array of string] A list of tags to apply to the event.
+ **alert_type**: [optional, enum, default=`info`] If it’s an alert event, set its type between: `error`, `warning`, `info`, and `success`.
+ **priority**: [optional, enum, default=`normal`] The priority of the event: `normal` or `low`.

# digdag-plugin-datadog

This [Digdag](https://www.digdag.io) plugin provides operators of [Datadog](https://www.datadoghq.com).

[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

## Getting Started

### Add Datadog API key and Application key

Currently, only environment variables are supported.

```bash
export DATADOG_API_KEY=<your api key>
export DATADOG_APP_KEY=<your application key>
export DATADOG_SITE=<your site> # Optional, "US" or "EU", default is "US"
```

### Add the plugin setting to your workflow.

```yaml
_export:
  plugin:
    dependencies:
      - dev.nomadblacky:digdag-plugin-datadog_2.13:0.1.0
```

## Operators

There are full examples in the [`examples`](./examples) directory.

### datadog_event>: Send a event to Datadog.

```yaml
+example:
  datadog_event>:
    title: "[TEST] digdag-plugin-datadog"
    text: "Digdag meets Datadog!!"
```

![events.png](images/events.png)

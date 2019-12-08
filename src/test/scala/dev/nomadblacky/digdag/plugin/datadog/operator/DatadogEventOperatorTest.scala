package dev.nomadblacky.digdag.plugin.datadog.operator

import java.time.Instant

import dev.nomadblacky.digdag.plugin.datadog.operator.event.DatadogEventOperator
import io.digdag.client.config.Config
import io.digdag.spi.{OperatorContext, SecretProvider, TaskRequest, TaskResult}
import requests.Requester
import scaladog.api.DatadogSite
import scaladog.api.events._

class DatadogEventOperatorTest extends DigdagSpec {

  describe("runTask") {
    it("returns TaskResult.empty when operation is succeeded") {
      val (request, context) = newContext(
        newConfig(
          ujson.Obj(
            "_command" -> ujson.Obj(
              "title" -> "TITLE",
              "text"  -> "TEXT",
              "tags" -> ujson.Arr(
                "project:digdag-plugin-datadog",
                "env:test"
              )
            )
          )
        )
      )
      val operator = new DatadogEventOperator(context, EventsAPIClientFactoryForTest)

      assert(operator.runTask() === TaskResult.empty(request))
    }
  }

  private def newContext(config: Config): (TaskRequest, OperatorContext) = {
    val request = newTaskRequest(config)
    val context = newMock[OperatorContext] { m =>
      when(m.getTaskRequest).thenReturn(request)
      when(m.getProjectPath).thenReturn(newTempDirectory())
      when(m.getSecrets).thenReturn(new SecretProviderForTest(Map.empty))
    }
    (request, context)
  }
}

object EventsAPIClientFactoryForTest extends APIClientFactory[EventsAPIClient] {
  override def newClient(secrets: SecretProvider): Either[IllegalArgumentException, EventsAPIClient] =
    Right(new EventsAPIClientForTest)
  override protected def newClient(apiKey: String, appKey: String, site: DatadogSite): EventsAPIClient =
    new EventsAPIClientForTest
}

class EventsAPIClientForTest extends EventsAPIClient {
  override def postEvent(
      title: String,
      text: String,
      dateHappened: Instant,
      priority: Priority,
      host: String,
      tags: Seq[String],
      alertType: AlertType,
      aggregationKey: String,
      sourceTypeName: String,
      relatedEventId: Long,
      deviceName: String
  ): PostEventResponse =
    PostEventResponse("ok", 999L, "https://app.datadoghq.com/event/event?id=999")

  override def getEvent(id: Long): Event = ???

  override def query(
      start: Instant,
      end: Instant,
      priority: Priority,
      sources: Seq[String],
      tags: Seq[String],
      unaggregated: Boolean
  ): Seq[Event] = ???

  override protected def apiKey: String = "api_key"

  override protected def appKey: String = "app_key"

  override def site: DatadogSite = DatadogSite.US

  override protected def _requester: Option[Requester] = None
}

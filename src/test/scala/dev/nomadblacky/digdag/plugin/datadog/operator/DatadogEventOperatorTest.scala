package dev.nomadblacky.digdag.plugin.datadog.operator

import java.time.Instant

import io.digdag.spi.{OperatorContext, SecretProvider, TaskRequest, TaskResult}
import org.mockito.scalatest.MockitoSugar
import requests.Requester
import scaladog.api.DatadogSite
import scaladog.api.events._
import org.scalatest.funsuite.AnyFunSuite

class DatadogEventOperatorTest extends AnyFunSuite with MockitoSugar with TestUtils {
  test("Return TaskResult.empty when operation is succeeded") {
    val (request, context) = newContext(
      ujson.Obj(
        "title" -> "TITLE",
        "text"  -> "TEXT"
      )
    )
    val operator = new DatadogEventOperator(context, EventsAPIClientFactoryForTest)

    assert(operator.runTask() === TaskResult.empty(request))
  }

  private def newContext(commands: ujson.Obj): (TaskRequest, OperatorContext) = {
    val request = newTaskRequest(newConfig(commands))
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

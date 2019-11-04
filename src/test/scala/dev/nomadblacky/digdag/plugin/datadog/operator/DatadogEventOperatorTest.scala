package dev.nomadblacky.digdag.plugin.datadog.operator

import java.time.Instant

import io.digdag.spi.{OperatorContext, TaskResult}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.FunSuite
import requests.Requester
import scaladog.api.DatadogSite
import scaladog.api.events._

class DatadogEventOperatorTest extends FunSuite with MockitoSugar with TestUtils {
  test("Return TaskResult.empty when operation is succeeded") {
    val request = newTaskRequest(
      newConfig(
        ujson.Obj(
          "title" -> "TITLE",
          "text"  -> "TEXT"
        )
      )
    )
    val context = {
      val m = mock[OperatorContext]
      when(m.getTaskRequest).thenReturn(request)
      when(m.getProjectPath).thenReturn(newTempDirectory())
      m
    }
    val operator = new DatadogEventOperator(context, new TestEventsAPIClient)

    assert(operator.runTask() === TaskResult.empty(request))
  }
}

class TestEventsAPIClient extends EventsAPIClient {
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

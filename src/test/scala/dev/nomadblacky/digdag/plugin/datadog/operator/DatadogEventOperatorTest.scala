package dev.nomadblacky.digdag.plugin.datadog.operator

import java.time.Instant

import dev.nomadblacky.digdag.plugin.datadog.operator.event.DatadogEventOperator
import io.digdag.client.config.Config
import io.digdag.spi._
import requests.Requester
import scaladog.api.DatadogSite
import scaladog.api.events._

class DatadogEventOperatorTest extends DigdagSpec {

  describe("runTask") {

    def requiredParams = ujson.Obj(
      "_command" -> ujson.Obj(
        "title" -> "TITLE",
        "text"  -> "TEXT"
      )
    )

    describe("when required params is set") {
      describe("when operation is succeeded") {
        val params = ujson.Obj(
          "_command" -> ujson.Obj(
            "title"      -> "TITLE",
            "text"       -> "TEXT",
            "tags"       -> ujson.Arr("project:digdag-plugin-datadog"),
            "alert_type" -> "success"
          )
        )
        val (request, context) = newContext(newConfig(params))
        val client             = spy(new EventsAPIClientForTest)
        val operator           = new DatadogEventOperator(context, new EventsAPIClientFactoryForTest(client))

        it("returns a TaskResult.empty") {
          assert(operator.runTask() === TaskResult.empty(request))
        }

        it("invoked EventsAPIClient#postEvent with expected parameters") {
          verify(client).postEvent(
            title = eqTo("TITLE"),
            text = eqTo("TEXT"),
            dateHappened = any[Instant],
            priority = eqTo(Priority.Normal),
            host = any[String],
            tags = eqTo(Seq("project:digdag-plugin-datadog")),
            alertType = eqTo(AlertType.Success),
            aggregationKey = any[String],
            sourceTypeName = any[String],
            relatedEventId = any[Long],
            deviceName = any[String]
          )
        }
      }

      it("throws a TaskExecutionException when operation is failed") {
        val (_, context) = newContext(newConfig(requiredParams))
        val client = {
          val c = spy(new EventsAPIClientForTest)
          when(c.postEvent(any, any, any, any, any, any, any, any, any, any, any)).thenThrow(new RuntimeException)
          c
        }
        val factory  = new EventsAPIClientFactoryForTest(client)
        val operator = new DatadogEventOperator(context, factory)

        assertThrows[TaskExecutionException](operator.runTask())
      }
    }

    describe("when invalid params is set") {
      it("throws a TaskExecutionException when `title` is missing") {
        val params = requiredParams
        params.obj("_command").obj.remove("title")
        val (_, context) = newContext(newConfig(params))
        val operator     = new DatadogEventOperator(context, new EventsAPIClientFactoryForTest)

        assertThrows[TaskExecutionException](operator.runTask())
      }

      it("throws a TaskExecutionException when `text` is missing") {
        val params = requiredParams
        params.obj("_command").obj.remove("text")
        val (_, context) = newContext(newConfig(params))
        val operator     = new DatadogEventOperator(context, new EventsAPIClientFactoryForTest)

        assertThrows[TaskExecutionException](operator.runTask())
      }

      it("throws a TaskExecutionException when `alert_type` is invalid") {
        val params = requiredParams
        params.obj("_command").obj("alert_type") = "foo"
        val (_, context) = newContext(newConfig(params))
        val operator     = new DatadogEventOperator(context, new EventsAPIClientFactoryForTest)

        assertThrows[TaskExecutionException](operator.runTask())
      }

      it("throws a TaskExecutionException when `priority` is invalid") {
        val params = requiredParams
        params.obj("_command").obj("priority") = "foo"
        val (_, context) = newContext(newConfig(params))
        val operator     = new DatadogEventOperator(context, new EventsAPIClientFactoryForTest)

        assertThrows[TaskExecutionException](operator.runTask())
      }
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

class EventsAPIClientFactoryForTest(eventsAPIClient: EventsAPIClient = new EventsAPIClientForTest)
    extends APIClientFactory[EventsAPIClient] {
  override def newClient(secrets: SecretProvider): Either[IllegalArgumentException, EventsAPIClient] =
    Right(eventsAPIClient)

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

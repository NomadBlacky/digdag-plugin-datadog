package dev.nomadblacky.digdag.plugin.datadog.operator.event

import com.typesafe.scalalogging.StrictLogging
import dev.nomadblacky.digdag.plugin.datadog.operator.{APIClientFactory, DigdagConfigOps}
import io.digdag.client.config.Config
import io.digdag.spi._
import io.digdag.util.BaseOperator
import scaladog.api.DatadogSite
import scaladog.api.events.{EventsAPIClient, PostEventResponse}

import scala.util.{Failure, Try}

class DatadogEventOperatorFactory extends OperatorFactory {
  override val getType: String = DatadogEventOperator.Name

  override def newOperator(context: OperatorContext): Operator = new DatadogEventOperator(context)
}

private[operator] class DatadogEventOperator(
    _context: OperatorContext,
    clientFactory: APIClientFactory[EventsAPIClient] = DefaultEventsAPIClientFactory
) extends BaseOperator(_context)
    with DigdagConfigOps
    with StrictLogging {
  override def runTask(): TaskResult = {
    logger.info(s"Start the ${DatadogEventOperator.Name} operation.")

    val operationT = for {
      params    <- Try(request.getConfig.getNested("_command"))
      _         = logger.debug(params.toString)
      eventsApi <- clientFactory.newClient(context.getSecrets).toTry
      response  <- postEvent(eventsApi, params)
      _         = logger.info(s"Succeeded to post the event to Datadog. ${response.url}")
    } yield TaskResult.empty(request)

    operationT.recoverWith {
      case e => Failure(new TaskExecutionException(e))
    }.get
  }

  private def postEvent(eventsApi: EventsAPIClient, params: Config): Try[PostEventResponse] = Try {
    eventsApi.postEvent(
      title = params[String]("title"),
      text = params[String]("text")
    )
  }
}

object DatadogEventOperator {
  final val Name = "datadog_event"
}

object DefaultEventsAPIClientFactory extends APIClientFactory[EventsAPIClient] {
  override protected def newClient(apiKey: String, appKey: String, site: DatadogSite): EventsAPIClient =
    EventsAPIClient(apiKey, appKey, site)
}

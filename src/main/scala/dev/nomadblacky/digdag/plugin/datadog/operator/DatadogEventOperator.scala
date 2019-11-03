package dev.nomadblacky.digdag.plugin.datadog.operator

import com.typesafe.scalalogging.StrictLogging
import io.digdag.spi._
import io.digdag.util.BaseOperator
import scaladog.api.events.EventsAPIClient

class DatadogEventOperatorFactory extends OperatorFactory {
  override val getType: String = DatadogEventOperator.Name

  override def newOperator(context: OperatorContext): Operator = new DatadogEventOperator(context)
}

private[operator] class DatadogEventOperator(
    _context: OperatorContext,
    eventsApi: EventsAPIClient = EventsAPIClient()
) extends BaseOperator(_context)
    with StrictLogging {
  override def runTask(): TaskResult = {
    logger.info(s"Start the ${DatadogEventOperator.Name} operation.")

    val params = request.getConfig.getNested("_command")

    logger.debug(params.toString)

    val response = eventsApi.postEvent(
      title = params.get("title", classOf[String]),
      text = params.get("text", classOf[String])
    )

    logger.info(s"Succeeded to post the event to Datadog. ${response.url}")

    TaskResult.empty(request)
  }
}

object DatadogEventOperator {
  final val Name = "datadog_event"
}

package dev.nomadblacky.digdag.plugin.datadog

import java.util

import com.typesafe.scalalogging.StrictLogging
import io.digdag.spi._
import io.digdag.util.BaseOperator
import javax.inject.Inject

class DatadogPlugin extends Plugin {
  override def getServiceProvider[T](`type`: Class[T]): Class[_ <: T] =
    if (`type` eq classOf[OperatorProvider]) classOf[DatadogOperatorProvider] asSubclass (`type`) else null
}

class DatadogOperatorProvider @Inject() (templateEngine: TemplateEngine) extends OperatorProvider {
  override def get(): util.List[OperatorFactory] = util.Arrays.asList(new DatadogEventOperatorFactory(templateEngine))
}

class DatadogEventOperatorFactory(val templateEngine: TemplateEngine) extends OperatorFactory {
  override def getType: String = DatadogEventOperator.Name

  override def newOperator(context: OperatorContext): Operator = new DatadogEventOperator(context, templateEngine)
}

private[datadog] class DatadogEventOperator(val _context: OperatorContext, val templateEngine: TemplateEngine)
    extends BaseOperator(_context)
    with StrictLogging {
  private[this] val datadog = scaladog.Client()

  override def runTask(): TaskResult = {
    logger.info(s"Start the ${DatadogEventOperator.Name} operation.")

    val params = request.getConfig.getNested("_command")

    logger.debug(params.toString)

    val response = datadog.events.postEvent(
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

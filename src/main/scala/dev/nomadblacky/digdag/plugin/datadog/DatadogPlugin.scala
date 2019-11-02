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
  override def getType: String = "datadog_event"

  override def newOperator(context: OperatorContext): Operator = new DatadogEventOperator(context, templateEngine)
}

private[datadog] class DatadogEventOperator(val _context: OperatorContext, val templateEngine: TemplateEngine)
    extends BaseOperator(_context)
    with StrictLogging {
  private[this] val datadog = scaladog.Client()

  override def runTask(): TaskResult = {
    val response = datadog.events.postEvent(
      title = "[TEST] digdag-datadog-plugin",
      text = "Digdag meets Datadog!!"
    )

    logger.info(s"Succeeded to post an event to Datadog. ${response.url}")

    TaskResult.empty(request)
  }
}

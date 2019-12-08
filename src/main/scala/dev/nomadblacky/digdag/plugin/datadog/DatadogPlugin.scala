package dev.nomadblacky.digdag.plugin.datadog

import java.util

import dev.nomadblacky.digdag.plugin.datadog.operator.event.DatadogEventOperatorFactory
import io.digdag.spi._
import javax.inject.Inject

class DatadogPlugin extends Plugin {
  override def getServiceProvider[T](`type`: Class[T]): Class[_ <: T] =
    if (`type` eq classOf[OperatorProvider]) classOf[DatadogOperatorProvider] asSubclass (`type`) else null
}

class DatadogOperatorProvider @Inject() (templateEngine: TemplateEngine) extends OperatorProvider {
  override def get(): util.List[OperatorFactory] = util.Arrays.asList(new DatadogEventOperatorFactory())
}

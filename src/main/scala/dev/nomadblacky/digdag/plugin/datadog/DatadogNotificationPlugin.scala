package dev.nomadblacky.digdag.plugin.datadog

import com.google.inject.Binder
import com.google.inject.name.Names
import com.typesafe.scalalogging.StrictLogging
import io.digdag.spi._

class DatadogNotificationPlugin extends Plugin {
  override def getServiceProvider[T](`type`: Class[T]): Class[_ <: T] =
    if (`type` eq classOf[NotificationSender]) classOf[DatadogNotificationSender].asSubclass(`type`) else null

  override def configureBinder[T](`type`: Class[T], binder: Binder): Unit = {
    binder
      .bind(classOf[NotificationSender])
      .annotatedWith(Names.named("datadog"))
      .to(classOf[DatadogNotificationSender])
  }
}

class DatadogNotificationSender extends NotificationSender with StrictLogging {
  override def sendNotification(notification: Notification): Unit = {
    logger.info("=== DATADOG NOTIFICATION SENDER ===")
    logger.info(notification.getMessage)
  }
}

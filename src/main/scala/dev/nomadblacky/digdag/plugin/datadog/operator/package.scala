package dev.nomadblacky.digdag.plugin.datadog

import io.digdag.client.config.Config
import io.digdag.spi.SecretProvider

import scala.reflect.ClassTag

package object operator {
  implicit class RichConfig(config: Config) {
    def apply[A](key: String)(implicit tag: ClassTag[A]): A =
      config.get(key, tag.runtimeClass).asInstanceOf[A]

    def getOption[A](key: String)(implicit tag: ClassTag[A]): Option[A] = {
      val opt = config.getOptional(key, tag.runtimeClass)
      if (opt.isPresent) Option(opt.get().asInstanceOf[A]) else None
    }
  }

  implicit class RichSecretProvider(secretProvider: SecretProvider) {
    def getOption(key: String): Option[String] = {
      val opt = secretProvider.getSecretOptional(key)
      if (opt.isPresent) Option(opt.get()) else None
    }
  }
}

package dev.nomadblacky.digdag.plugin.datadog.operator

import io.digdag.spi.SecretProvider

trait SecretProviderOps {
  implicit class RichSecretProvider(secretProvider: SecretProvider) {
    def getOption(key: String): Option[String] = {
      val opt = secretProvider.getSecretOptional(key)
      if (opt.isPresent) Option(opt.get()) else None
    }
  }
}

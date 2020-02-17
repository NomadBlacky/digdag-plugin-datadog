package dev.nomadblacky.digdag.plugin.datadog.operator

import dev.nomadblacky.digdag.plugin.datadog.util.GoogleOptionalOps
import io.digdag.spi.SecretProvider
import scaladog.api.{APIClient, DatadogSite}

trait APIClientFactory[A <: APIClient] extends GoogleOptionalOps {
  private final val SecretKeyNameOfApiKey         = "datadog.api_key"
  private final val EnvKeyNameOfApiKey            = "DATADOG_API_KEY"
  private final val SecretKeyNameOfApplicationKey = "datadog.app_key"
  private final val EnvKeyNameOfApplicationKey    = "DATADOG_APP_KEY"
  private final val SecretKeyNameOfSite           = "datadog.site"
  private final val EnvKeyNameOfSite              = "DATADOG_SITE"

  protected val env: Map[String, String] = sys.env

  protected def newClient(apiKey: String, appKey: String, site: DatadogSite): A

  def newClient(secrets: SecretProvider): Either[IllegalArgumentException, A] =
    for {
      apiKey <- lookupApiKey(secrets)
      appKey <- lookupApiKey(secrets)
      site   <- lookupSite(secrets)
    } yield newClient(apiKey, appKey, site)

  def lookupApiKey(secrets: SecretProvider): Either[IllegalArgumentException, String] =
    lookup(secrets, SecretKeyNameOfApiKey, EnvKeyNameOfApiKey)
      .toRight(
        new IllegalArgumentException(
          s"Datadog API key not found. You must set the `$SecretKeyNameOfApiKey` secret or the `$EnvKeyNameOfApiKey` environment variable."
        )
      )

  def lookupApplicationKey(secrets: SecretProvider): Either[IllegalArgumentException, String] =
    lookup(secrets, SecretKeyNameOfApplicationKey, EnvKeyNameOfApplicationKey)
      .toRight(
        new IllegalArgumentException(
          s"Datadog Application key not found. You must set the `$SecretKeyNameOfApplicationKey` secret or the `$EnvKeyNameOfApplicationKey` environment variable."
        )
      )

  def lookupSite(secrets: SecretProvider): Either[IllegalArgumentException, DatadogSite] = {
    def errorMessage(name: String) =
      s"""The Datadog site "$name" is invalid. Check your `$SecretKeyNameOfApplicationKey` secret or the `$EnvKeyNameOfApplicationKey` environment variable."""
    lookup(secrets, SecretKeyNameOfSite, EnvKeyNameOfSite)
      .map { name =>
        DatadogSite.withNameInsensitiveOption(name).map(Right.apply).getOrElse {
          Left(new IllegalArgumentException(errorMessage(name)))
        }
      }
      .getOrElse(Right(DatadogSite.US))
  }

  private def lookup(secrets: SecretProvider, secretKey: String, envKey: String): Option[String] =
    secrets.getSecretOptional(secretKey).asScala.filter(_.nonEmpty).orElse(env.get(envKey).filter(_.nonEmpty))
}

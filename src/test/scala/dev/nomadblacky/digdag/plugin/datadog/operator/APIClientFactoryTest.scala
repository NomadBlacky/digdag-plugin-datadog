package dev.nomadblacky.digdag.plugin.datadog.operator

import org.scalatest.prop.{TableDrivenPropertyChecks, Tables}
import scaladog.api.DatadogSite
import scaladog.api.events.EventsAPIClient
import org.scalatest.funspec.AnyFunSpec

class APIClientFactoryTest extends AnyFunSpec with TestUtils with Tables with TableDrivenPropertyChecks {
  type Env             = Map[String, String]
  type Secret          = Map[String, String]
  type LookupResult[A] = Either[IllegalArgumentException, A]

  class APIClientFactoryForTest(override protected val env: Map[String, String])
      extends APIClientFactory[EventsAPIClient] {
    override protected def newClient(apiKey: String, appKey: String, site: DatadogSite): EventsAPIClient = ???
  }

  describe("lookupApiKey") {
    // format: off
    val table = Table[Env, Secret, LookupResult[String]](
      ("env"                              , "secret"                           , "expect"),
      (Map("DATADOG_API_KEY" -> "ENV_KEY"), Map.empty                          , Right("ENV_KEY")),
      (Map.empty                          , Map("datadog.api_key" -> "SEC_KEY"), Right("SEC_KEY")),
      (Map("DATADOG_API_KEY" -> "ENV_KEY"), Map("datadog.api_key" -> "SEC_KEY"), Right("SEC_KEY")),
      (Map.empty                          , Map.empty                          , Left(new IllegalArgumentException)),
      (Map("DATADOG_API_KEY" -> "")       , Map.empty                          , Left(new IllegalArgumentException)),
      (Map.empty                          , Map("datadog.api_key" -> "")       , Left(new IllegalArgumentException))
    )
    // format: on

    it("returns an API key when set valid parameters") {
      forAll(table) { (env, secret, expect) =>
        val factory = new APIClientFactoryForTest(env)
        val secrets = new SecretProviderForTest(secret)
        val actual  = factory.lookupApiKey(secrets)
        (actual, expect) match {
          case (Left(act), Left(exp)) => assert(act.getClass eq exp.getClass)
          case (exp, act)             => assert(exp === act)
        }
      }
    }
  }

  describe("lookupApplicationKey") {
    // format: off
    val table = Table[Env, Secret, LookupResult[String]](
      ("env"                              , "secret"                           , "expect"),
      (Map("DATADOG_APP_KEY" -> "ENV_KEY"), Map.empty                          , Right("ENV_KEY")),
      (Map.empty                          , Map("datadog.app_key" -> "SEC_KEY"), Right("SEC_KEY")),
      (Map("DATADOG_APP_KEY" -> "ENV_KEY"), Map("datadog.app_key" -> "SEC_KEY"), Right("SEC_KEY")),
      (Map.empty                          , Map.empty                          , Left(new IllegalArgumentException)),
      (Map("DATADOG_APP_KEY" -> "")       , Map.empty                          , Left(new IllegalArgumentException)),
      (Map.empty                          , Map("datadog.app_key" -> "")       , Left(new IllegalArgumentException))
    )
    // format: on

    it("returns an Application key when set valid parameters") {
      forAll(table) { (env, secret, expect) =>
        val factory = new APIClientFactoryForTest(env)
        val secrets = new SecretProviderForTest(secret)
        val actual  = factory.lookupApplicationKey(secrets)
        (actual, expect) match {
          case (Left(act), Left(exp)) => assert(act.getClass eq exp.getClass)
          case (exp, act)             => assert(exp === act)
        }
      }
    }
  }

  describe("lookupSite") {
    // format: off
    val table = Table[Env, Secret, LookupResult[DatadogSite]](
      ("env"                       , "secret"                    , "expect"),
      (Map("DATADOG_SITE" -> "US") , Map.empty                   , Right(DatadogSite.US)),
      (Map.empty                   , Map("datadog.site" -> "EU") , Right(DatadogSite.EU)),
      (Map("DATADOG_SITE" -> "US") , Map("datadog.site" -> "EU") , Right(DatadogSite.EU)),
      (Map.empty                   , Map.empty                   , Right(DatadogSite.US)),
      (Map("DATADOG_SITE" -> "FOO"), Map.empty                   , Left(new IllegalArgumentException)),
      (Map.empty                   , Map("datadog.site" -> "FOO"), Left(new IllegalArgumentException))
    )
    // format: on

    it("returns an Application key when set valid parameters") {
      forAll(table) { (env, secret, expect) =>
        val factory = new APIClientFactoryForTest(env)
        val secrets = new SecretProviderForTest(secret)
        val actual  = factory.lookupSite(secrets)
        (actual, expect) match {
          case (Left(act), Left(exp)) => assert(act.getClass eq exp.getClass)
          case (exp, act)             => assert(exp === act)
        }
      }
    }
  }
}

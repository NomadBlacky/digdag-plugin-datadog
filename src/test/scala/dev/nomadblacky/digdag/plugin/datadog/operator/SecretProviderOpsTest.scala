package dev.nomadblacky.digdag.plugin.datadog.operator

import org.scalatest.funspec.AnyFunSpec

class SecretProviderOpsTest extends AnyFunSpec with TestUtils with SecretProviderOps {

  val secrets = new SecretProviderForTest(Map("key" -> "value"))

  describe("RichSecretProvider") {
    describe("getOption") {
      it("returns a Some[String] when secrets contains the specified key") {
        assert(secrets.getOption("key") === Some("value"))
      }

      it("returns None when secrets doesn't contain the specified key") {
        assert(secrets.getOption("unknown") === None)
      }
    }
  }
}

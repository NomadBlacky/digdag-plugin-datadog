package dev.nomadblacky.digdag.plugin.datadog.util

import com.google.common.base.Optional
import dev.nomadblacky.digdag.plugin.datadog.operator.DigdagSpec

class GoogleOptionalOpsTest extends DigdagSpec with GoogleOptionalOps {
  describe("RichGoogleOptional") {
    describe("asScala") {
      it("returns a Some[String] when an Optional value is not empty") {
        assert(Optional.of("foo").asScala === Some("foo"))
      }

      it("returns None when an Optional value is empty") {
        assert(Optional.absent[String]().asScala === None)
      }
    }
  }
}

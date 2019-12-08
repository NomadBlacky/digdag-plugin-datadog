package dev.nomadblacky.digdag.plugin.datadog.operator

import io.digdag.client.config.{Config, ConfigException}

class DigdagConfigOpsTest extends DigdagSpec with DigdagConfigOps {

  val config: Config = newConfig(
    ujson
      .Obj(
        "key" -> "value",
        "list" -> ujson.Arr(
          1,
          2,
          3
        ),
        "empty_list" -> ujson.Arr()
      )
  )

  describe("RichConfig") {
    describe("apply") {
      it("returns a value of the specified type") {
        assert(config.apply[String]("key") === "value")
      }

      it("throws a ConfigException if the specified key is not found") {
        assertThrows[ConfigException](config.apply[String]("unknown"))
      }
    }

    describe("getSeqOrEmpty") {
      it("returns a Seq that has specified type elements") {
        assert(config.getSeqOrEmpty[Int]("list") === Seq(1, 2, 3))
      }

      it("returns an empty Seq if the specified key is not found") {
        assert(config.getSeqOrEmpty[Int]("foo") === Seq.empty)
      }
    }

    describe("getOption") {
      it("returns an Option[A] that has a specified type value") {
        assert(config.getOption[String]("key") === Some("value"))
      }

      it("returns None if the specified key is not found") {
        assert(config.getOption[String]("foo") === None)
      }
    }
  }
}

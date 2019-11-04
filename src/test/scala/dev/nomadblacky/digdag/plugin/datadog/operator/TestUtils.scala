package dev.nomadblacky.digdag.plugin.datadog.operator

import java.nio.file.{Files, Path}
import java.time.{Instant, ZoneId}
import java.util.UUID

import io.digdag.client.DigdagClient
import io.digdag.client.config.{Config, ConfigFactory}
import io.digdag.spi.{ImmutableTaskRequest, TaskRequest}

trait TestUtils {
  val configFactory = new ConfigFactory(DigdagClient.objectMapper())

  def newTaskRequest(config: Config): TaskRequest =
    ImmutableTaskRequest
      .builder()
      .siteId(1)
      .projectId(2)
      .workflowName("wf")
      .revision("rev")
      .taskId(3)
      .attemptId(4)
      .sessionId(5)
      .taskName("t")
      .lockId("l")
      .timeZone(ZoneId.systemDefault())
      .sessionUuid(UUID.randomUUID())
      .sessionTime(Instant.now())
      .createdAt(Instant.now())
      .config(config)
      .localConfig(configFactory.create())
      .lastStateParams(configFactory.create())
      .build()

  def newTempDirectory(): Path = Files.createTempDirectory("op_test")

  def newConfig(command: ujson.Obj): Config = {
    val json = ujson.Obj(
      "_command" -> command
    )
    configFactory.fromJsonString(json.render())
  }
}

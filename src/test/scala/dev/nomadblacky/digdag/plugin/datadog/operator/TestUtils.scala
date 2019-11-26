package dev.nomadblacky.digdag.plugin.datadog.operator

import java.nio.file.{Files, Path}
import java.time.{Instant, ZoneId}
import java.util.UUID

import com.google.common.base.Optional
import io.digdag.client.DigdagClient
import io.digdag.client.config.{Config, ConfigFactory}
import io.digdag.spi.{ImmutableTaskRequest, SecretProvider, TaskRequest}
import org.mockito.scalatest.MockitoSugar

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.WeakTypeTag

trait TestUtils extends MockitoSugar {
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

  def newMock[A <: AnyRef: ClassTag: WeakTypeTag](stubbing: A => Unit): A = {
    val m = mock[A]
    stubbing(m)
    m
  }

  class SecretProviderForTest(secrets: Map[String, String]) extends SecretProvider {
    override def getSecretOptional(key: String): Optional[String] =
      secrets.get(key).map(v => Optional.of(v)).getOrElse(Optional.absent())
  }
}

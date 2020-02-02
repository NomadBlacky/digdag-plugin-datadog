package dev.nomadblacky.digdag.plugin.datadog

import java.time.Instant
import java.util

import com.google.common.base.Optional
import com.google.inject.Binder
import com.typesafe.scalalogging.StrictLogging
import dev.nomadblacky.digdag.plugin.datadog.operator.event.DatadogEventOperatorFactory
import io.digdag.client.config.Config
import io.digdag.core.agent.{AgentId, InProcessTaskCallbackApi, TaskCallbackApi}
import io.digdag.core.database.TransactionManager
import io.digdag.core.log.{LogServerManager, TaskLogger}
import io.digdag.core.queue.TaskQueueServerManager
import io.digdag.core.repository.ProjectStoreManager
import io.digdag.core.session.{SessionStoreManager, StoredSessionAttempt}
import io.digdag.core.storage.ArchiveManager
import io.digdag.core.workflow.{AttemptBuilder, WorkflowExecutor}
import io.digdag.spi._
import javax.inject.Inject

class DatadogPlugin extends Plugin with StrictLogging {

  override def getServiceProvider[T](`type`: Class[T]): Class[_ <: T] =
    if (`type` eq classOf[OperatorProvider]) classOf[DatadogOperatorProvider] asSubclass (`type`) else null

  override def configureBinder[T](`type`: Class[T], binder: Binder): Unit = {
    super.configureBinder(`type`, binder)
    logger.info("configuring!!!!!!!!!!!!!!!!!!!!!!!!!")
//    binder.bind(classOf[TaskCallbackApi]).to(classOf[FooTaskCallbackApi]).in(Scopes.SINGLETON)
  }
}

class DatadogOperatorProvider @Inject() (templateEngine: TemplateEngine) extends OperatorProvider {
  override def get(): util.List[OperatorFactory] = util.Arrays.asList(new DatadogEventOperatorFactory())
}

class FooTaskCallbackApi @Inject() (
    pm: ProjectStoreManager,
    sm: SessionStoreManager,
    archiveManager: ArchiveManager,
    qm: TaskQueueServerManager,
    lm: LogServerManager,
    agentId: AgentId,
    attemptBuilder: AttemptBuilder,
    exec: WorkflowExecutor,
    tm: TransactionManager
) extends TaskCallbackApi
    with StrictLogging {

  val underlying = new InProcessTaskCallbackApi(pm, sm, archiveManager, qm, lm, agentId, attemptBuilder, exec, tm)

  override def newTaskLogger(request: TaskRequest): TaskLogger = underlying.newTaskLogger(request)

  override def taskHeartbeat(siteId: Int, lockedIds: util.List[String], agentId: AgentId, lockSeconds: Int): Unit =
    underlying.taskHeartbeat(siteId, lockedIds, agentId, lockSeconds)

  override def openArchive(request: TaskRequest): Optional[StorageObject] =
    underlying.openArchive(request)

  override def taskSucceeded(siteId: Int, taskId: Long, lockId: String, agentId: AgentId, result: TaskResult): Unit =
    underlying.taskSucceeded(siteId, taskId, lockId, agentId, result)

  override def taskFailed(siteId: Int, taskId: Long, lockId: String, agentId: AgentId, error: Config): Unit =
    underlying.taskFailed(siteId, taskId, lockId, agentId, error)

  override def retryTask(
      siteId: Int,
      taskId: Long,
      lockId: String,
      agentId: AgentId,
      retryInterval: Int,
      retryStateParams: Config,
      error: Optional[Config]
  ): Unit = underlying.retryTask(siteId, taskId, lockId, agentId, retryInterval, retryStateParams, error)

  override def startSession(
      siteId: Int,
      projectId: Int,
      workflowName: String,
      instant: Instant,
      retryAttemptName: Optional[String],
      overrideParams: Config
  ): StoredSessionAttempt = {
    logger.info("Session is started!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    underlying.startSession(siteId, projectId, workflowName, instant, retryAttemptName, overrideParams)
  }
}

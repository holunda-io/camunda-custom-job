package io.holunda.ext.customjob.api

import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity

/**
 * Marker interface for job payload.
 */
interface JobPayload

sealed class JobHandlerCommand<T : JobPayload>(open val payload: T)

/**
 * Wraps all information passed to the JobHandler#execute method.
 */
data class ExecuteJobCommand<T : JobPayload>(
  override val payload: T,
  val execution: ExecutionEntity?,
  val commandContext: CommandContext,
  val tenantId: String?) : JobHandlerCommand<T>(payload)


/**
 * Wraps all information passed to the JobHandler#onDelete method.
 */
data class OnJobDelete<T : JobPayload>(override val payload: T, val jobEntity: JobEntity) : JobHandlerCommand<T>(payload)


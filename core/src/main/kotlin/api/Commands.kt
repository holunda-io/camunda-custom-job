package io.holunda.ext.customjob.api

import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity
import java.time.Instant


data class OnJobDelete<T:JobPayload>(val payload: T, val jobEntity: JobEntity)

data class ExecuteJobCommand<T : JobPayload>(
  val payload: T,
  val execution: ExecutionEntity?,
  val commandContext: CommandContext,
  val tenantId: String?)

sealed class CreateJobCommand(open val jobHandlerType: String, open val payload: JobPayload)

data class ScheduleJobCommand (
  override val jobHandlerType: String,
  override val payload: JobPayload,
  val dueDate: Instant
) : CreateJobCommand(jobHandlerType, payload)

data class InsertJobCommand (
  override val jobHandlerType: String,
  override val payload: JobPayload
) : CreateJobCommand(jobHandlerType, payload)

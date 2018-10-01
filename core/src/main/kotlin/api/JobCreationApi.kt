package io.holunda.ext.customjob.api

import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity
import java.time.Instant


sealed class CreateJobCommand(open val jobHandlerType: String, open val payload: JobPayload)

/**
 * Schedule a job with given payload for given dueDate.
 */
data class ScheduleJobCommand (
  override val jobHandlerType: String,
  override val payload: JobPayload,
  val dueDate: Instant
) : CreateJobCommand(jobHandlerType, payload)

/**
 * Insert a job with given payload to be executed asap.
 */
data class InsertJobCommand (
  override val jobHandlerType: String,
  override val payload: JobPayload
) : CreateJobCommand(jobHandlerType, payload)


/**
 * Use this interface to publish a CreateJobCommand.
 */
interface CreateJobGateway {
  fun send(cmd: CreateJobCommand) : String
}

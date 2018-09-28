package io.holunda.ext.customjob.api

import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity
import javax.imageio.plugins.jpeg.JPEGHuffmanTable


data class OnJobDelete<T:JobPayload>(val payload: T, val jobEntity: JobEntity)

data class ExecuteJobCommand<T : JobPayload>(
  val payload: T,
  val execution: ExecutionEntity?,
  val commandContext: CommandContext,
  val tenantId: String?)

data class ScheduleJobCommand(val jobHandlerType: String)

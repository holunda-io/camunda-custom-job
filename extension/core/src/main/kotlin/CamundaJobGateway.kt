package io.holunda.ext.customjob

import io.holunda.job.CustomJobHandlerBuilder
import io.holunda.job.api.*
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.interceptor.Command
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity
import java.util.*

class CamundaJobGateway(private val configuration: ProcessEngineConfigurationImpl, private val jobHandlerBuilder: CustomJobHandlerBuilder) : CreateJobGateway {

  override fun send(cmd: CreateJobCommand): String = execute(when (cmd) {
    is ScheduleJobCommand -> toCamundaCommand(cmd)
    is InsertJobCommand -> toCamundaCommand(cmd)
  })

  private fun JobPayload.toConfig() = jobHandlerBuilder.jobHandlerConfiguration(this)

  private fun toCamundaCommand(cmd: ScheduleJobCommand) = Command {
    val job = TimerEntity().apply {
      jobHandlerType = cmd.jobHandlerType
      duedate = Date.from(cmd.dueDate)
      jobHandlerConfiguration = cmd.payload.toConfig()
    }

    it.jobManager.schedule(job)

    return@Command job.id
  }

  private fun toCamundaCommand(cmd: InsertJobCommand) = Command {
    val job = MessageEntity().apply {
      jobHandlerType = cmd.jobHandlerType
      jobHandlerConfiguration = cmd.payload.toConfig()
    }
    it.jobManager.insertJob(job)

    return@Command job.id
  }



  private fun execute(command: Command<String>): String =
    if (Context.getCommandContext() == null)
      configuration.commandExecutorTxRequired.execute(command)
    else
      command.execute(Context.getCommandContext())
}


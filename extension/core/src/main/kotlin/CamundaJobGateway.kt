package io.holunda.ext.customjob

import io.holunda.ext.customjob.api.CreateJobCommand
import io.holunda.ext.customjob.api.CreateJobGateway
import io.holunda.ext.customjob.api.InsertJobCommand
import io.holunda.ext.customjob.api.ScheduleJobCommand
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.context.Context
import org.camunda.bpm.engine.impl.interceptor.Command
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity
import java.time.Instant
import java.util.*

class CamundaJobGateway(private val configuration: ProcessEngineConfigurationImpl, private val jobHandlerBuilder: CustomJobHandlerBuilder) : CreateJobGateway {

  override fun send(cmd: CreateJobCommand): String {
    val command = when (cmd) {
      is ScheduleJobCommand -> Command<String> {
        TimerEntity().apply {
          jobHandlerType = cmd.jobHandlerType
          duedate = Date.from(cmd.dueDate)
          jobHandlerConfiguration = jobHandlerBuilder.jobHandlerConfiguration(cmd.payload)

          it.jobManager.schedule(this)
        }.id
      }

      is InsertJobCommand -> Command {
        MessageEntity().apply {
          jobHandlerType = cmd.jobHandlerType
          jobHandlerConfiguration = jobHandlerBuilder.jobHandlerConfiguration(cmd.payload)
        }.id
      }
    }

    return execute(command)
  }

  private fun execute(command: Command<String>): String =
    if (Context.getCommandContext() == null)
      configuration.commandExecutorTxRequired.execute(command)
    else
      command.execute(Context.getCommandContext())
}


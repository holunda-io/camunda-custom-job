package io.holunda.job

import com.fasterxml.jackson.databind.ObjectMapper
import io.holunda.job.api.CustomJobHandler
import io.holunda.job.api.ExecuteJobCommand
import io.holunda.job.api.JobPayload
import io.holunda.job.api.OnJobDelete
import org.camunda.bpm.engine.impl.interceptor.CommandContext
import org.camunda.bpm.engine.impl.jobexecutor.JobHandler
import org.camunda.bpm.engine.impl.jobexecutor.JobHandlerConfiguration
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity

interface CustomJobHandlerConfiguration<T : JobPayload> : JobHandlerConfiguration {
  val payload: T
}

class CustomJobHandlerBuilder(val objectMapper: ObjectMapper) {

  /**
   * Create a new JobHandlerConfiguration based on json/type.
   */
  fun <T : JobPayload> jobHandlerConfiguration(json: String, type: Class<T>) = jobHandlerConfiguration(objectMapper.readValue(json, type))

  /**
   * Create a new JobHandlerConfiguration based on payload.
   */
  fun <T : JobPayload> jobHandlerConfiguration(payload: T) = object : CustomJobHandlerConfiguration<T> {
    override val payload = payload

    override fun toCanonicalString() = objectMapper.writeValueAsString(payload)
  }

  /**
   * Create JobHandler instance based on custom job declaration.
   */
  fun <T : JobPayload> jobHandler(handler: CustomJobHandler<T>) = object : JobHandler<CustomJobHandlerConfiguration<T>> {
    override fun newConfiguration(canonicalString: String): CustomJobHandlerConfiguration<T> = jobHandlerConfiguration(canonicalString, handler.payloadType)

    override fun onDelete(configuration: CustomJobHandlerConfiguration<T>, jobEntity: JobEntity) =
      handler.onDelete(OnJobDelete(configuration.payload, jobEntity))

    override fun getType(): String = handler.type

    override fun execute(
      configuration: CustomJobHandlerConfiguration<T>,
      execution: ExecutionEntity?,
      commandContext: CommandContext,
      tenantId: String?
    ) = handler.execute(ExecuteJobCommand(configuration.payload, execution, commandContext, tenantId))

    override fun toString(): String = "${handler::class.simpleName}[type=${type}]"
  }

}

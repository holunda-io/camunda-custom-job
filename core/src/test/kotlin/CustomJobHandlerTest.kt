package io.holunda.ext.customjob

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.holunda.ext.customjob.api.ExecuteJobCommand
import io.holunda.ext.customjob.api.JobPayload
import io.holunda.ext.customjob.api.OnJobDelete
import io.holunda.ext.customjob.api.ScheduleJobCommand
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.camunda.bpm.engine.test.mock.MockExpressionManager
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.util.*

class CustomJobHandlerTest {

  val builder = CustomJobHandlerBuilder(jacksonObjectMapper())

  data class FooPayload(val name: String) : JobPayload

  object DummyJobHandler : CustomJobHandler<FooPayload> {

    override val type = "dummy"
    override val payloadType = FooPayload::class.java

    override fun execute(cmd: ExecuteJobCommand<FooPayload>) {
      println(cmd)
      println("config: ${cmd.payload}")
    }

    override fun onDelete(cmd: OnJobDelete<FooPayload>) {
      println(cmd)
    }
  }

  @get:Rule
  val camunda = ProcessEngineRule(StandaloneInMemProcessEngineConfiguration().apply {

    isJobExecutorActivate = false
    expressionManager = MockExpressionManager()
    databaseSchemaUpdate = DB_SCHEMA_UPDATE_DROP_CREATE
    isDbMetricsReporterActivate = false

    customJobHandlers = listOf(builder.jobHandler(DummyJobHandler))
  }.buildProcessEngine())

  @Test
  fun `custom job created and executed`() {
    val gateway = CamundaJobGateway(camunda.processEngineConfiguration, builder)

    val id = gateway.send(ScheduleJobCommand(jobHandlerType = DummyJobHandler.type, payload = FooPayload("foo"), dueDate = Instant.now()))

    val job = camunda.managementService.createJobQuery().jobId(id).singleResult() as TimerEntity

    assertThat(job).isNotNull
    assertThat(job.duedate).isInSameMinuteWindowAs(Date.from(Instant.now()))

    camunda.managementService.executeJob(job.id)

    assertThat(camunda.managementService.createJobQuery().jobId(id).singleResult()).isNull()
  }
}

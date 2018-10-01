package io.holunda.ext.customjob

import io.holunda.ext.customjob.test.DummyJobHandler
import io.holunda.ext.customjob.test.FooPayload
import io.holunda.ext.customjob.api.ScheduleJobCommand
import io.holunda.ext.customjob.test.builder
import io.holunda.ext.customjob.test.processEngineRule
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

  val handler = DummyJobHandler()

  @get:Rule
  val camunda = processEngineRule(handler)

  @Test
  fun `custom job created and executed`() {
    val gateway = CamundaJobGateway(camunda.processEngineConfiguration, builder)

    val id = gateway.send(ScheduleJobCommand(jobHandlerType = DummyJobHandler.TYPE, payload = FooPayload("foo"), dueDate = Instant.now()))

    val job = camunda.managementService.createJobQuery().jobId(id).singleResult() as TimerEntity

    assertThat(job).isNotNull
    assertThat(job.duedate).isInSameMinuteWindowAs(Date.from(Instant.now()))

    camunda.managementService.executeJob(job.id)

    assertThat(camunda.managementService.createJobQuery().jobId(id).singleResult()).isNull()

    assertThat(handler.executions).hasSize(1)
  }
}

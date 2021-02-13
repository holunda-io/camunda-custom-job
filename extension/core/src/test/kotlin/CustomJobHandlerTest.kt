package io.holunda.ext.customjob

import io.holunda.job.api.ScheduleJobCommand
import io.holunda.job.test.DummyJobHandler
import io.holunda.job.test.FooPayload
import io.holunda.job.test.builder
import io.holunda.job.test.processEngineRule
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity
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

package io.holunda.ext.customjob

import io.holunda.ext.customjob.api.CreateJobGateway
import io.holunda.ext.customjob.api.InsertJobCommand
import io.holunda.ext.customjob.test.DummyJobHandler
import io.holunda.ext.customjob.test.FooPayload
import io.holunda.ext.customjob.test.builder
import io.holunda.ext.customjob.test.processEngineRule
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.model.bpmn.Bpmn
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InsertJobFromJavaDelegate {

  val handler = DummyJobHandler()

  @get:Rule
  val camunda = processEngineRule(handler)

  val gateway by lazy {
    CamundaJobGateway(camunda.processEngineConfiguration, builder)
  }

  class CreateJobDelegate(private val gateway: CreateJobGateway) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
      gateway.send(InsertJobCommand(
        jobHandlerType = DummyJobHandler.TYPE,
        payload = FooPayload(name = "hello")
      ))
    }
  }

  @Before
  fun setUp() {
    camunda.manageDeployment(camunda.repositoryService.createDeployment()
      .addModelInstance("dummy.bpmn", Bpmn.createExecutableProcess("dummy")
        .startEvent()
        .serviceTask("service").camundaDelegateExpression("\${createJob}")
        .endEvent()
        .done())
      .deploy())

    Mocks.register("createJob", CreateJobDelegate(gateway))
  }

  @Test
  fun `create a job from a javaDelegate`() {
    assertThat(camunda.managementService.createJobQuery().list()).isEmpty()

    val pi = camunda.runtimeService.startProcessInstanceByKey("dummy")

    val job = camunda.managementService.createJobQuery().singleResult()

    assertThat(job).isNotNull

    camunda.managementService.executeJob(job.id)

    assertThat(handler.executions).isNotEmpty
  }
}

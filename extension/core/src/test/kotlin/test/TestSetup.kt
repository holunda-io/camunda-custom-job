package io.holunda.ext.customjob.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.holunda.ext.customjob.CustomJobHandlerBuilder
import io.holunda.ext.customjob.api.CustomJobHandler
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration
import org.camunda.bpm.engine.impl.history.HistoryLevel
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.camunda.bpm.engine.test.mock.MockExpressionManager

val objectMapper = jacksonObjectMapper()

val builder = CustomJobHandlerBuilder(objectMapper)

fun processEngineConfiguration(handler: CustomJobHandler<FooPayload>) = StandaloneInMemProcessEngineConfiguration().apply {

  isJobExecutorActivate = false
  expressionManager = MockExpressionManager()
  databaseSchemaUpdate = ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE
  isDbMetricsReporterActivate = false
  historyLevel = HistoryLevel.HISTORY_LEVEL_FULL

  customJobHandlers = listOf(builder.jobHandler(handler))
}

fun processEngineRule(handler: CustomJobHandler<FooPayload>) = ProcessEngineRule( processEngineConfiguration(handler).buildProcessEngine() )

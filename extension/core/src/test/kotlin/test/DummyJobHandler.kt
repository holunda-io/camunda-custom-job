package io.holunda.ext.customjob.test

import io.holunda.ext.customjob.api.CustomJobHandler
import io.holunda.ext.customjob.api.ExecuteJobCommand
import io.holunda.ext.customjob.api.JobPayload
import io.holunda.ext.customjob.api.OnJobDelete


data class FooPayload(val name: String) : JobPayload

class DummyJobHandler : CustomJobHandler<FooPayload> {
  companion object {
    const val TYPE = "dummy"
  }


  val executions = mutableListOf<ExecuteJobCommand<*>>()
  val deletions = mutableListOf<OnJobDelete<*>>()

  override val type = TYPE
  override val payloadType = FooPayload::class.java

  override fun execute(cmd: ExecuteJobCommand<FooPayload>) {
    executions.add(cmd)
  }

  override fun onDelete(cmd: OnJobDelete<FooPayload>) {
    deletions.add(cmd)
  }
}

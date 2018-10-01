package io.holunda.ext.customjob.api

/**
 * Implement this to create a jobHandler.
 */
interface CustomJobHandler<T : JobPayload> {
  val type: String
  val payloadType: Class<T>

  fun execute(cmd: ExecuteJobCommand<T>)

  fun onDelete(cmd: OnJobDelete<T>) = Unit
}

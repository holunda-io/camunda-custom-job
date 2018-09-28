package io.holunda.ext.customjob.api

interface CreateJobGateway {
  fun send(cmd: CreateJobCommand) : String
}

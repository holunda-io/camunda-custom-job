rootProject.name = "camunda-custom-job"

include("extension:api")
findProject(":extension:api")?.name = "camunda-custom-job-api"


include("extension:core")
findProject(":extension:core")?.name = "camunda-custom-job"

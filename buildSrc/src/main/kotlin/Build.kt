import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate

object Versions {
  val java = JavaVersion.VERSION_1_8
  val kotlin = "1.2.71"
  val dependencyManagement = "1.0.6.RELEASE"
  val springBoot = "2.0.5.RELEASE"
  val camunda = "7.9.0"
  val camundaEnterprise = "7.9.4-ee"
}

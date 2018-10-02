import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version Versions.kotlin
  kotlin("plugin.spring") version Versions.kotlin
  id("io.spring.dependency-management") version Versions.dependencyManagement
}

configure<DependencyManagementExtension> {
  imports {
    mavenBom("org.camunda.bpm:camunda-bom:${Versions.camunda}")
  }
}

dependencies {
  compile(project(":extension:camunda-custom-job-api"))

  compileOnly("org.camunda.bpm:camunda-engine")

  compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")

  testCompile("org.camunda.bpm:camunda-engine")
  testCompile("org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}")
  testCompile("org.mockito:mockito-core:2.15.0")
  testCompile("org.assertj:assertj-core:3.10.0")
  testCompile("com.h2database:h2:1.4.197")

}

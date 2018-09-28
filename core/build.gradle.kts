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
  compile(kotlin("stdlib-jdk8"))
  compile(kotlin("reflect"))
  compile("org.springframework.boot:spring-boot-starter")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = Versions.java
  freeCompilerArgs = listOf("-Xjsr305=strict")
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = Versions.java
  freeCompilerArgs = listOf("-Xjsr305=strict")
}

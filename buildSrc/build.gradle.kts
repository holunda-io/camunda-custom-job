import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
  `kotlin-dsl`
}

repositories {
  jcenter()
}

subprojects {
  if (hasSource("kotlin")) {
    applyKotlinConventions()
  }
}

fun Project.hasSource(name:String) = file("src/main/$name").isDirectory || file("src/test/$name").isDirectory
fun Project.applyKotlinConventions() {
  apply(plugin = "kotlin")

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_1_8.name
      freeCompilerArgs += listOf("-Xjsr305=strict")
    }
  }

  dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
  }

  plugins.withType<KotlinDslPlugin> {
    kotlinDslPluginOptions {
      experimentalWarning.set(false)
    }
  }
}

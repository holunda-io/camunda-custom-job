plugins {
  base
  idea
}

allprojects {
  group = "io.holunda.ext"
  version = "0.0.1-SNAPSHOT"

  repositories {
    mavenLocal()
    jcenter()
  }
}

dependencies {
  subprojects.forEach {
    archives(it)
  }
}

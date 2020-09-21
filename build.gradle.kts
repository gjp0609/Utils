plugins {
    kotlin("jvm") version "1.4.0"
    `java-library`
    `maven-publish`
}

group = "com.onysakura"
version = "0.0.1"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public/")
    jcenter()
    mavenCentral()
}

dependencies {
    val moshiVersion = "1.9.3"
    val sqliteJdbcVersion = "3.32.3.2"

    implementation(kotlin("stdlib"))
    implementation("org.xerial:sqlite-jdbc:$sqliteJdbcVersion")
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    sourceSets {
        main {
            java.srcDir("src/main/java")
        }
    }
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")
        }
        test {
            kotlin.srcDir("src/test/kotlin")
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.4"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.4"
        }
    }
}
plugins {
    kotlin("jvm") version "1.4.0"
    `java-library`
}

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.xerial:sqlite-jdbc:3.32.3.2")
    testImplementation(kotlin("test-testng"))
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

    test {
        useTestNG()
    }
}


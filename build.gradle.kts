plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "dev.simstoe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.lombok)
    implementation(libs.hikari.cp)

    annotationProcessor(libs.lombok)

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.shadowJar {
    relocate("com.zaxxer.hikari", "dev.simstoe.ranks.libs.hikari")
    archiveClassifier.set("")
}

tasks.jar {
    dependsOn("shadowJar")
}

tasks.test {
    useJUnitPlatform()
}
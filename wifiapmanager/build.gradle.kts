plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.rama.wifiapmanager"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()      // optional but recommended
            withJavadocJar()      // optional
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

val usernames = System.getenv("GITHUB_USERNAME") ?: error("GITHUB_USERNAME not set")
val token = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN not set")

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.rama"       // your library group
                artifactId = "wifiapmanager"
                version = "1.0.0"

                from(components["release"])
            }
        }
        repositories {
            // GitHub Packages
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/dinesh9936/RamaWifiAccessPointsManager")
                credentials {
                    username = usernames
                    password = token
                }
            }
        }
    }

}

plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android {
    namespace = "io.github.giuclab.recurio"
    compileSdk = 35
    defaultConfig { applicationId = "io.github.giuclab.recurio"; minSdk = 26; targetSdk = 35; versionCode = 10; versionName = "1.0.0" }
    buildFeatures { compose = true; buildConfig = true }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.core:core-ktx:1.16.0")
    testImplementation("junit:junit:4.13.2")
}

// Release signing is supplied by the build environment, never committed to Git.
val releaseStore = providers.environmentVariable("RECURIO_KEYSTORE_PATH")
android {
    if (releaseStore.isPresent) {
        signingConfigs {
            create("production") {
                storeFile = file(releaseStore.get())
                storePassword = providers.environmentVariable("RECURIO_STORE_PASSWORD").get()
                keyAlias = providers.environmentVariable("RECURIO_KEY_ALIAS").get()
                keyPassword = providers.environmentVariable("RECURIO_KEY_PASSWORD").get()
            }
        }
        buildTypes.getByName("release").signingConfig = signingConfigs.getByName("production")
    }
}

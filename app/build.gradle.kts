import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services") version "4.4.4" apply true
    kotlin("kapt")
}
android {
    namespace = "com.example.petcare"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.petcare"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.example.petcare.HiltTestRunner"
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY")
        val openAiApiKey = localProperties.getProperty("OPENAI_API_KEY")
        manifestPlaceholders["OPENAI_API_KEY"] = openAiApiKey;
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey;
        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiApiKey\"")
        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        buildFeatures{
            compose = true
            buildConfig = true
        }

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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}

configurations.matching { it.name.endsWith("RuntimeClasspath") }.configureEach {
    exclude(group = "com.google.auto.value", module = "auto-value")
    exclude(group = "com.google.auto.value", module = "auto-value-annotations")
}

dependencies {
    // --- COMPOSE BOM (zarzadza wersjami Compose) ---
    implementation(platform(libs.androidx.compose.bom))

    // --- CORE & LIFECYCLE ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata") // wersja z BOM

    // --- COMPOSE UI & MATERIAL ---
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")

    // --- NAVIGATION ---
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // --- HILT ---
    implementation(libs.hilt.android)
    implementation(libs.androidx.material3)
    kapt(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-work:1.3.0")
    kapt("androidx.hilt:hilt-compiler:1.3.0")

    // --- FIREBASE ---
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")

    // --- WORK MANAGER ---
    implementation("androidx.work:work-runtime-ktx:2.11.0")

    // --- MAPS ---
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // --- INNE ---
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.timber)
    implementation("org.dmfs:lib-recur:0.17.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("com.kizitonwose.calendar:compose:2.6.1")

    // --- DESUGARING ---
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // --- DEBUG ---
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- TESTING (unit) ---
    testImplementation("junit:junit:4.13.2")
    testImplementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // --- TESTING (androidTest) ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.compiler)

    // --- HTTP client ---
    implementation("io.ktor:ktor-client-core:2.3.3")
    implementation("io.ktor:ktor-client-okhttp:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")

    // -- PDF --
    implementation("com.github.librepdf:openpdf:1.3.39")


}

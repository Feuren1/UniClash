plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "project.main.uniclash"
    compileSdk = 34

    defaultConfig {
        applicationId = "project.main.uniclash"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        viewBinding = true //added = icon
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
    testImplementation("io.mockk:mockk:1.11.0")
    implementation("io.coil-kt:coil-gif:2.5.0")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.emoji2:emoji2:1.4.0")
    implementation("androidx.emoji2:emoji2-views:1.4.0")
    implementation("androidx.emoji2:emoji2-views-helper:1.4.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.4.0-alpha02")
    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-video:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.camera:camera-extensions:1.3.0")
    implementation ("androidx.camera:camera-core:1.2.0")
    implementation ("androidx.camera:camera-camera2:1.2.0")
    implementation ("androidx.camera:camera-lifecycle:1.2.0")
    implementation ("androidx.camera:camera-view:1.0.0-alpha25")
    implementation("androidx.camera:camera-view:1.2.3")
    implementation("androidx.camera:camera-video:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.31.2-alpha")
    implementation ("androidx.fragment:fragment:1.6.2")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.4")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.datastore:datastore-core:1.0.0")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.media3:media3-common:1.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:5.7.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.2.1")
    implementation("org.mockito:mockito-core:5.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation ("tech.utsmankece:osm-android-compose:0.0.5")
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation ("com.google.android.gms:play-services-location:17.1.0")
    implementation ("androidx.activity:activity-ktx:1.4.0")
    implementation ("androidx.activity:activity-compose:1.4.0-alpha01")
    implementation ("io.coil-kt:coil-compose:1.3.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-inappmessaging-display")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
}

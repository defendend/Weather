plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.defendend.weather"
        minSdk = 27
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0-alpha06")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("androidx.preference:preference-ktx:1.2.0")

    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation("androidx.room:room-runtime:2.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.cardview:cardview:1.0.0")

    annotationProcessor("androidx.room:room-compiler:2.4.2")
    kapt("androidx.room:room-compiler:2.4.2")



    implementation("com.google.dagger:hilt-android:2.41")
    kapt("com.google.dagger:hilt-compiler:2.41")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.41")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.41")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.41")
    kaptTest("com.google.dagger:hilt-compiler:2.41")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

kapt {
    correctErrorTypes = true
}
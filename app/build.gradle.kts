plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(Application.compileSdkVersion)

    defaultConfig {
        applicationId = Application.applicationId
        minSdkVersion(Application.minSdkVersion)
        targetSdkVersion(Application.targetSdkVersion)
        versionCode = Application.versionCode
        versionName = Application.versionName
        testInstrumentationRunner = Application.testInstrumentationRunner
    }

    buildTypes {
        getByName("release") {
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
        dataBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Dependencies.Kotlin.kotlin_stdlib)
    implementation("androidx.core:core-ktx:${Versions.coreKtx}")
    implementation("androidx.appcompat:appcompat:${Versions.appCompat}")
    implementation("com.google.android.material:material:${Versions.materialTheme}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}")
    implementation(platform("com.google.firebase:firebase-bom:${Versions.firebaseBom}"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutinesPlayServices}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidxLifecycle}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}")
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.navigation}")
    implementation("androidx.navigation:navigation-ui-ktx:${Versions.navigation}")
    implementation("com.github.bumptech.glide:glide:${Versions.glide}")
    kapt("com.github.bumptech.glide:compiler:${Versions.glide}")
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-gson:${Versions.retrofit}")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitCoroutinesAdapter}")
    implementation("androidx.activity:activity-ktx:${Versions.activity}")
    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-compiler:${Versions.hilt}")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hiltViewModel}")
    kapt("androidx.hilt:hilt-compiler:${Versions.hiltCompiler}")
    api("com.theartofdev.edmodo:android-image-cropper:${Versions.imageCropper}")
    implementation("com.facebook.shimmer:shimmer:${Versions.facebookShimmer}")
    testImplementation("junit:junit:${Versions.junit}")
    androidTestImplementation("androidx.test.ext:junit:${Versions.junitExt}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
}

apply(plugin = "com.google.gms.google-services")
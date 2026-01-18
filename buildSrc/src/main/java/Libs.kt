@file:Suppress("unused")

object Libs {
    // Kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    // Kuper
    const val kuper = "dev.jahir:Kuper:${Versions.kuper}@aar"

    // OneSignal
    const val oneSignal = "com.onesignal:OneSignal:${Versions.oneSignal}"
    
    // Material Design 3
    const val material = "com.google.android.material:material:${Versions.material}"
    
    // Coil - Image Loading
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coil}"
    
    // Testing - JUnit 5
    const val junit5Api = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
    const val junit5Params = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
    
    // Testing - Kotest
    const val kotestRunner = "io.kotest:kotest-runner-junit5:${Versions.kotest}"
    const val kotestAssertions = "io.kotest:kotest-assertions-core:${Versions.kotest}"
    const val kotestProperty = "io.kotest:kotest-property:${Versions.kotest}"
    
    // Testing - MockK
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val mockkAndroid = "io.mockk:mockk-android:${Versions.mockk}"
    
    // Testing - Robolectric
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    
    // Testing - Espresso
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
    
    // Testing - AndroidX Test
    const val androidxTestCore = "androidx.test:core:${Versions.androidxTest}"
    const val androidxTestRunner = "androidx.test:runner:${Versions.androidxTest}"
    const val androidxTestRules = "androidx.test:rules:${Versions.androidxTest}"
}
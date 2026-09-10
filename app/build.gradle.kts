import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "tr.yurdunubil.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "tr.yurdunubil.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 8
        versionName = "0.5.3"
        buildConfigField("String", "SUPABASE_URL", "\"https://phcdfnvqhhwkhrxsmuar.supabase.co\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"sb_publishable_vwk3J5ag16XmMOm3x734MQ_-mMsJVe2\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

val syncCanonicalAppIcon = tasks.register("syncCanonicalAppIcon") {
    outputs.file(file("src/main/res/drawable/yurdunu_bil_app_icon.png"))
    doLast {
        val source = rootProject.file("file_00000000915c81f4882e3e45e01e1320.png")
        val target = file("src/main/res/drawable/yurdunu_bil_app_icon.png")
        require(source.exists()) { "Canonical app icon missing: ${source.absolutePath}" }
        target.parentFile.mkdirs()
        source.copyTo(target, overwrite = true)
    }
}

tasks.named("preBuild") { dependsOn(syncCanonicalAppIcon) }

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    // Tabler's MIT-licensed icon family, exposed as Compose ImageVectors.
    implementation("io.github.ardasoyturk.compose.icons:tabler-icons:2.0.7")
    debugImplementation("androidx.compose.ui:ui-tooling")

    val supabaseVersion = "3.5.0"
    implementation(platform("io.github.jan-tennert.supabase:bom:$supabaseVersion"))
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    implementation("io.github.jan-tennert.supabase:functions-kt")
    implementation("io.ktor:ktor-client-android:3.0.3")
}

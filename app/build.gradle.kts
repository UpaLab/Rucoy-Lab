plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Assinatura: os valores vêm dos segredos do GitHub (variáveis de ambiente no GitHub Actions).
// A chave NUNCA fica no repositório. Sem os segredos, só o APK de debug é gerado.
val signingKeyPath: String? = System.getenv("SIGNING_KEY_PATH")
val temChave = signingKeyPath != null && file(signingKeyPath).exists()

android {
    namespace = "io.github.upalab.rucoylab"
    compileSdk = 37

    defaultConfig {
        // Identificador do app. NÃO MUDAR (ver docs/app.md, seção 7).
        applicationId = "io.github.upalab.rucoylab"
        minSdk = 26
        targetSdk = 36
        versionCode = (System.getenv("GITHUB_RUN_NUMBER") ?: "1").toInt()
        versionName = "0.1.0"
    }

    signingConfigs {
        if (temChave) {
            create("release") {
                storeFile = file(signingKeyPath!!)
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            if (temChave) signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    // A tabela de mobs vem direto de data/mobs.json (uma fonte só para docs e app).
    sourceSets.getByName("main").assets.directories.add("../data")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Só bibliotecas oficiais do Android/Compose.
    val composeBom = platform("androidx.compose:compose-bom:2026.09.00")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.json:json:20250517")
}

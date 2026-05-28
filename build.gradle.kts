// Топ-уровень: здесь плагины ТОЛЬКО объявляются, но не применяются к самому корню
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
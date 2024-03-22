package me.chung.utils

object SystemEnvironmentVariableProvider {
    fun getEnvVariable(key: String): String =
        System.getenv(key) ?: throw IllegalStateException("please set $key in environment")

    fun getEnvVariableOrNull(key: String): String? = System.getenv(key)
}
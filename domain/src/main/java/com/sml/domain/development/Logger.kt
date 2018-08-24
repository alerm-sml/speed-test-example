package com.sml.domain.development

interface Logger {
    fun d(message: String, vararg args: Any)
}
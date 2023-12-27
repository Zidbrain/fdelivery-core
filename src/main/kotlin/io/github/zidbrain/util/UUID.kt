package io.github.zidbrain.util

import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)

fun String.tryToUUID(): UUID? = try {
    UUID.fromString(this)
} catch (ex: Throwable) {
    null
}
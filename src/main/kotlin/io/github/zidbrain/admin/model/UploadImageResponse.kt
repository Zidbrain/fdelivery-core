package io.github.zidbrain.admin.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponse(
    val relativePath: String
)

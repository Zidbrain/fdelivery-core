package io.github.zidbrain.order.model

import io.github.zidbrain.auth.model.ExposedUserDto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class OrderStatusDto(
    val orderId: String,
    val status: OrderStatus,
    val assignedTo: ExposedUserDto?
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(OffsetDateTime::class)
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {

    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun deserialize(decoder: Decoder): OffsetDateTime =
        OffsetDateTime.from(formatter.parse(decoder.decodeString()))

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.format(formatter))
    }

}
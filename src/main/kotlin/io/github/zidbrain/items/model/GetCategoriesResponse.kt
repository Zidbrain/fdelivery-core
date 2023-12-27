package io.github.zidbrain.items.model

import io.github.zidbrain.items.tables.ItemDao
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

@Serializable
data class GetCategoriesResponse(
    val categories: List<CategoryDto>,
    @Serializable(with = BigDecimalSerializer::class)
    val deliveryFee: BigDecimal
)

@Serializable
data class CategoryDto(val id: String? = null, val name: String, val items: List<ItemDto>)

@Serializable
data class ItemDto(
    val id: String? = null,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val imagePath: String
)

fun ItemDao.setFromDto(itemDto: ItemDto) {
    name = itemDto.name
    price = itemDto.price
    imagePath = imagePath
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(BigDecimal::class)
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}
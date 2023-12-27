package io.github.zidbrain.items.service

import io.github.zidbrain.admin.tables.SettingDao
import io.github.zidbrain.items.model.CategoryDto
import io.github.zidbrain.items.model.UpdateItemAction
import io.github.zidbrain.items.model.setFromDto
import io.github.zidbrain.items.tables.Categories
import io.github.zidbrain.items.tables.CategoryDao
import io.github.zidbrain.items.tables.ItemDao
import io.github.zidbrain.items.tables.Items
import io.github.zidbrain.order.tables.OrderedItemDao
import io.github.zidbrain.order.tables.OrderedItems
import io.github.zidbrain.util.toUUID
import io.github.zidbrain.util.tryToUUID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.util.*

class ItemsService(private val userDatabase: Database, private val adminDatabase: Database) {

    fun getCategories() = transaction(userDatabase) {
        CategoryDao.all().map { it.toDto() }
    }

    fun getDeliveryFee() = transaction(userDatabase) {
        return@transaction SettingDao.findById("deliveryFee")!!.value!!
    }

    private fun updateItems(actions: List<UpdateItemAction>, categoryDao: CategoryDao) {
        actions.forEach {
            when (it) {
                is UpdateItemAction.Create -> ItemDao.new {
                    setFromDto(it.item)
                    category = categoryDao
                }

                is UpdateItemAction.Delete -> Items.deleteWhere { _ -> id eq it.id.toUUID() }
                is UpdateItemAction.Update -> ItemDao[it.item.id!!.toUUID()].apply { setFromDto(it.item) }
            }
        }
    }

    fun updateCategories(request: List<CategoryDto>) = transaction(adminDatabase) {
        val orderedCache = OrderedItemDao.all().toList()
        OrderedItems.deleteAll()
        Items.deleteAll()
        Categories.deleteAll()
        
        request.forEach { 
            val category = CategoryDao.new(it.id?.tryToUUID() ?: UUID.randomUUID()) {
                name = it.name
            }
            it.items.forEach { item ->
                ItemDao.new(item.id?.tryToUUID() ?: UUID.randomUUID()) { 
                    name = item.name
                    price = item.price
                    imagePath = item.imagePath
                    this.category = category
                }
            }
        }

        orderedCache.forEach {
            OrderedItemDao.new(it.id.value) {
                orderedAmount = it.orderedAmount
                order = it.order
                item = it.item?.let { ItemDao.findById(it.id.value) }
            }
        }
    }

    fun setDeliveryFee(deliveryFee: BigDecimal) = transaction(adminDatabase) {
        SettingDao["deliveryFee"].apply { value = deliveryFee.toPlainString() }
    }
}

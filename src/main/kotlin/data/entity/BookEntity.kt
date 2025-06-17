package com.dpv.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

class BookEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<BookEntity>(Books)

    var title by Books.title
    var author by Books.author
    var description by Books.description
    var createdAt by Books.createdAt
    var updatedAt by Books.updatedAt
}

object Books: IntIdTable("books") {
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val description = text("description").nullable().default(null)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
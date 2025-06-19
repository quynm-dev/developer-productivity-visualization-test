package com.dpv.data.entity

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

class RepositoryEntity(id: EntityID<Long>): LongEntity(id) {
    companion object : LongEntityClass<RepositoryEntity>(Repositories)

    var name by Repositories.name
    var githubUrl by Repositories.github_url
    var ownerId by Repositories.ownerId
    var language by Repositories.language
    var pullsUrl by Repositories.pulls_url
    var commitsUrl by Repositories.commits_url
    var lastSyncAt by Repositories.lastSyncAt
    var createdAt by Repositories.createdAt
    var updatedAt by Repositories.updatedAt
}

object Repositories: LongIdTable("repositories") {
    val name = varchar("name", 255).uniqueIndex()
    val github_url = text("github_url")
    val ownerId = reference("owner_id", Users)
    val language = varchar("language", 255)
    val pulls_url = text("pulls_url")
    val commits_url = text("commits_url")
    val lastSyncAt = datetime("last_sync_at").nullable().default(null)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
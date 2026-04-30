package com.memoamber.desktop.data

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 桌面端数据库 (JDBC + SQLite)
 * 数据存储在 ~/.memoamber/data.db
 */
class DesktopDatabaseManager {

    private val dataDir = File(System.getProperty("user.home"), ".memoamber")
    private val dbFile = File(dataDir, "data.db")
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private var conn: Connection? = null

    init {
        dataDir.mkdirs()
        initTables()
    }

    private fun db(): Connection =
        conn ?: DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}").also { conn = it }

    private fun initTables() {
        db().createStatement().use { s ->
            s.execute("CREATE TABLE IF NOT EXISTS diary_entries (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, mood TEXT DEFAULT '', location TEXT DEFAULT '', created_at TEXT NOT NULL, updated_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS vault_items (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, username TEXT NOT NULL, encrypted_password TEXT NOT NULL, url TEXT DEFAULT '', notes TEXT DEFAULT '', category TEXT DEFAULT 'general', created_at TEXT NOT NULL, updated_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS messages (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, status TEXT DEFAULT 'draft', recipient TEXT DEFAULT '', recipient_email TEXT DEFAULT '', send_method TEXT DEFAULT 'none', scheduled_at TEXT DEFAULT '', created_at TEXT NOT NULL, updated_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS media_items (id INTEGER PRIMARY KEY AUTOINCREMENT, file_path TEXT NOT NULL, description TEXT DEFAULT '', media_type TEXT DEFAULT 'image', created_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS community_posts (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, author TEXT DEFAULT '匿名', category TEXT DEFAULT 'general', tags TEXT DEFAULT '', likes INTEGER DEFAULT 0, created_at TEXT NOT NULL)")
            s.execute("CREATE TABLE IF NOT EXISTS email_config (id INTEGER PRIMARY KEY AUTOINCREMENT, smtp_host TEXT DEFAULT '', smtp_port INTEGER DEFAULT 587, smtp_user TEXT DEFAULT '', smtp_pass_encrypted TEXT DEFAULT '', sender_name TEXT DEFAULT '', use_tls INTEGER DEFAULT 1)")
            s.execute("CREATE TABLE IF NOT EXISTS scheduled_tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, message_id INTEGER NOT NULL, scheduled_at TEXT NOT NULL, status TEXT DEFAULT 'pending', created_at TEXT NOT NULL)")
            // 迁移：如果旧表 wills 存在但 messages 不存在，迁移数据
            try {
                val rs = s.executeQuery("SELECT count(*) FROM wills LIMIT 1")
                rs.close()
                // wills 表存在，检查 messages 是否为空
                val rs2 = s.executeQuery("SELECT count(*) FROM messages")
                val msgCount = if (rs2.next()) rs2.getInt(1) else 0
                rs2.close()
                if (msgCount == 0) {
                    s.execute("INSERT INTO messages (id, title, content, status, recipient, recipient_email, send_method, scheduled_at, created_at, updated_at) SELECT id, title, content, status, recipient, '', send_method, '', created_at, updated_at FROM wills")
                }
            } catch (_: Exception) { /* wills表不存在，忽略 */ }
        }
    }

    private fun now() = LocalDateTime.now().format(fmt)

    // ===== 日记 =====
    fun insertDiary(title: String, content: String, mood: String = "", location: String = ""): Long =
        insert("diary_entries", "title, content, mood, location, created_at, updated_at", title, content, mood, location, now(), now())
    fun getAllDiaries(): List<Map<String, String>> = query("SELECT * FROM diary_entries ORDER BY created_at DESC")
    fun updateDiary(id: Long, title: String, content: String, mood: String, location: String): Boolean =
        update("diary_entries", "title = ?, content = ?, mood = ?, location = ?, updated_at = ?", title, content, mood, location, now(), id)
    fun deleteDiary(id: Long) = delete("diary_entries", id)

    // ===== 密码箱 =====
    fun insertVaultItem(title: String, username: String, encPwd: String, url: String = "", notes: String = ""): Long =
        insert("vault_items", "title, username, encrypted_password, url, notes, category, created_at, updated_at", title, username, encPwd, url, notes, "general", now(), now())
    fun getAllVaultItems(): List<Map<String, String>> = query("SELECT * FROM vault_items ORDER BY created_at DESC")
    fun updateVaultItem(id: Long, title: String, username: String, encPwd: String, url: String, notes: String): Boolean =
        update("vault_items", "title = ?, username = ?, encrypted_password = ?, url = ?, notes = ?, updated_at = ?", title, username, encPwd, url, notes, now(), id)
    fun deleteVaultItem(id: Long) = delete("vault_items", id)

    // ===== 给谁的留言（原遗嘱） =====
    fun insertMessage(title: String, content: String, recipient: String = "", recipientEmail: String = "", sendMethod: String = "none", scheduledAt: String = ""): Long =
        insert("messages", "title, content, recipient, recipient_email, send_method, scheduled_at, created_at, updated_at", title, content, recipient, recipientEmail, sendMethod, scheduledAt, now(), now())
    fun getAllMessages(): List<Map<String, String>> = query("SELECT * FROM messages ORDER BY created_at DESC")
    fun updateMessage(id: Long, title: String, content: String, recipient: String, recipientEmail: String, sendMethod: String, scheduledAt: String): Boolean =
        update("messages", "title = ?, content = ?, recipient = ?, recipient_email = ?, send_method = ?, scheduled_at = ?, updated_at = ?", title, content, recipient, recipientEmail, sendMethod, scheduledAt, now(), id)
    fun deleteMessage(id: Long) = delete("messages", id)

    // ===== 相册 =====
    fun insertMedia(path: String, desc: String = "", type: String = "image"): Long =
        insert("media_items", "file_path, description, media_type, created_at", path, desc, type, now())
    fun getAllMedia(): List<Map<String, String>> = query("SELECT * FROM media_items ORDER BY created_at DESC")
    fun deleteMedia(id: Long) = delete("media_items", id)

    // ===== 社区 =====
    fun insertPost(content: String, author: String = "匿名"): Long =
        insert("community_posts", "content, author, category, tags, created_at", content, author, "general", "", now())
    fun getAllPosts(): List<Map<String, String>> = query("SELECT * FROM community_posts ORDER BY created_at DESC")

    // ===== 邮箱配置 =====
    fun getEmailConfig(): Map<String, String>? {
        val results = query("SELECT * FROM email_config LIMIT 1")
        return results.firstOrNull()
    }
    fun saveEmailConfig(smtpHost: String, smtpPort: String, smtpUser: String, smtpPassEncrypted: String, senderName: String, useTls: Boolean): Long {
        // 先删除旧配置
        db().createStatement().use { it.execute("DELETE FROM email_config") }
        return insert("email_config", "smtp_host, smtp_port, smtp_user, smtp_pass_encrypted, sender_name, use_tls", smtpHost, smtpPort, smtpUser, smtpPassEncrypted, senderName, if (useTls) "1" else "0")
    }

    // ===== 通用 CRUD =====
    private fun insert(table: String, columns: String, vararg values: String): Long {
        val placeholders = values.joinToString(",") { "?" }
        db().prepareStatement("INSERT INTO $table ($columns) VALUES ($placeholders)").use { ps ->
            values.forEachIndexed { i, v -> ps.setString(i + 1, v) }
            ps.executeUpdate()
        }
        db().createStatement().use { stmt ->
            stmt.executeQuery("SELECT last_insert_rowid()").use { rs ->
                if (rs.next()) return rs.getLong(1)
            }
        }
        return -1
    }

    private fun update(table: String, setClause: String, vararg values: Any?): Boolean {
        val sql = "UPDATE $table SET $setClause WHERE id = ?"
        db().prepareStatement(sql).use { ps ->
            values.forEachIndexed { i, v ->
                if (v is Long) ps.setLong(i + 1, v)
                else ps.setString(i + 1, v?.toString() ?: "")
            }
            return ps.executeUpdate() > 0
        }
    }

    private fun query(sql: String): List<Map<String, String>> {
        val results = mutableListOf<Map<String, String>>()
        db().createStatement().use { stmt ->
            stmt.executeQuery(sql).use { rs ->
                while (rs.next()) {
                    val map = mutableMapOf<String, String>()
                    for (i in 1..rs.metaData.columnCount) map[rs.metaData.getColumnName(i)] = rs.getString(i) ?: ""
                    results.add(map)
                }
            }
        }
        return results
    }

    private fun delete(table: String, id: Long): Boolean {
        db().prepareStatement("DELETE FROM $table WHERE id = ?").use { ps ->
            ps.setLong(1, id)
            return ps.executeUpdate() > 0
        }
    }

    fun close() { conn?.close(); conn = null }
}

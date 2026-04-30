package com.memoamber.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopDatabaseManager
import com.memoamber.desktop.data.DesktopSecurityManager

@Composable
fun VaultScreen(db: DesktopDatabaseManager, security: DesktopSecurityManager, dek: ByteArray) {
    var items by remember { mutableStateOf(db.getAllVaultItems()) }
    var showDialog by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf(-1L) }
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 28.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("密码保险箱", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Text("共 ${items.size} 条记录", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            FilledTonalButton(
                onClick = { editId = -1L; title = ""; username = ""; password = ""; url = ""; notes = ""; showDialog = true },
                shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Filled.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("添加")
            }
        }

        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Lock, null, Modifier.size(40.dp), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("密码箱为空", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(items) { item ->
                    VaultItemCard(
                        item = item,
                        onEdit = {
                            editId = item["id"]?.toLongOrNull() ?: -1L
                            title = item["title"] ?: ""
                            username = item["username"] ?: ""
                            password = "" // 安全原因不回显
                            url = item["url"] ?: ""
                            notes = item["notes"] ?: ""
                            showDialog = true
                        },
                        onDelete = {
                            item["id"]?.toLongOrNull()?.let { id -> db.deleteVaultItem(id); items = db.getAllVaultItems() }
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        val isEdit = editId > 0
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text(if (isEdit) "编辑密码" else "添加密码", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("用户名") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(
                        value = password, onValueChange = { password = it }, label = { Text(if (isEdit) "新密码（留空则不变）" else "密码") },
                        visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("网址") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("备注") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isEdit) {
                            // 如果密码为空，用旧的加密密码
                            val encPwd = if (password.isNotBlank()) security.encryptString(password, dek) else (items.find { it["id"] == editId.toString() }?.get("encrypted_password") ?: "")
                            db.updateVaultItem(editId, title, username, encPwd, url, notes)
                        } else {
                            val encPwd = security.encryptString(password, dek)
                            db.insertVaultItem(title, username, encPwd, url, notes)
                        }
                        items = db.getAllVaultItems(); showDialog = false
                    },
                    enabled = title.isNotBlank() && (isEdit || password.isNotBlank()),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(if (isEdit) "保存修改" else "保存") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } }
        )
    }
}

@Composable
fun VaultItemCard(item: Map<String, String>, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.VpnKey, null, Modifier.size(22.dp), MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item["title"] ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(item["username"] ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (item["url"]?.isNotEmpty() == true) Text(item["url"] ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, "编辑", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) }
                IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, "删除", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)) }
            }
        }
    }
}

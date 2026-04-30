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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopDatabaseManager
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WillScreen(db: DesktopDatabaseManager) {
    var messages by remember { mutableStateOf(db.getAllMessages()) }
    var showDialog by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf(-1L) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }
    var sendMethod by remember { mutableStateOf("none") }
    var scheduledAt by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 28.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("给谁的留言", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Text("共 ${messages.size} 条留言", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            FilledTonalButton(
                onClick = {
                    editId = -1L; title = ""; content = ""; recipient = ""; recipientEmail = ""
                    sendMethod = "none"; scheduledAt = ""
                    showDialog = true
                },
                shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Filled.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("新建留言")
            }
        }

        if (messages.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.EditNote, null, Modifier.size(40.dp), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("还没有留言", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("留下你的嘱托和寄语", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(messages) { msg ->
                    MessageCard(
                        message = msg,
                        onEdit = {
                            editId = msg["id"]?.toLongOrNull() ?: -1L
                            title = msg["title"] ?: ""
                            content = msg["content"] ?: ""
                            recipient = msg["recipient"] ?: ""
                            recipientEmail = msg["recipient_email"] ?: ""
                            sendMethod = msg["send_method"] ?: "none"
                            scheduledAt = msg["scheduled_at"] ?: ""
                            showDialog = true
                        },
                        onDelete = {
                            msg["id"]?.toLongOrNull()?.let { id -> db.deleteMessage(id); messages = db.getAllMessages() }
                        }
                    )
                }
            }
        }
    }

    // 新建/编辑留言 Dialog
    if (showDialog) {
        val isEdit = editId > 0
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text(if (isEdit) "编辑留言" else "新建留言", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("标题") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("留言内容") }, modifier = Modifier.fillMaxWidth().height(150.dp), maxLines = 8, shape = RoundedCornerShape(12.dp))

                    Divider(modifier = Modifier.padding(vertical = 2.dp))
                    Text("收件人信息", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(value = recipient, onValueChange = { recipient = it }, label = { Text("收件人姓名") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = recipientEmail, onValueChange = { recipientEmail = it }, label = { Text("收件人邮箱") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    Divider(modifier = Modifier.padding(vertical = 2.dp))
                    Text("发送设置", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)

                    var sendExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = sendExpanded, onExpandedChange = { sendExpanded = it }) {
                        OutlinedTextField(
                            value = when (sendMethod) { "email_now" -> "立即发送邮件"; "email_scheduled" -> "定时发送邮件"; "manual" -> "手动发送"; else -> "不发送" },
                            onValueChange = {}, readOnly = true, label = { Text("发送方式") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sendExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = sendExpanded, onDismissRequest = { sendExpanded = false }) {
                            DropdownMenuItem(text = { Text("不发送（仅保存）") }, onClick = { sendMethod = "none"; sendExpanded = false })
                            DropdownMenuItem(text = { Text("手动发送") }, onClick = { sendMethod = "manual"; sendExpanded = false })
                            DropdownMenuItem(text = { Text("立即发送邮件") }, onClick = { sendMethod = "email_now"; sendExpanded = false })
                            DropdownMenuItem(text = { Text("定时发送邮件") }, onClick = { sendMethod = "email_scheduled"; sendExpanded = false })
                        }
                    }

                    // 定时发送 - 日期时间选择
                    if (sendMethod == "email_scheduled") {
                        var showDatePicker by remember { mutableStateOf(false) }
                        val now = LocalDateTime.now()
                        var pYear by remember { mutableIntStateOf(scheduledAt.parseField(0, 4, now.year)) }
                        var pMonth by remember { mutableIntStateOf(scheduledAt.parseField(5, 7, now.monthValue)) }
                        var pDay by remember { mutableIntStateOf(scheduledAt.parseField(8, 10, now.dayOfMonth)) }
                        var pHour by remember { mutableIntStateOf(scheduledAt.parseField(11, 13, now.hour)) }
                        var pMin by remember { mutableIntStateOf(scheduledAt.parseField(14, 16, now.minute)) }

                        OutlinedTextField(
                            value = if (scheduledAt.isNotBlank()) scheduledAt else "点击右侧图标选择时间",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("发送时间") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Filled.CalendarMonth, "选择时间")
                                }
                            }
                        )

                        if (showDatePicker) {
                            AlertDialog(
                                onDismissRequest = { showDatePicker = false },
                                shape = RoundedCornerShape(20.dp),
                                title = { Text("选择发送时间", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        // 年
                                        Text("年份", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        SpinRow(pYear, "年", { pYear = (pYear - 1).coerceAtLeast(2024) }, { pYear = (pYear + 1).coerceAtMost(2100) })
                                        // 月
                                        Text("月份", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        SpinRow(pMonth, "月", { pMonth = if (pMonth == 1) 12 else pMonth - 1 }, { pMonth = if (pMonth == 12) 1 else pMonth + 1 })
                                        // 日
                                        Text("日期", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        SpinRow(pDay, "日", { pDay = (pDay - 1).coerceAtLeast(1) }, { pDay = (pDay + 1).coerceAtMost(31) })
                                        Divider()
                                        // 时分
                                        Text("时间", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                                            IconButton(onClick = { pHour = if (pHour == 0) 23 else pHour - 1 }) { Icon(Icons.Filled.KeyboardArrowLeft, null) }
                                            Text(String.format("%02d", pHour), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.width(48.dp).wrapContentSize(Alignment.Center))
                                            Text(":", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                                            Text(String.format("%02d", pMin), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.width(48.dp).wrapContentSize(Alignment.Center))
                                            IconButton(onClick = { pHour = if (pHour == 23) 0 else pHour + 1 }) { Icon(Icons.Filled.KeyboardArrowRight, null) }
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                            IconButton(onClick = { pMin = if (pMin < 5) 55 else pMin - 5 }) { Icon(Icons.Filled.KeyboardArrowLeft, null) }
                                            Spacer(Modifier.width(72.dp))
                                            IconButton(onClick = { pMin = if (pMin >= 55) 0 else pMin + 5 }) { Icon(Icons.Filled.KeyboardArrowRight, null) }
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        scheduledAt = String.format("%04d-%02d-%02d %02d:%02d", pYear, pMonth, pDay, pHour, pMin)
                                        showDatePicker = false
                                    }, shape = RoundedCornerShape(12.dp)) { Text("确定") }
                                },
                                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("取消") } }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            if (isEdit) db.updateMessage(editId, title, content, recipient, recipientEmail, sendMethod, scheduledAt)
                            else db.insertMessage(title, content, recipient, recipientEmail, sendMethod, scheduledAt)
                            messages = db.getAllMessages(); showDialog = false
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(if (isEdit) "保存修改" else "保存") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("取消") } }
        )
    }
}

// 辅助：解析 scheduledAt 字符串中的字段
private fun String?.parseField(start: Int, end: Int, default: Int): Int {
    if (this.isNullOrBlank() || this.length < end) return default
    return this.substring(start, end).toIntOrNull() ?: default
}

// 辅助：SpinRow（左右箭头+中间文字）
@Composable
private fun SpinRow(value: Int, suffix: String, onDec: () -> Unit, onInc: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = onDec) { Icon(Icons.Filled.KeyboardArrowLeft, null) }
        Text("$value $suffix", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.width(72.dp).wrapContentSize(Alignment.Center))
        IconButton(onClick = onInc) { Icon(Icons.Filled.KeyboardArrowRight, null) }
    }
}

@Composable
fun MessageCard(message: Map<String, String>, onEdit: () -> Unit, onDelete: () -> Unit) {
    val sendMethod = message["send_method"] ?: "none"
    val scheduledAt = message["scheduled_at"] ?: ""

    val statusLabel = when {
        sendMethod == "email_scheduled" && scheduledAt.isNotEmpty() -> "定时 $scheduledAt"
        sendMethod == "email_now" -> "待发送"
        sendMethod == "manual" -> "手动发送"
        else -> "仅保存"
    }
    val statusColor = when (sendMethod) {
        "email_scheduled", "email_now" -> MaterialTheme.colorScheme.primaryContainer
        "manual" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val statusTextColor = when (sendMethod) {
        "email_scheduled", "email_now" -> MaterialTheme.colorScheme.primary
        "manual" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(message["title"] ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor) {
                    Text(statusLabel, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = statusTextColor, fontWeight = FontWeight.Medium)
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Edit, "编辑", Modifier.size(16.dp), MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, "删除", Modifier.size(16.dp), MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.width(3.dp).height(40.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)))
                Spacer(Modifier.width(12.dp))
                Text(message["content"] ?: "", style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (message["recipient"]?.isNotEmpty() == true || message["recipient_email"]?.isNotEmpty() == true) {
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, null, Modifier.size(14.dp), MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    val recipientText = buildString {
                        append(message["recipient"] ?: "")
                        if (!message["recipient_email"].isNullOrEmpty()) {
                            if (isNotEmpty()) append(" · ")
                            append(message["recipient_email"])
                        }
                    }
                    Text(recipientText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

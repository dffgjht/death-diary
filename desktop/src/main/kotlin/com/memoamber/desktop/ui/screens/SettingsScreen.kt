package com.memoamber.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.memoamber.desktop.data.DesktopDatabaseManager
import com.memoamber.desktop.data.DesktopSecurityManager
import java.io.File

@Composable
fun SettingsScreen(security: DesktopSecurityManager, db: DesktopDatabaseManager, onLock: () -> Unit) {
    val dataDir = File(System.getProperty("user.home"), ".memoamber")
    var showResetDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }

    // 邮箱配置
    var smtpHost by remember { mutableStateOf("") }
    var smtpPort by remember { mutableStateOf("587") }
    var smtpUser by remember { mutableStateOf("") }
    var smtpPass by remember { mutableStateOf("") }
    var senderName by remember { mutableStateOf("") }
    var useTls by remember { mutableStateOf(true) }
    var emailConfigLoaded by remember { mutableStateOf(false) }

    // 延迟加载邮箱配置
    LaunchedEffect(Unit) {
        val config = db.getEmailConfig()
        if (config != null) {
            smtpHost = config["smtp_host"] ?: ""
            smtpPort = config["smtp_port"] ?: "587"
            smtpUser = config["smtp_user"] ?: ""
            smtpPass = config["smtp_pass_encrypted"] ?: "" // 简化：直接存储
            senderName = config["sender_name"] ?: ""
            useTls = config["use_tls"] != "0"
            emailConfigLoaded = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("设置", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))

        // 邮箱配置
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Email, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("邮箱配置", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                        Text("配置 SMTP 邮箱用于发送留言", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                    if (smtpUser.isNotEmpty()) {
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                            Text("已配置", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                FilledTonalButton(
                    onClick = { showEmailDialog = true },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Icon(Icons.Filled.Settings, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (smtpUser.isNotEmpty()) "修改邮箱配置" else "配置邮箱")
                }
            }
        }

        // 数据存储
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.tertiaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Storage, null, Modifier.size(20.dp), MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("数据存储", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
                Spacer(Modifier.height(12.dp))
                Text("数据目录：${dataDir.absolutePath}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("数据库：${File(dataDir, "data.db").absolutePath}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        }

        // 安全
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Shield, null, Modifier.size(20.dp), MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("安全", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { showResetDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                    Icon(Icons.Filled.Key, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("重置主密码")
                }
            }
        }

        // 会话
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Lock, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("会话", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = onLock, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                    Icon(Icons.Filled.Lock, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("立即锁定")
                }
            }
        }

        // 关于
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Info, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("关于", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
                Spacer(Modifier.height(12.dp))
                Text("记忆琥珀 MemoAmber v1.5.0", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Text("琥珀封存记忆，守护你的珍贵时光", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        }
    }

    // 重置密码确认
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false }, shape = RoundedCornerShape(20.dp),
            title = { Text("重置主密码") }, text = { Text("此操作将清除所有加密数据并重置密码。确定继续吗？") },
            confirmButton = { Button(onClick = { showResetDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), shape = RoundedCornerShape(12.dp)) { Text("确认重置") } },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("取消") } }
        )
    }

    // 邮箱配置 Dialog
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("邮箱 SMTP 配置", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("配置邮箱用于发送定时留言。常用邮箱 SMTP 设置：", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("QQ邮箱: smtp.qq.com:587 | 163邮箱: smtp.163.com:465 | Gmail: smtp.gmail.com:587", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))

                    OutlinedTextField(value = smtpHost, onValueChange = { smtpHost = it }, label = { Text("SMTP 服务器") }, placeholder = { Text("smtp.qq.com") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = smtpPort, onValueChange = { smtpPort = it }, label = { Text("端口") }, placeholder = { Text("587") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = smtpUser, onValueChange = { smtpUser = it }, label = { Text("邮箱账号") }, placeholder = { Text("your@email.com") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = smtpPass, onValueChange = { smtpPass = it }, label = { Text("授权码/密码") }, visualTransformation = PasswordVisualTransformation(), placeholder = { Text("不是邮箱登录密码，是SMTP授权码") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = senderName, onValueChange = { senderName = it }, label = { Text("发件人名称") }, placeholder = { Text("记忆琥珀") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = useTls, onCheckedChange = { useTls = it })
                        Spacer(Modifier.width(8.dp))
                        Text("使用 TLS 加密", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        db.saveEmailConfig(smtpHost, smtpPort, smtpUser, smtpPass, senderName, useTls)
                        showEmailDialog = false
                    },
                    enabled = smtpHost.isNotBlank() && smtpUser.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("保存配置") }
            },
            dismissButton = { TextButton(onClick = { showEmailDialog = false }) { Text("取消") } }
        )
    }
}

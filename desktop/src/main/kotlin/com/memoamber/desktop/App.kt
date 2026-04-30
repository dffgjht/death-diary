package com.memoamber.desktop

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memoamber.desktop.ui.screens.*
import com.memoamber.desktop.ui.theme.MemoAmberTheme
import com.memoamber.desktop.ui.theme.SidebarBackground
import com.memoamber.desktop.ui.theme.SidebarOnBackground
import com.memoamber.desktop.ui.theme.SidebarSelectedItem
import com.memoamber.desktop.data.DesktopSecurityManager
import com.memoamber.desktop.data.DesktopDatabaseManager

@Composable
fun MemoAmberApp() {
    val securityManager = remember { DesktopSecurityManager() }
    val databaseManager = remember { DesktopDatabaseManager() }
    val currentRoute = remember { mutableStateOf("lock") }
    val sessionPassword = remember { mutableStateOf("") }

    MemoAmberTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AnimatedContent(
                targetState = currentRoute.value,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) }
            ) { route ->
                when (route) {
                    "lock" -> LockScreen(
                        securityManager = securityManager,
                        onAuthSuccess = { pwd ->
                            sessionPassword.value = pwd
                            currentRoute.value = "home"
                        }
                    )
                    "home" -> MainScreen(
                        securityManager = securityManager,
                        databaseManager = databaseManager,
                        dek = remember(sessionPassword.value) {
                            if (sessionPassword.value.isNotEmpty()) securityManager.getDataEncryptionKey(sessionPassword.value) else ByteArray(0)
                        },
                        onLock = {
                            sessionPassword.value = ""
                            currentRoute.value = "lock"
                        }
                    )
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MainScreen(
    securityManager: DesktopSecurityManager,
    databaseManager: DesktopDatabaseManager,
    dek: ByteArray,
    onLock: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        NavItem("主页", Icons.Filled.Home, Icons.Outlined.Home),
        NavItem("日记", Icons.Filled.Book, Icons.Outlined.Book),
        NavItem("密码箱", Icons.Filled.Lock, Icons.Outlined.Lock),
        NavItem("留言", Icons.Filled.EditNote, Icons.Outlined.EditNote),
        NavItem("设置", Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // ===== 左侧深色侧边导航 =====
        Column(
            modifier = Modifier
                .width(220.dp)
                .fillMaxHeight()
                .background(SidebarBackground)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo 区域
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = SidebarSelectedItem
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "记忆琥珀",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SidebarOnBackground
            )
            Text(
                "MemoAmber",
                fontSize = 12.sp,
                color = SidebarOnBackground.copy(alpha = 0.5f)
            )

            Spacer(Modifier.height(32.dp))

            // 导航项
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                val bgColor = if (isSelected) SidebarSelectedItem.copy(alpha = 0.2f) else Color.Transparent
                val contentColor = if (isSelected) SidebarSelectedItem else SidebarOnBackground.copy(alpha = 0.7f)
                val icon = if (isSelected) item.selectedIcon else item.unselectedIcon

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp),
                        tint = contentColor
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        item.label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = contentColor
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // 底部锁定按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLock() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Lock,
                    "锁定",
                    modifier = Modifier.size(22.dp),
                    tint = SidebarOnBackground.copy(alpha = 0.5f)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "锁定",
                    fontSize = 14.sp,
                    color = SidebarOnBackground.copy(alpha = 0.5f)
                )
            }
        }

        // ===== 右侧内容区 =====
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) }
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(onNavigate = { selectedTab = it })
                    1 -> DiaryScreen(databaseManager)
                    2 -> VaultScreen(databaseManager, securityManager, dek)
                    3 -> WillScreen(databaseManager)
                    4 -> SettingsScreen(securityManager, databaseManager, onLock)
                }
            }
        }
    }
}

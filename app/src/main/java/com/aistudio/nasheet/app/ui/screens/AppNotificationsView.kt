package com.aistudio.nasheet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.nasheet.app.data.database.AppNotification
import com.aistudio.nasheet.app.ui.theme.*

// 3. APPL NOTIFICATIONS SCREEN (الإشعارات والتوصيات الذكية)
@Composable
fun AppNotificationsView(
    notifications: List<AppNotification>,
    onMarkAsRead: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: الكل, 1: الأولياء, 2: توصيات ذكية

    val filteredNotifications = remember(notifications, selectedTab) {
        when (selectedTab) {
            1 -> notifications.filter { it.type == "parent" || it.type == "child" }
            2 -> notifications.filter { it.type == "recommendation" }
            else -> notifications
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "التنبيهات والتوصيات الذكية 🔔",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Custom Tab Rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabItem(title = "الكل", isSelected = selectedTab == 0, onClick = { selectedTab = 0 })
                    TabItem(title = "توجيهات الكبار", isSelected = selectedTab == 1, onClick = { selectedTab = 1 })
                    TabItem(title = "توصيات ذكية ✨", isSelected = selectedTab == 2, onClick = { selectedTab = 2 })
                }
            }
        },
        containerColor = CreamBackground
    ) { innerPadding ->
        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد تنبيهات أو توصيات في هذا القسم حالياً. 🎉",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNotifications) { item ->
                    val isRead = item.isRead
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isRead) Color.White else Color(0xFFF0FDF4)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isRead) 0.5.dp else 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (item.type) {
                                                "recommendation" -> SoftTeal.copy(alpha = 0.15f)
                                                "parent" -> CoralWarm.copy(alpha = 0.15f)
                                                else -> PastelBlue
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = when (item.type) {
                                            "recommendation" -> "توصية ذكية 👾"
                                            "parent" -> "توجيه ولي الأمر 👨‍👩‍👦"
                                            else -> "عام 📢"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = when (item.type) {
                                            "recommendation" -> SoftTeal
                                            "parent" -> CoralWarm
                                            else -> CharcoalText
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (!isRead) {
                                    IconButton(onClick = { onMarkAsRead(item.id) }) {
                                        Icon(Icons.Rounded.Check, "Mark as read", tint = SoftTeal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CharcoalText.copy(alpha = 0.9f),
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TabItem(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 15.sp
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(3.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}

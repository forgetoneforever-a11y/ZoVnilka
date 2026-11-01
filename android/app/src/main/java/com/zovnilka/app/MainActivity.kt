package com.zovnilka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppBlack = Color(0xFF050505)
private val Glass = Color(0xCC171717)
private val SoftWhite = Color(0xFFF4F4F4)
private val Muted = Color(0xFF858585)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZoVnilkaRoot() }
    }
}

private data class ChatItem(
    val title: String,
    val preview: String,
    val time: String,
    val initials: String,
    val unread: Int = 0
)

@Composable
private fun ZoVnilkaRoot() {
    var openedChat by remember { mutableStateOf<ChatItem?>(null) }
    val chats = listOf(
        ChatItem("Избранное", "Сохранённые сообщения", "Закреплено", "★"),
        ChatItem("Александр", "Привет! Как дела?", "12:41", "А", 2),
        ChatItem("Команда ZoVnilka", "Первый макет уже готов", "12:30", "Z", 4),
        ChatItem("Мария", "Отправила фотографию", "11:52", "М")
    )

    MaterialTheme(colorScheme = darkColorScheme(
        background = AppBlack,
        surface = Color(0xFF151515),
        primary = SoftWhite,
        onBackground = SoftWhite,
        onSurface = SoftWhite
    )) {
        Surface(Modifier.fillMaxSize(), color = AppBlack) {
            AnimatedContent(targetState = openedChat, label = "navigation") { chat ->
                if (chat == null) ChatListScreen(chats) { openedChat = it }
                else ConversationScreen(chat) { openedChat = null }
            }
        }
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(Color(0xCC242424), Color(0x99121212))))
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun GlassAction(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        Modifier.size(44.dp).clip(RoundedCornerShape(15.dp))
            .background(Glass).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, contentDescription = null, tint = SoftWhite) }
}

@Composable
private fun ChatListScreen(chats: List<ChatItem>, onOpen: (ChatItem) -> Unit) {
    Column(
        Modifier.fillMaxSize().background(AppBlack).statusBarsPadding()
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ZoVnilka", color = SoftWhite, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text("Твоё пространство общения", color = Muted, fontSize = 12.sp)
            }
            GlassAction(Icons.Default.Search) { }
            Spacer(Modifier.width(8.dp))
            GlassAction(Icons.Default.Edit) { }
        }
        Spacer(Modifier.height(24.dp))
        GlassCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ChatBubble, null, tint = SoftWhite)
                Spacer(Modifier.width(10.dp))
                Text("Сообщения", color = SoftWhite, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Spacer(Modifier.weight(1f))
                Text("BETA", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(chats) { chat ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        .background(Color(0x99141414))
                        .clickable { onOpen(chat) }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF292929)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(chat.initials, color = SoftWhite, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(chat.title, color = SoftWhite, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(chat.preview, color = Muted, fontSize = 12.sp, maxLines = 1)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(chat.time, color = Muted, fontSize = 10.sp)
                        if (chat.unread > 0) {
                            Box(Modifier.clip(CircleShape).background(Color(0xFF333333)).padding(horizontal = 7.dp, vertical = 3.dp)) {
                                Text(chat.unread.toString(), color = SoftWhite, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp))
                .background(Color(0xCC151515)).padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(Icons.Default.ChatBubble, null, tint = SoftWhite)
            Icon(Icons.Default.People, null, tint = Muted)
            Icon(Icons.Default.Call, null, tint = Muted)
            Icon(Icons.Default.Settings, null, tint = Muted)
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun ConversationScreen(chat: ChatItem, onBack: () -> Unit) {
    var draft by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().background(AppBlack).statusBarsPadding().padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlassAction(Icons.Default.ArrowBack, onBack)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(chat.title, color = SoftWhite, fontWeight = FontWeight.SemiBold)
                Text("Профиль и информация", color = Muted, fontSize = 11.sp)
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            GlassCard(Modifier.fillMaxWidth()) {
                Text("Это начало вашей истории", color = SoftWhite, fontWeight = FontWeight.SemiBold)
                Text("Подключение сообщений появится на следующем этапе разработки.", color = Muted, fontSize = 12.sp)
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Glass).padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, null, tint = Muted, modifier = Modifier.padding(8.dp))
            TextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Сообщение…", color = Muted) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = { /* Message transport is added in the next milestone. */ }) {
                Icon(Icons.Default.Send, null, tint = SoftWhite)
            }
        }
    }
}

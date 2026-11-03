package com.zovnilka.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

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
    var chats by remember {
        mutableStateOf(
            listOf(
                ChatItem("Избранное", "Сохранённые сообщения", "Закреплено", "★"),
                ChatItem("Александр", "Привет! Как дела?", "12:41", "А", 2),
                ChatItem("Команда ZoVnilka", "Первый макет уже готов", "12:30", "Z", 4),
                ChatItem("Мария", "Отправила фотографию", "11:52", "М")
            )
        )
    }
    var archived by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
    var showArchive by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(
        background = AppBlack,
        surface = Color(0xFF151515),
        primary = SoftWhite,
        onBackground = SoftWhite,
        onSurface = SoftWhite
    )) {
        Surface(Modifier.fillMaxSize(), color = AppBlack) {
            AnimatedContent(targetState = openedChat, label = "navigation") { chat ->
                if (chat == null) {
                    ChatListScreen(
                        chats = if (showArchive) archived else chats,
                        isArchive = showArchive,
                        archivedCount = archived.size,
                        onOpen = { openedChat = it },
                        onToggleArchive = { showArchive = !showArchive },
                        onMarkRead = { item ->
                            chats = chats.map { if (it == item) it.copy(unread = 0) else it }
                        },
                        onArchive = { item ->
                            chats = chats.filterNot { it == item }
                            archived = archived + item
                        },
                        onRestore = { item ->
                            archived = archived.filterNot { it == item }
                            chats = chats + item
                        }
                    )
                } else {
                    ConversationScreen(chat) { openedChat = null }
                }
            }
        }
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(22.dp))
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
private fun ChatListScreen(
    chats: List<ChatItem>,
    isArchive: Boolean,
    archivedCount: Int,
    onOpen: (ChatItem) -> Unit,
    onToggleArchive: () -> Unit,
    onMarkRead: (ChatItem) -> Unit,
    onArchive: (ChatItem) -> Unit,
    onRestore: (ChatItem) -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(AppBlack).statusBarsPadding()
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ZoVnilka", color = SoftWhite, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text(if (isArchive) "Архив чатов" else "Твоё пространство общения", color = Muted, fontSize = 12.sp)
            }
            GlassAction(Icons.Default.Search) { }
            Spacer(Modifier.width(8.dp))
            GlassAction(Icons.Default.Edit) { }
        }
        Spacer(Modifier.height(20.dp))
        if (!isArchive) {
            GlassCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubble, null, tint = SoftWhite)
                    Spacer(Modifier.width(10.dp))
                    Text("Сообщения", color = SoftWhite, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                    Spacer(Modifier.weight(1f))
                    Text("BETA", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF171717)).clickable(onClick = onToggleArchive).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(if (isArchive) Icons.Default.ArrowBack else Icons.Default.Archive, null, tint = SoftWhite)
            Spacer(Modifier.width(10.dp))
            Text(if (isArchive) "Вернуться к чатам" else "Архив", color = SoftWhite, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Text(if (isArchive) "Назад" else archivedCount.toString(), color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(chats, key = { it.title }) { chat ->
                SwipeChatRow(
                    chat = chat,
                    archived = isArchive,
                    onClick = { onOpen(chat) },
                    onSwipeRight = { if (!isArchive) onMarkRead(chat) },
                    onSwipeLeft = { if (isArchive) onRestore(chat) else onArchive(chat) }
                )
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
private fun SwipeChatRow(
    chat: ChatItem,
    archived: Boolean,
    onClick: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeLeft: () -> Unit
) {
    var dragX by remember(chat.title) { mutableFloatStateOf(0f) }
    var actionLabel by remember { mutableStateOf("") }
    val animatedX by animateFloatAsState(dragX, label = "chat-swipe")
    val threshold = 105f

    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 76.dp)
                .background(if (dragX > 0) Color(0xFF26382D) else Color(0xFF292929))
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (dragX > 0) Arrangement.Start else Arrangement.End
        ) {
            Icon(
                if (archived) Icons.Default.Unarchive
                else if (dragX > 0) Icons.Default.MarkChatRead else Icons.Default.Archive,
                contentDescription = null,
                tint = SoftWhite
            )
            Spacer(Modifier.width(8.dp))
            Text(
                when {
                    archived -> "Вернуть"
                    dragX > 0 -> "Прочитано"
                    else -> "В архив"
                },
                color = SoftWhite,
                fontSize = 12.sp
            )
        }
        Row(
            Modifier.fillMaxWidth()
                .offset { IntOffset(animatedX.roundToInt(), 0) }
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF141414))
                .pointerInput(chat.title, archived) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, amount ->
                            change.consume()
                            dragX = (dragX + amount).coerceIn(-150f, 150f)
                        },
                        onDragEnd = {
                            when {
                                dragX > threshold -> onSwipeRight()
                                dragX < -threshold -> onSwipeLeft()
                            }
                            dragX = 0f
                        },
                        onDragCancel = { dragX = 0f }
                    )
                }
                .clickable(onClick = onClick)
                .padding(12.dp),
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
                if (chat.unread > 0 && !archived) {
                    Box(Modifier.clip(CircleShape).background(Color(0xFF333333)).padding(horizontal = 7.dp, vertical = 3.dp)) {
                        Text(chat.unread.toString(), color = SoftWhite, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationScreen(chat: ChatItem, onBack: () -> Unit) {
    var draft by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<String?>(null) }
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
                Text("Подсказка: в будущем свайп по сообщению вправо будет отвечать на него.", color = Muted, fontSize = 11.sp)
            }
        }
        if (replyTo != null) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    .clip(RoundedCornerShape(12.dp)).background(Color(0xFF222222)).padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ответ: $replyTo", color = SoftWhite, fontSize = 12.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = { replyTo = null }) {
                    Icon(Icons.Default.Close, null, tint = Muted)
                }
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
            IconButton(onClick = { /* Message transport is not connected yet. */ }) {
                Icon(Icons.Default.Send, null, tint = SoftWhite)
            }
        }
    }
}

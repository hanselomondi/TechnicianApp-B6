package com.example.clientapp.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clientapp.NavDestinations
import com.example.clientapp.models.ChatMessage
import com.example.clientapp.ui.view_models.SpecificChatViewModel
import com.example.clientapp.ui.view_models.ChatListViewModel

@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatListViewModel = viewModel()
) {
    val chatList by viewModel.chatList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Light background color
            .padding(8.dp) // General padding for the screen
    ) {
        Text(
            text = "Chats",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between items
        ) {
            items(chatList.toList()) { (techID, latestMessageData) ->
                ChatRow(techID, latestMessageData) {
                    navController.navigate( NavDestinations.SPECIFIC_CHAT.name + "/$techID")
                }
            }
        }
    }
}

@Composable
fun ChatRow(
    techId: String,
    latestMessageData: Map<String, Any>,
    onClick: () -> Unit
) {
    val message = latestMessageData["message"] as? String ?: ""
    val time = latestMessageData["time"] as? com.google.firebase.Timestamp
    val formattedTime = time?.toDate()?.toString() ?: ""

    // TODO logic flawed from Firestore even. Fix later
    val displayText = if (message.startsWith("Client_")) {
        "You: $message"
    } else {
        message
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White, RoundedCornerShape(8.dp)) // Rounded corners for chat row
            .shadow(1.dp, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = techId,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayText,
                style = TextStyle(fontSize = 14.sp, color = Color(0xFF666666)),
                maxLines = 1, // Limit to one line for better appearance
                overflow = TextOverflow.Ellipsis // Add ellipsis if text is too long
            )
        }

        Text(
            text = formattedTime,
            style = TextStyle(fontSize = 12.sp, color = Color(0xFF999999)),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}


@Composable
fun SpecificChatScreen(
    clientID: String,
    techID: String?,
    viewModel: SpecificChatViewModel = viewModel()
) {
    val chatMessages by viewModel.chatMessages.collectAsState()

    LaunchedEffect(techID) {
        techID?.let {
            viewModel.fetchChatMessages(techID = it, clientID = clientID)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Take up remaining space
                .fillMaxWidth(),
            reverseLayout = false // Ensure messages are in chronological order
        ) {
            items(chatMessages) { message ->
                MessageBubble(message)
            }
        }

        // Input field and send button for new messages
        MessageInput(onSend = { message ->
            techID?.let {
                viewModel.sendMessage(techId = it, clientId = clientID, message = message)
            }
        })
    }
}


@Composable
fun MessageBubble(message: ChatMessage) {
    // Choose bubble color based on the sender
    val messageColor = if (message.isFromTech) Color.Gray else Color.Blue

    // Format the timestamp for better readability
    val formattedTime = remember(message.timestamp) {
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        sdf.format(message.timestamp.toDate())
    }

    // Align messages to the start or end
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (message.isFromTech) Arrangement.Start else Arrangement.End
    ) {
        // Column to stack message content and timestamp
        Column(
            modifier = Modifier
                .background(messageColor, RoundedCornerShape(8.dp))
                .padding(8.dp)
                .wrapContentWidth()
        ) {
            // Message Text
            Text(
                text = message.content,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Timestamp Text
            Text(
                text = formattedTime,
                style = TextStyle(fontSize = 12.sp),
                color = Color.Black
            )
        }
    }
}


@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Add padding for better appearance
            .background(Color.Gray.copy(alpha = 0.1f)) // Optional: Background color for better visibility
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type a message...") },
            modifier = Modifier
                .weight(1f) // Take up available width
                .padding(end = 8.dp) // Add padding to the end for spacing with the button
        )
        Button(onClick = {
            onSend(message)
            message = ""
        }) {
            Text("Send")
        }
    }
}
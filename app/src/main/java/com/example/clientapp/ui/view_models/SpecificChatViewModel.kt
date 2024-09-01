package com.example.clientapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientapp.firebase_functions.clientCreateOrSendMessage
import com.example.clientapp.models.ChatMessage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SpecificChatViewModel : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    fun fetchChatMessages(clientID: String, techID: String) {
        viewModelScope.launch {
            // TODO take to Firestore file into a function later
            val chatCollection = FirebaseFirestore.getInstance()
                .collection("Clients")
                .document(clientID)
                .collection("Chats")
                .document(techID)

            val result = chatCollection.get().await()

            if (result.exists()) {
                val messages = result.data?.entries
                    ?.filter { it.value is Map<*, *> }
                    ?.map { entry ->
                        val messageMap = entry.value as? Map<String, Any> ?: emptyMap()
                        ChatMessage(
                            content = messageMap["message"] as? String ?: "",
                            timestamp = messageMap["time"] as Timestamp,
                            isFromTech = entry.key.startsWith("Tech_")
                        )
                    } ?: emptyList()

                // Sort messages by timestamp
                val sortedMessages = messages.sortedBy { it.timestamp }

                _chatMessages.value = sortedMessages
            } else {
                _chatMessages.value = emptyList()
            }
        }
    }

    fun sendMessage(clientId: String, techId: String, message: String) {
        viewModelScope.launch {
            clientCreateOrSendMessage(clientId = clientId, techId = techId, message = message)
            fetchChatMessages(clientID = clientId, techID = techId) // Refresh the chat messages
        }
    }
}


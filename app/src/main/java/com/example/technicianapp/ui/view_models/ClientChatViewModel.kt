package com.example.technicianapp.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicianapp.firebase_functions.techCreateOrSendMessage
import com.example.technicianapp.models.ChatMessage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClientChatViewModel : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    fun fetchChatMessages(clientId: String, techId: String) {
        viewModelScope.launch {
            val chatCollection = FirebaseFirestore.getInstance()
                .collection("Technicians")
                .document(techId)
                .collection("Chats")
                .document(clientId)

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
            techCreateOrSendMessage(clientId = clientId, techId = techId, message = message)
            fetchChatMessages(clientId = clientId, techId = techId) // Refresh the chat messages
        }
    }
}



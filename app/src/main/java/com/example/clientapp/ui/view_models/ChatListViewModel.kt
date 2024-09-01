package com.example.clientapp.ui.view_models


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientapp.firebase_functions.getChatList
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {
    private val _chatList = MutableStateFlow<List<Pair<String, Map<String, Any>>>>(emptyList())
    val chatList: StateFlow<List<Pair<String, Map<String, Any>>>> = _chatList

    init {
        fetchClientsChatList()
    }

    private fun fetchClientsChatList() {
        viewModelScope.launch {
            val result = getChatList()

            if (result.isSuccess) {
                val documents = result.getOrNull() ?: emptyList()

                val chats = documents.map { doc ->
                    Log.d("Firestore", "Document data: ${doc.data}") // Debugging log

                    // Safely extract and handle the latest message
                    val latestMessage = doc.data?.entries
                        ?.filter { it.value is Map<*, *> } // Filter entries that are maps
                        ?.maxByOrNull { entry ->
                            // Extract the time from the map
                            val messageMap = entry.value as? Map<String, Any> ?: emptyMap()

                            val timestamp = messageMap["time"] as Timestamp
                            timestamp
                        }

                    // Map to extract tech ID and message data
                    val latestMessageMap = latestMessage?.value as? Map<String, Any> ?: emptyMap()
                    doc.id to latestMessageMap
                }

                _chatList.value = chats
            } else {
                // Handle the error
            }
        }
    }
}
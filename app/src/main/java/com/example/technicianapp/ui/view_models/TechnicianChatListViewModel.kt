package com.example.technicianapp.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicianapp.firebase_functions.getClientsChatList
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TechnicianChatListViewModel : ViewModel() {
    private val _clientsChatList = MutableStateFlow<List<Pair<String, Map<String, Any>>>>(emptyList())
    val clientsChatList: StateFlow<List<Pair<String, Map<String, Any>>>> = _clientsChatList

    init {
        fetchClientsChatList(FirebaseAuth.getInstance().currentUser!!.uid)
    }

    private fun fetchClientsChatList(techId: String) {
        viewModelScope.launch {
            val result = getClientsChatList(techId)

            if (result.isSuccess) {
                val documents = result.getOrNull() ?: emptyList()

                val clientChats = documents.map { doc ->
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

                    // Map to extract client ID and message data
                    val latestMessageMap = latestMessage?.value as? Map<String, Any> ?: emptyMap()
                    doc.id to latestMessageMap
                }

                _clientsChatList.value = clientChats
            } else {
                // Handle the error
            }
        }
    }
}

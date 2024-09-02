package com.example.clientapp.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.thread.ThreadId
import com.example.clientapp.firebase_functions.recommendTechnicians
import com.example.clientapp.models.AssistantResponse
import com.example.clientapp.models.Technician
import com.example.clientapp.openai_functions.OpenAIObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(BetaOpenAI::class)
class AssistantViewModel : ViewModel() {

    // Internal state flows
    private val _prompt = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _response = MutableStateFlow(AssistantResponse())
    private val _technicians = MutableStateFlow(emptyList<Technician>())

    // Public state flows
    val prompt: StateFlow<String> = _prompt.asStateFlow()
    val response = _response.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val technicians = _technicians.asStateFlow()

    init {
        Log.d("AssistantViewModel", "AssistantViewModel created")
    }

    // Function to update the prompt
    fun updatePrompt(newPrompt: String) {
        _prompt.value = newPrompt
    }

    // Function to submit the prompt and fetch responses
    fun submitPromptFetchResponse() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("AssistantViewModel", "Prepare Assistant Start")
                OpenAIObject.prepareAssistant(_prompt.value)
                Log.d("AssistantViewModel", "Prepare Assistant End")

                // Wait for response from OpenAI
                Log.d("AssistantViewModel", "Get Response Start")
                val assistantResponse = OpenAIObject.getResponse()
                Log.d("AssistantViewModel", "Get Response End")


                //assistantResponse.content.
                _response.value = assistantResponse
            } catch (e: Exception) {
                Log.e("AssistantViewModel", "Error fetching response: ", e)
            } finally {
                _isLoading.value = false

                getTechRecommendations()
            }
        }
    }

    fun getTechRecommendations() {
        if (_response.value.inferredServices.isNotEmpty()) {
            viewModelScope.launch {
                //_isLoading.value = true

                try {
                    _technicians.value = recommendTechnicians(_response.value.inferredServices)
                    Log.d("AssistantViewModel", "Technicians: ${_technicians.value}")

                } catch (e: Exception) {
                    // TODO("Not yet implemented")
                    Log.e("AssistantViewModel", "Error fetching technicians: ", e)
                } finally {
                    //_isLoading.value = false

                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        OpenAIObject.clear()

        Log.d("AssistantViewModel", "AssistantViewModel cleared")
    }
}

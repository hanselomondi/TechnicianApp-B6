package com.example.technicianapp.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicianapp.firebase_functions.addSkillsToTechnicianFirestore
import com.example.technicianapp.firebase_functions.fetchServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SkillsViewModel : ViewModel() {
    private val _services = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val services = _services.asStateFlow()

    // Keeps track of selected skills by category
    private val _selectedSkills = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val selectedSkills = _selectedSkills.asStateFlow()

    private val _addSkillsResult = MutableStateFlow<Result<String>>(Result.failure(Exception("")))
    val addSkillsResult = _addSkillsResult.asStateFlow()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            val result = fetchServices()

            result.onSuccess { data ->
                _services.value = data
            }.onFailure { exception ->
                // Handle error (e.g., log or show a message to the user)
                Log.e("SkillsViewModel", "Error fetching services: ", exception)
            }
        }
    }

    fun updateSelectedSkills(category: String, service: String, isChecked: Boolean) {
        val currentSkills = _selectedSkills.value.toMutableMap()
        // MutableList containing the Services
        val updatedSkills = currentSkills.getOrDefault(category, emptyList()).toMutableList()

        // if it is checked and the list doesn't yet contain the service:
        if (isChecked and (updatedSkills.contains(service).not())) {
            // if the list doesn't yet contain the service
            updatedSkills.add(service)

            Log.d("SkillsViewModel", "Service $service checked")
        } else { // if it is unchecked:
            updatedSkills.remove(service)

            Log.d("SkillsViewModel", "Service $service removed")
        }

        currentSkills[category] = updatedSkills
        _selectedSkills.value = currentSkills
    }

    /*fun getSelectedSkills(): Map<String, List<String>> {
        return _selectedSkills.value
    }*/

    fun saveSelectedSkills() {
        viewModelScope.launch {
            _addSkillsResult.value = addSkillsToTechnicianFirestore(_selectedSkills.value)

            Log.d("SkillsViewModel", _addSkillsResult.value.toString())
        }
    }
}

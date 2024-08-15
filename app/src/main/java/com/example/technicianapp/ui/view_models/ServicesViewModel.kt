package com.example.technicianapp.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicianapp.firebase_functions.addServicesToTechnicianFirestore
import com.example.technicianapp.firebase_functions.fetchServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServicesViewModel : ViewModel() {
    private val _services = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val services = _services.asStateFlow()

    // Keeps track of selected skills by category
    private val _selectedServices = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val selectedServices = _selectedServices.asStateFlow()

    private val _addServicesResult = MutableStateFlow<Result<String>>(Result.failure(Exception("")))
    val addServicesResult = _addServicesResult.asStateFlow()

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

    fun updateSelectedServices(category: String, service: String, isChecked: Boolean) {
        val currentServices = _selectedServices.value.toMutableMap()
        // MutableList containing the Services
        val updatedServices = currentServices.getOrDefault(category, emptyList()).toMutableList()

        // if it is checked and the list doesn't yet contain the service:
        if (isChecked and (updatedServices.contains(service).not())) {
            // if the list doesn't yet contain the service
            updatedServices.add(service)

            Log.d("ServicesViewModel", "Service $service checked")
        } else { // if it is unchecked:
            updatedServices.remove(service)

            Log.d("ServicesViewModel", "Service $service removed")
        }

        currentServices[category] = updatedServices
        _selectedServices.value = currentServices
    }

    /*fun getSelectedSkills(): Map<String, List<String>> {
        return _selectedSkills.value
    }*/

    fun saveSelectedSkills() {
        viewModelScope.launch {
            _addServicesResult.value = addServicesToTechnicianFirestore(_selectedServices.value)

            Log.d("ServicesViewModel", _addServicesResult.value.toString())
        }
    }
}

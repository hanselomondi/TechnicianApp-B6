package com.example.technicianapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.technicianapp.ui.view_models.ServicesViewModel

@Composable
fun ServicesSelectionScreen(
    navController: NavController,
    viewModel: ServicesViewModel = viewModel(),
) {
    val services by viewModel.services.collectAsState()
    val expandedCategory = remember { mutableStateOf<String?>(null) }
    val selectedServices by viewModel.selectedServices.collectAsState()
    val servicesAdded by viewModel.addServicesResult.collectAsState()

    LazyColumn {
        services.forEach { (category, servicesList) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            expandedCategory.value =
                                if (expandedCategory.value == category) null else category
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = category, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        if (expandedCategory.value == category) {
                            servicesList.forEach { service ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable(enabled = false) {},
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedServices[category]?.contains(service) == true,
                                        onCheckedChange = { checked ->
                                            viewModel.updateSelectedServices(
                                                category,
                                                service,
                                                checked
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(text = service)
                                }
                            }
                        }
                    }
                }
            }

        }

        item {
            Button(
                onClick = {
                    viewModel.saveSelectedSkills()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Save Selection")
            }
        }
    }

    servicesAdded.onSuccess {
        Text("Skills added successfully. $it")

        navController.navigate("home")
    }.onFailure {
        Text("Error adding skills: $it")
    }
}

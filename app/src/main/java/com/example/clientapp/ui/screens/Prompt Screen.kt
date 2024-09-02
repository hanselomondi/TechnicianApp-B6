package com.example.clientapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clientapp.ui.view_models.AssistantViewModel

@Composable
fun AssistantScreen(navController: NavController, viewModel: AssistantViewModel = viewModel()) {
    val prompt by viewModel.prompt.collectAsState()
    val response by viewModel.response.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = prompt,
            onValueChange = { viewModel.updatePrompt(it) },
            label = { Text("Enter your prompt") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.submitPromptFetchResponse() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = response.toString(),
                modifier = Modifier.padding(vertical = 4.dp)
            )






        }
    }
}

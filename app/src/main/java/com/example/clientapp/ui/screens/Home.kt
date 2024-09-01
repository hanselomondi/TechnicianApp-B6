package com.example.clientapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clientapp.NavDestinations
import com.example.clientapp.ui.view_models.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel(),
) {
    val signedOut by viewModel.signedOut.collectAsState()
    /*val cc = rememberCoroutineScope()

    var message1 by remember { mutableStateOf("") }
    var message2 by remember { mutableStateOf("") }

    val context = LocalContext.current*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Home Screen!")

        Text(viewModel.getAccountDetails())

        Spacer(modifier = Modifier.height(16.dp))





        /*Spacer(modifier = Modifier.height(25.dp))
        TextField(
            value = message1,
            onValueChange = { message1 = it },
            label = { Text("Client message Emulation") },
            modifier = Modifier.fillMaxWidth()
        )
        // TODO Test for messaging
        Button(
            onClick = {
                cc.launch {
                    clientCreateOrSendMessage(
                        clientId = "ClientID_1",
                        message = message1,
                        techId = techID,
                    ).onSuccess {
                        message1 = ""

                        Toast.makeText(
                            context,
                            "Message sent from ClientID_1 to Tech $techID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    //getClientsChatList(techId = "TechID")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Message from ClientID_1")
        }

        Spacer(modifier = Modifier.height(25.dp))
        TextField(
            value = message2,
            onValueChange = { message2 = it },
            label = { Text("Client message Emulation") },
            modifier = Modifier.fillMaxWidth()
        )
        // TODO Test for messaging
        Button(
            onClick = {
                cc.launch {
                    clientCreateOrSendMessage(
                        clientId = "ClientID_2",
                        message = message2,
                        techId = techID,
                    ).onSuccess {
                        message2 = ""

                        Toast.makeText(
                            context,
                            "Message sent from ClientID_2 to Tech $techID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    //getClientsChatList(techId = "TechID")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Message from ClientID_2")
        }*/


        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                navController.navigate(NavDestinations.CHAT_LIST.name)
            }
        ) {
            Text("See All Chats")
        }

        Spacer(modifier = Modifier.height(25.dp))

        /*Button(
            onClick = {
                navController.navigate(NavDestinations.EDIT_PROFILE.name)
            }
        ) {
            Text("Edit Profile")
        }*/

        Button(
            onClick = {
                navController.navigate(NavDestinations.PROMPT_SCREEN.name)
            }
        ) {
            Text("Prompt AI for Services")
        }


        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                viewModel.logout()
            }
        ) {
            Text("Logout")
        }
    }

    // TODO make sure there is no back screen
    signedOut.onSuccess {
        navController.navigate(NavDestinations.LOGIN.name) {
            // Clear the back stack to only have LOGIN as the top destination
            popUpTo(NavDestinations.LOGIN.name) { inclusive = true }
            launchSingleTop = true
        }
    }.onFailure { }

}
package com.example.testcrud.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testcrud.model.Chat
import com.example.testcrud.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(apiService: ApiService, currentUser: String) {
    var messages by remember { mutableStateOf(listOf<Chat>()) }
    var newMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Fetch messages every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            apiService.getMessages { fetchedMessages ->
                messages = fetchedMessages
            }
            delay(2000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Text("${message.sender_username}: ${message.message} (${message.timestamp})")
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                label = { Text("Votre message") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    scope.launch {
                        apiService.sendMessage(currentUser, newMessage) { success ->
                            if (success) {
                                newMessage = ""
                                apiService.getMessages { fetchedMessages -> messages = fetchedMessages }
                            }
                        }
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Envoyer")
            }
        }
    }
}

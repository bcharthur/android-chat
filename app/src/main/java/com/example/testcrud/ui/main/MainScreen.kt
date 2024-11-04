package com.example.testcrud.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testcrud.model.User
import com.example.testcrud.network.ApiService
import kotlinx.coroutines.launch

@Composable
fun MainScreen(apiService: ApiService) {
    var users by remember { mutableStateOf(listOf<User>()) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var editingUser by remember { mutableStateOf<User?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch data when the composable is loaded
    LaunchedEffect(Unit) {
        apiService.fetchData { result ->
            users = result
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Form for adding/editing a user
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    if (editingUser == null) {
                        // Ajouter un nouvel utilisateur
                        apiService.addUser(User(0, name, email, phone)) {
                            if (it) {
                                apiService.fetchData { result -> users = result }
                            }
                        }
                    } else {
                        // Modifier un utilisateur existant
                        apiService.updateUser(editingUser!!.id, User(editingUser!!.id, name, email, phone)) {
                            if (it) {
                                apiService.fetchData { result -> users = result }
                            }
                        }
                        editingUser = null
                    }
                    name = ""
                    email = ""
                    phone = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(if (editingUser == null) "Add User" else "Update User")
        }

        // Display user list
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
            items(users) { user ->
                UserRow(user, onEdit = {
                    name = user.name
                    email = user.email
                    phone = user.phone
                    editingUser = user
                }, onDelete = {
                    scope.launch {
                        apiService.deleteUser(user.id) {
                            if (it) {
                                apiService.fetchData { result -> users = result }
                            }
                        }
                    }
                })
            }
        }
    }
}

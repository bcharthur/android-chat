package com.example.testcrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


data class User(val id: Int, val name: String, val email: String, val phone: String)

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        var users by remember { mutableStateOf(listOf<User>()) }
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var editingUser by remember { mutableStateOf<User?>(null) }

        // Fetch data when the composable is loaded
        LaunchedEffect(Unit) {
            fetchData { result ->
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
                    if (editingUser == null) {
                        // Ajouter un nouvel utilisateur
                        addUser(User(0, name, email, phone)) {
                            if (it) {
                                // Rafraîchir la liste des utilisateurs
                                fetchData { result -> users = result }
                            }
                        }
                    } else {
                        // Modifier un utilisateur existant
                        updateUser(editingUser!!.id, User(editingUser!!.id, name, email, phone)) {
                            if (it) {
                                fetchData { result -> users = result }
                            }
                        }
                        editingUser = null
                    }
                    name = ""
                    email = ""
                    phone = ""
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
                        deleteUser(user.id) {
                            if (it) {
                                fetchData { result -> users = result }
                            }
                        }
                    })
                }
            }
        }
    }

    @Composable
    fun UserRow(user: User, onEdit: (User) -> Unit, onDelete: (User) -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Name: ${user.name}")
                Text("Email: ${user.email}")
                Text("Phone: ${user.phone}")
            }
            Row {
                Button(onClick = { onEdit(user) }, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Edit")
                }
                Button(onClick = { onDelete(user) }) {
                    Text("Delete")
                }
            }
        }
    }

    private fun fetchData(callback: (List<User>) -> Unit) {
        val url = "http://10.0.2.2:81/api/getData.php"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    try {
                        val jsonArray = JSONArray(responseData)
                        val userList = mutableListOf<User>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val user = User(
                                id = jsonObject.getInt("id"),
                                name = jsonObject.getString("name"),
                                email = jsonObject.getString("email"),
                                phone = jsonObject.getString("phone")
                            )
                            userList.add(user)
                        }
                        callback(userList)
                    } catch (e: Exception) {
                        callback(emptyList())
                    }
                } ?: callback(emptyList())
            }
        })
    }

    private fun addUser(user: User, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/addUser.php"
        val jsonBody = JSONObject()
        jsonBody.put("name", user.name)
        jsonBody.put("email", user.email)
        jsonBody.put("phone", user.phone)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
                    jsonBody.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }

    private fun updateUser(id: Int, user: User, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/updateUser.php"
        val jsonBody = JSONObject()
        jsonBody.put("id", id)
        jsonBody.put("name", user.name)
        jsonBody.put("email", user.email)
        jsonBody.put("phone", user.phone)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
                    jsonBody.toString()
        )

        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }

    private fun deleteUser(id: Int, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/deleteUser.php"
        val jsonBody = JSONObject()
        jsonBody.put("id", id)

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
                    jsonBody.toString()
        )

        val request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }
        })
    }
}

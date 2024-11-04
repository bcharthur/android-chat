package com.example.testcrud.network

import com.example.testcrud.model.Chat
import com.example.testcrud.model.User
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ApiService(private val client: OkHttpClient = OkHttpClient()) {

    fun fetchData(callback: (List<User>) -> Unit) {
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

    fun addUser(user: User, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/addUser.php"
        val jsonBody = JSONObject().apply {
            put("name", user.name)
            put("email", user.email)
            put("phone", user.phone)
        }

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

    fun updateUser(id: Int, user: User, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/updateUser.php"
        val jsonBody = JSONObject().apply {
            put("id", id)
            put("name", user.name)
            put("email", user.email)
            put("phone", user.phone)
        }

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

    fun deleteUser(id: Int, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/deleteUser.php"
        val jsonBody = JSONObject().apply {
            put("id", id)
        }

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


    fun registerUser(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        val url = "http://10.0.2.2:81/api/registerUser.php"
        val jsonBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

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
                callback(false, "Erreur de connexion au serveur.")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    response.body?.let {
                        val jsonResponse = JSONObject(it.string())
                        val message = jsonResponse.optString("message")
                        callback(false, message)
                    } ?: callback(false, "Erreur inconnue.")
                }
            }
        })
    }

    fun loginUser(username: String, password: String, callback: (Boolean, String?) -> Unit) {
        val url = "http://10.0.2.2:81/api/loginUser.php"
        val jsonBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

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
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.let {
                        val jsonResponse = JSONObject(it.string())
                        val userName = jsonResponse.optString("username")
                        callback(true, userName)
                    } ?: callback(false, null)
                } else {
                    callback(false, "Identifiants incorrects.")
                }
            }
        })
    }

    fun sendMessage(senderUsername: String, message: String, callback: (Boolean) -> Unit) {
        val url = "http://10.0.2.2:81/api/sendMessage.php"
        val jsonBody = JSONObject().apply {
            put("sender_username", senderUsername)
            put("message", message)
        }

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

    fun getMessages(callback: (List<Chat>) -> Unit) {
        val url = "http://10.0.2.2:81/api/getMessages.php"
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
                        val messageList = mutableListOf<Chat>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val message = Chat(
                                id = jsonObject.getInt("id"),
                                sender_username = jsonObject.getString("sender_username"),
                                message = jsonObject.getString("message"),
                                timestamp = jsonObject.getString("timestamp")
                            )
                            messageList.add(message)
                        }
                        callback(messageList)
                    } catch (e: Exception) {
                        callback(emptyList())
                    }
                } ?: callback(emptyList())
            }
        })
    }

}

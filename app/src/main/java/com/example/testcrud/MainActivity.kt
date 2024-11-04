package com.example.testcrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.testcrud.network.ApiService
import com.example.testcrud.ui.auth.LoginScreen
import com.example.testcrud.ui.auth.RegisterScreen
import com.example.testcrud.ui.chat.ChatScreen

class MainActivity : ComponentActivity() {
    private val apiService = ApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var showRegisterScreen by remember { mutableStateOf(false) }
            var currentUserName by remember { mutableStateOf("") }

            if (isLoggedIn) {
                // Rediriger vers ChatScreen lorsque l'utilisateur est connecté
                ChatScreen(apiService = apiService, currentUser = currentUserName)
            } else {
                if (showRegisterScreen) {
                    RegisterScreen(
                        apiService = apiService,
                        onRegisterSuccess = { showRegisterScreen = false }, // Naviguer vers l'écran de connexion après l'inscription réussie
                        onNavigateToLogin = { showRegisterScreen = false } // Retour à la connexion
                    )
                } else {
                    LoginScreen(
                        apiService = apiService,
                        onLoginSuccess = { username ->
                            isLoggedIn = true
                            currentUserName = username
                        },
                        onNavigateToRegister = { showRegisterScreen = true }
                    )
                }
            }
        }
    }
}

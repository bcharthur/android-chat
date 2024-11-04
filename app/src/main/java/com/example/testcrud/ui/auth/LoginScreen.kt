package com.example.testcrud.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testcrud.network.ApiService
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(apiService: ApiService, onLoginSuccess: (String) -> Unit, onNavigateToRegister: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre de la page
        Text(
            text = "Connexion",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), // Appliquer la transformation de mot de passe
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // Ligne avec les labels et les boutons "Login" et "S'inscrire"
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Se connecter", modifier = Modifier.padding(bottom = 4.dp))
                Button(
                    onClick = {
                        scope.launch {
                            apiService.loginUser(username, password) { success, userName ->
                                if (success) {
                                    onLoginSuccess(userName ?: "")
                                } else {
                                    errorMessage = "Identifiants incorrects. Vérifiez vos informations ou inscrivez-vous."
                                }
                            }
                        }
                    }
                ) {
                    Text("Login")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Créer un compte", modifier = Modifier.padding(bottom = 4.dp))
                Button(
                    onClick = onNavigateToRegister
                ) {
                    Text("S'inscrire")
                }
            }
        }

        errorMessage?.let {
            Text(it, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

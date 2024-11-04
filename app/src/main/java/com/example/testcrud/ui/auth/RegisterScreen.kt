package com.example.testcrud.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun RegisterScreen(apiService: ApiService, onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
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
            text = "Inscription",
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
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        // Ligne avec les labels et les boutons "S'inscrire" et "Retour à la connexion"
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Créer un compte", modifier = Modifier.padding(bottom = 4.dp))
                Button(
                    onClick = {
                        scope.launch {
                            apiService.registerUser(username, password) { success, error ->
                                if (success) {
                                    onRegisterSuccess()
                                } else {
                                    errorMessage = error ?: "Une erreur est survenue"
                                }
                            }
                        }
                    }
                ) {
                    Text("S'inscrire")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Revenir", modifier = Modifier.padding(bottom = 4.dp))
                Button(
                    onClick = onNavigateToLogin
                ) {
                    Text("Retour à la connexion")
                }
            }
        }

        errorMessage?.let {
            Text(it, modifier = Modifier.padding(vertical = 8.dp))
            if (it.contains("existe déjà")) {
                TextButton(onClick = onNavigateToLogin) {
                    Text("Connectez-vous")
                }
            }
        }
    }
}

package com.example.testcrud.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testcrud.model.User

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

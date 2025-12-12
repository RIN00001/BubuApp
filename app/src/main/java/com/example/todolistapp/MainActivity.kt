package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.todolistapp.ui.theme.TodoListAppTheme
import com.example.todolistapp.views.TodoListApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListAppTheme {
                // TODO: add routers here
                TodoListApp()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    TodoListAppTheme {
        TodoListApp()
    }
}
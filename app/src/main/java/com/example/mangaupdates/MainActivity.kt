package com.example.mangaupdates

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.mangaupdates.ui.theme.MangaupdatesTheme
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MangaupdatesTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    //state variables
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loginSuccess by remember { mutableStateOf<Boolean?>(null) }

    //launches network calls
    val coroutineScope = rememberCoroutineScope()

    //centers ui
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation() //hides input
        )
        Spacer(modifier = Modifier.height(16.dp))
        //async call for login request
        Button(onClick = {
            coroutineScope.launch {
                isLoading = true
                try {
                    val response = ApiClient.api.login(LoginRequest(username, password))

                    if (response.status == "success") {
                        loginSuccess = true
                        Log.d("LoginSuccess", "✅ Session Token: ${response.context?.session_token}")
                        Log.d("LoginSuccess", "✅ UID: ${response.context?.uid}")
                    } else {
                        loginSuccess = false
                        Log.e("LoginFailure", "❌ Login failed: ${response.reason}")
                    }

                } catch (e: HttpException) {
                    Log.e("LoginError", "HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}")
                    loginSuccess = false
                } catch (e: Exception) {
                    Log.e("LoginError", e.message ?: "Unknown error")
                    loginSuccess = false
                } finally {
                    isLoading = false
                }
            }
        }) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (loginSuccess) {
            true -> Text("✅ Login Successful!", color = Color.Green)
            false -> Text("❌ Login Failed", color = Color.Red)
            null -> {}
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

object ApiClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.mangaupdates.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: MangaUpdatesApi = retrofit.create(MangaUpdatesApi::class.java)
}



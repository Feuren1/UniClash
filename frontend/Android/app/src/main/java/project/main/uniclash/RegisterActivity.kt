package project.main.uniclash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import project.main.uniclash.retrofit.UserService
import project.main.uniclash.ui.theme.UniClashTheme
import project.main.uniclash.viewmodels.LoginViewModel
import project.main.uniclash.viewmodels.RegisterViewModel

@OptIn(ExperimentalComposeUiApi::class)
class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val registerViewModel by viewModels<RegisterViewModel> {
            RegisterViewModel.provideFactory(UserService.getInstance(this), this.application)
        }
        val activityContext = this

        super.onCreate(savedInstanceState)
        setContent {
            UniClashTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.background),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        val message by registerViewModel.text.collectAsState()
                        if (message.isNotEmpty()) {
                            Text(message, modifier = Modifier.padding(16.dp),
                                    style = TextStyle(
                                    color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            )
                        }
                        Box(
                            modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // otherwise RegisterFOrm will overlap toLoginButton
                        ) {
                            RegisterForm(registerViewModel)
                        }
                        ReturnToLoginButton(activityContext)
                    }
                }
            }
        }
    }
}

@Composable
fun ReturnToLoginButton(context: Context) {
    Button(
        onClick = {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            text = "Go to log in",
            color = Color.White
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterForm(registerViewModel: RegisterViewModel) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = "Email")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.Black,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Black,
                containerColor = Color.White
            )
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = "Username")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.Black,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Black,
                containerColor = Color.White
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisibility) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Black,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Black,
            containerColor = Color.White
        )
        )

        Button(
            onClick = {
                registerViewModel.signup(email, password, username) { callback ->
                    // Handle the registration result here
                    if (callback.success) {
                        // Registration successful
                        // Optionally, you can navigate to the next screen or show a success message

                    } else {
                        // Registration failed
                        // Optionally, you can show an error message
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Register")
        }
    }
}







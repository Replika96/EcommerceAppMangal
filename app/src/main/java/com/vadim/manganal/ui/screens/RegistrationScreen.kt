package com.vadim.manganal.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vadim.manganal.ui.ViewModel.AuthUiState
import com.vadim.manganal.ui.ViewModel.NavEvent
import com.vadim.manganal.ui.ViewModel.RegistrationViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.LightGray
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.SoftOrange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegistrationScreen(
    authViewModel: RegistrationViewModel,
    onNavigateToHome: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()

    var name by rememberSaveable  { mutableStateOf("") }
    var email by rememberSaveable  { mutableStateOf("") }
    var password by rememberSaveable  { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable  { mutableStateOf(false) }
    var passwordError by rememberSaveable  { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.navEvent.collectLatest { event ->
            if (event is NavEvent.ToHome) onNavigateToHome()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            Toast.makeText(context, (uiState as AuthUiState.Error).message, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .systemBarsPadding()
    ) {

        IconButton(
            onClick = onNavigateToHome,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = "Закрыть",
                tint = DarkBrown,
                modifier = Modifier.size(40.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Имя") },
                colors = textFieldColors(),
                modifier = fieldModifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                colors = textFieldColors(),
                modifier = fieldModifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Пароль") },
                colors = textFieldColors(),
                modifier = fieldModifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordError = false
                },
                label = { Text("Подтвердите пароль", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                singleLine = true,
                isError = passwordError,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Подтверждение пароля") },
                colors = textFieldColors(),
                modifier = fieldModifier
            )

            if (passwordError) {
                Text(
                    text = "Пароли не совпадают!",
                    color = MutedTerracotta,
                    fontSize = 16.sp,
                    fontFamily = GilroyBlack,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Некорректный email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password != confirmPassword) {
                        passwordError = true
                        return@Button
                    }

                    passwordError = false
                    authViewModel.registerUser(name, email, password)
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftOrange,
                    contentColor = DarkBrown
                ),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Зарегистрироваться", fontFamily = GilroyBlack, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Уже есть аккаунт? Войти",
                    color = MutedTerracotta,
                    fontFamily = GilroyBlack,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = SoftOrange,
    unfocusedIndicatorColor = LightGray
)



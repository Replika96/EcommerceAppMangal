package com.vadim.manganal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vadim.manganal.ui.viewModel.RegistrationViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.LightGray
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.SoftOrange

@Composable
fun EditPersonalInfoScreen(
    viewModel: RegistrationViewModel,
    onBack: () -> Unit
) {
    val userData by viewModel.userData.collectAsState()

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }


    LaunchedEffect(userData) {
        viewModel.loadUserData()
        userData?.let {
            name = it.name
            surname = it.surname.orEmpty()
            patronymic = it.patronymic.orEmpty()
            phone = it.phone.orEmpty()
            email = it.email
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .systemBarsPadding()
    ) {

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = DarkBrown,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Редактировать профиль",
                style = TextStyle(
                    fontFamily = GilroyBlack,
                    fontSize = 24.sp,
                    color = DarkBrown
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.8f)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = DarkBrown) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = SoftOrange,
                    unfocusedIndicatorColor = LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp)
            )

            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Фамилия", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Surname", tint = DarkBrown) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = SoftOrange,
                    unfocusedIndicatorColor = LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp)
            )

            OutlinedTextField(
                value = patronymic,
                onValueChange = { patronymic = it },
                label = { Text("Отчество", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Patronymic", tint = DarkBrown) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = SoftOrange,
                    unfocusedIndicatorColor = LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = DarkBrown) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = SoftOrange,
                    unfocusedIndicatorColor = LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("E-mail", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = LightGray, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = LightGray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledIndicatorColor = LightGray,
                    disabledLabelColor = LightGray,
                    disabledLeadingIconColor = LightGray
                ),
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp)
            )

            Button(
                onClick = {
                    val updatedUser = userData?.copy(
                        name = name,
                        surname = surname,
                        patronymic = patronymic,
                        phone = phone
                    )
                    if (updatedUser != null) {
                        viewModel.updateUserData(updatedUser)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftOrange,
                    contentColor = DarkBrown
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Сохранить",
                    fontFamily = GilroyBlack,
                    fontSize = 18.sp
                )
            }
        }
    }
}


package com.vadim.manganal.ui.screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vadim.manganal.ui.viewModel.AuthUiState
import com.vadim.manganal.ui.viewModel.RegistrationViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.LightGray
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.SoftOrange

@Composable
fun EditAddressScreen(
    viewModel: RegistrationViewModel,
    onBack: () -> Unit
) {
    val userData by viewModel.userData.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var city by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var house by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }


    LaunchedEffect(userData) {
        viewModel.loadUserData()
        userData?.let {
            city = it.city.orEmpty()
            region = it.region.orEmpty()
            street = it.street.orEmpty()
            postalCode = it.postalCode.orEmpty()
            house = it.house.orEmpty()
            apartment = it.apartment.orEmpty()
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                viewModel.resetUiState()
                onBack()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, (uiState as AuthUiState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
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
                imageVector = Icons.Default.ArrowBack,
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
                text = "Редактировать адрес",
                style = TextStyle(
                    fontFamily = GilroyBlack,
                    fontSize = 24.sp,
                    color = DarkBrown
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.8f)
            )


            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    color = SoftOrange,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Город", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = "City", tint = DarkBrown) },
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
                value = region,
                onValueChange = { region = it },
                label = { Text("Регион", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Map, contentDescription = "Region", tint = DarkBrown) },
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
                value = street,
                onValueChange = { street = it },
                label = { Text("Улица", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Street", tint = DarkBrown) },
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
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Индекс", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.MarkunreadMailbox, contentDescription = "Postal Code", tint = DarkBrown) },
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
                value = house,
                onValueChange = { house = it },
                label = { Text("Дом", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = "House", tint = DarkBrown) },
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
                value = apartment,
                onValueChange = { apartment = it },
                label = { Text("Квартира", color = MutedTerracotta, fontFamily = GilroyBlack, fontSize = 18.sp) },
                textStyle = TextStyle(color = DarkBrown, fontFamily = GilroyBlack, fontSize = 16.sp),
                leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = "Apartment", tint = DarkBrown) },
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

            Button(
                onClick = {
                    if (city.isBlank() || street.isBlank() || house.isBlank()) {
                        Toast.makeText(context, "Город, улица и дом обязательны", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val updatedUser = userData?.copy(
                        city = city,
                        region = region,
                        street = street,
                        postalCode = postalCode,
                        house = house,
                        apartment = apartment
                    )
                    if (updatedUser != null) {
                        viewModel.updateUserData(updatedUser)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftOrange,
                    contentColor = DarkBrown
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Сохранить",
                        fontFamily = GilroyBlack,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
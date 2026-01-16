package com.vadim.manganal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vadim.manganal.R
import com.vadim.manganal.ui.ViewModel.AuthUiState
import com.vadim.manganal.ui.ViewModel.NavEvent
import com.vadim.manganal.ui.ViewModel.RegistrationViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.LightGray
import com.vadim.manganal.ui.theme.MutedTerracotta

@Composable
fun ProfileScreen(
    viewModel: RegistrationViewModel,
    onAdminClick: () -> Unit,
    onRegistrationClick: () -> Unit,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit,
    onEditPersonalInfo: () -> Unit,
    onEditAddress: () -> Unit,
    onContact: () -> Unit,
    onHistory: () -> Unit,
) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    val isAnonymous by viewModel.isAnonymous.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val userData by viewModel.userData.collectAsState()

    val context = LocalContext.current


    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            Toast.makeText(context, (uiState as AuthUiState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }


    LaunchedEffect(Unit) {
        viewModel.initAuth()
        viewModel.loadUserData()
        viewModel.navEvent.collect { event ->
            when (event) {
                is NavEvent.ToHome -> onNavigate("home")
                is NavEvent.ToLogin -> onNavigate("login")
                is NavEvent.ToAdmin -> onAdminClick()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Личный кабинет",
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        color = DarkBrown
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            val currentUid = userData?.email
            val isAdmin = currentUid == "tazmin.vadim@mail.ru"

            if (isAdmin) {
                TextButton(
                    onClick = { onAdminClick() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MutedTerracotta),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Перейти в админку", style = TextStyle(fontFamily = fontFamily, fontSize = 18.sp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }


            HorizontalDivider(thickness = 1.dp, color = LightGray)

            TextButton(
                onClick = { onHistory() },
                colors = ButtonDefaults.textButtonColors(contentColor = DarkBrown),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("История заказов", style = TextStyle(fontFamily = fontFamily, fontSize = 18.sp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
            HorizontalDivider(thickness = 1.dp, color = LightGray)

            // кнопка "Зарегистрироваться" или "Выйти"
            when (isAnonymous) {
                null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(
                                color = DarkBrown,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Секундочку...",
                                color = DarkBrown,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.alpha(0.8f)
                            )
                        }
                    }
                }
                true -> {
                    TextButton(
                        onClick = onRegistrationClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = MutedTerracotta),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Зарегистрироваться", style = TextStyle(fontFamily = fontFamily, fontSize = 18.sp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
                false -> {
                    TextButton(
                        onClick = { viewModel.signOut() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MutedTerracotta),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Выйти", style = TextStyle(fontFamily = fontFamily, fontSize = 18.sp))
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        }
                    }
                }
            }

            val textStyle = TextStyle(
                fontFamily = fontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            val headerStyle = textStyle.copy(fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)

            // контакты
            TextButton(
                onClick = onContact,
                colors = ButtonDefaults.textButtonColors(contentColor = DarkBrown),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Контакты", style = TextStyle(fontFamily = fontFamily, fontSize = 18.sp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }

            // раздел "Персональная информация"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(DarkBrown, shape = RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Персональная информация", style = headerStyle)

                    if (isAnonymous == false && userData != null) {
                        // показываем данные пользователя, если он зарегистрирован
                        val personalItems = listOf(
                            "Имя" to userData?.name.orEmpty(),
                            "Фамилия" to userData?.surname.orEmpty(),
                            "Отчество" to userData?.patronymic.orEmpty(),
                            "Телефон" to userData?.phone.orEmpty(),
                            "e-mail" to userData?.email.orEmpty()
                        )
                        personalItems.forEach { (label, value) ->
                            Text(text = "$label: $value", style = textStyle)
                        }

                        TextButton(
                            onClick = onEditPersonalInfo,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text(
                                text = "Изменить",
                                style = textStyle,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    } else {
                        Text(
                            text = "Пожалуйста, зарегистрируйтесь или войдите, чтобы ввести персональные данные.",
                            style = textStyle
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(DarkBrown, shape = RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Адрес доставки", style = headerStyle)

                    if (isAnonymous == false && userData != null) {
                        val addressItems = listOf(
                            "Город" to userData?.city.orEmpty(),
                            "Регион" to userData?.region.orEmpty(),
                            "Улица" to userData?.street.orEmpty(),
                            "Индекс" to userData?.postalCode.orEmpty(),
                            "Дом" to userData?.house.orEmpty(),
                            "Кв" to userData?.apartment.orEmpty()
                        )

                        addressItems.forEach { (label, value) ->
                            Text(text = "$label: $value", style = textStyle)
                        }

                        TextButton(
                            onClick = onEditAddress,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text(
                                text = "Изменить",
                                style = textStyle,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    } else {
                        Text(
                            text = "Пожалуйста, зарегистрируйтесь или войдите, чтобы ввести адрес доставки.",
                            style = textStyle
                        )
                    }
                }
            }
        }
    }
}


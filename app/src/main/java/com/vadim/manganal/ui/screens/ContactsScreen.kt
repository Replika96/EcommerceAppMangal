package com.vadim.manganal.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vadim.manganal.R
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.screens.fontFamily
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@Composable
fun ContactsScreen(
    onNavigateToHome: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBeige
    ) {
        Box {
            IconButton(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Back",
                    tint = DarkBrown,
                    modifier = Modifier.size(40.dp)
                )
            }
            Column {
                // заголовок
                Text(
                    text = "Контакты",
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = fontFamily, fontSize = 40.sp),
                    color = MutedTerracotta,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                LazyColumn {
                    // телефоны
                    item {
                        ContactItem(
                            icon = Icons.Default.Phone,
                            title = "Телефон",
                            content = "+7 (923) 393-66-14\n+7 (962) 800 60-01",
                            onClick = {
                                clipboardManager.setText(AnnotatedString("+7 (923) 393-66-14"))
                                scope.launch {
                                    snackbarHostState.showSnackbar("Телефон скопирован")
                                }
                            }
                        )
                    }

                    // адрес
                    item {
                        ContactItem(
                            icon = Icons.Default.LocationOn,
                            title = "Наш адрес",
                            content = "Республика Хакасия г. Абакан,\n ул. Мира 65е Завод",
                            onClick = {
                                // открытие адреса в Google Maps
                                val address = "Республика Хакасия г. Абакан, ул. Мира 65е"
                                val uri = "geo:0,0?q=${Uri.encode(address)}".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                runCatching {
                                    context.startActivity(intent)
                                }.onFailure {
                                    //если Google Maps не установлены, открываем в браузере
                                    context.startActivity(Intent(Intent.ACTION_VIEW,
                                        "https://maps.google.com/?q=${Uri.encode(address)}".toUri()))
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar("Открываем адрес...")
                                }
                            }
                        )
                    }

                    // Email
                    item {
                        ContactItem(
                            icon = Icons.Default.Email,
                            title = "E-Mail",
                            content = "35510@mail.ru",
                            onClick = {
                                // открытие почтового клиента
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:35510@mail.ru".toUri()
                                }
                                runCatching {
                                    context.startActivity(Intent.createChooser(emailIntent, "Отправить email"))
                                }.onFailure {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Не удалось открыть почтовый клиент")
                                    }
                                }
                            }
                        )
                    }

                    // мессенджеры
                    item {
                        Column {
                            Text(
                                text = "Мессенджеры",
                                color = MutedTerracotta,
                                style = MaterialTheme.typography.titleSmall.copy(fontFamily = fontFamily, fontSize = 32.sp),
                                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
                            )
                            ContactItem(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_whatsapp),
                                title = "WhatsApp",
                                content = "7 (923) 393-66-14",
                                isColoredIcon = true,
                                onClick = {
                                    // Открытие WhatsApp
                                    val phoneNumber = "+79233936614"
                                    val whatsappUri = "https://wa.me/$phoneNumber".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW, whatsappUri)
                                    runCatching {
                                        context.startActivity(intent)
                                    }.onFailure {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("WhatsApp не установлен")
                                        }
                                    }
                                }
                            )
                            ContactItem(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_viber),
                                title = "Viber",
                                content = "+7 (923) 393-66-14",
                                isColoredIcon = true,
                                onClick = {
                                    val phoneNumber = "+79233936614"
                                    val viberUri = "viber://chat?number=$phoneNumber".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW, viberUri)
                                    runCatching {
                                        context.startActivity(intent)
                                    }.onFailure {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Viber не установлен")
                                        }
                                        context.startActivity(Intent(Intent.ACTION_VIEW,
                                            "https://viber.me/$phoneNumber".toUri()))
                                    }
                                }
                            )
                        }
                    }

                    // соцсети
                    item {
                        Column {
                            Text(
                                text = "Социальные сети",
                                color = MutedTerracotta,
                                style = MaterialTheme.typography.titleSmall.copy(fontFamily = fontFamily, fontSize = 32.sp),
                                modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
                            )
                            ContactItem(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_odnoklassniki),
                                title = "Одноклассники",
                                content = "574523391595",
                                isColoredIcon = true,
                                onClick = {
                                    // открытие профиля в Одноклассниках
                                    val okProfileId = "574523391595" // ID профиля
                                    val okUri = "ok://profile/$okProfileId".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW, okUri)
                                    runCatching {
                                        context.startActivity(intent)
                                    }.onFailure {
                                        // если приложение не установлено, открываем веб-версию
                                        val webUri = "https://ok.ru/profile/$okProfileId".toUri()
                                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Открываем Одноклассники в браузере")
                                        }
                                    }
                                }
                            )
                            ContactItem(
                                icon = ImageVector.vectorResource(id = R.drawable.ic_vk),
                                title = "ВКонтакте",
                                content = "id700714456",
                                isColoredIcon = true,
                                onClick = {
                                    // открытие профиля в ВКонтакте
                                    val vkProfileId = "id700714456" // ID профиля
                                    val vkUri = "vk://profile/$vkProfileId".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW, vkUri)
                                    runCatching {
                                        context.startActivity(intent)
                                    }.onFailure {
                                        // если приложение не установлено, открываем веб-версию
                                        val webUri = "https://vk.com/$vkProfileId".toUri()
                                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Открываем ВКонтакте в браузере")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    title: String,
    content: String,
    isColoredIcon: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = LightBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isColoredIcon) Color.Unspecified else MutedTerracotta,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    color = DarkBrown,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = fontFamily,
                        fontSize = 24.sp
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = content,
                    color = DarkBrown.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = fontFamily,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}
package com.vadim.manganal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vadim.manganal.R
import com.vadim.manganal.ui.viewModel.RegistrationViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.LightGray
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.SageGreen
import com.vadim.manganal.ui.viewModel.CartViewModel


@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    cartViewModel: CartViewModel,
    regViewModel: RegistrationViewModel,
    onOrderConfirm: () -> Unit,
    onEditAddress: () -> Unit
) {

    val cartState by cartViewModel.cart.collectAsState()

    val userData by regViewModel.userData.collectAsState()
    LaunchedEffect(Unit) { regViewModel.loadUserData() }   // один раз


    val fontFamily = remember { FontFamily(Font(R.font.gilroyblack)) }
    var selectedDelivery by rememberSaveable { mutableStateOf("Самовывоз") }
    var selectedPayment  by rememberSaveable { mutableStateOf("Наличные") }


    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    painter = painterResource(R.drawable.ic_icons_arrow_left),
                    contentDescription = "Назад",
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                "Оформление заказа",
                style = TextStyle(fontSize = 22.sp, fontFamily = fontFamily),
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("Способ получения", fontWeight = FontWeight.Bold, fontFamily = fontFamily, fontSize = 26.sp)
        listOf("Самовывоз", "Доставка").forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedDelivery = option }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedDelivery == option,
                    onClick  = { selectedDelivery = option },
                    colors   = RadioButtonDefaults.colors(
                        selectedColor           = DarkBrown,
                        unselectedColor         = Color.Gray,
                        disabledSelectedColor   = Color.LightGray,
                        disabledUnselectedColor = Color.LightGray
                    )
                )
                Text(option, fontFamily = fontFamily, fontSize = 18.sp)
            }
        }

        // Адрес (только если доставка)
        if (selectedDelivery == "Доставка") {
            Spacer(Modifier.height(16.dp))
            Text("Адрес доставки", fontWeight = FontWeight.Bold, fontFamily = fontFamily,fontSize = 18.sp)
            if (userData != null) {

                Text(userData!!.name, fontFamily = fontFamily)
                Text(userData!!.phone ?: "Телефон не указан", fontFamily = fontFamily)
            } else {
                // поля ввода для анонимных пользователей
                var name by remember { mutableStateOf("") }
                var phone by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = MutedTerracotta,
                        unfocusedIndicatorColor = LightGray
                    )
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Номер телефона", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = MutedTerracotta,
                        unfocusedIndicatorColor = LightGray
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            if (userData != null) {
                val parts = listOfNotNull(
                    userData?.postalCode,
                    userData?.region,
                    userData?.city,
                    userData?.street,
                    userData?.house,
                    userData?.apartment
                )
                Row(
                    Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = parts.joinToString(", "),
                        fontFamily = fontFamily,
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = { onEditAddress() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MutedTerracotta)
                ) {
                    Text("Изменить", color = Color.White, fontFamily = fontFamily)
                }
            } else {
                // блок для анонимных пользователей
                Column {

                    var city by remember { mutableStateOf("") }
                    var street by remember { mutableStateOf("") }
                    var house by remember { mutableStateOf("") }
                    var apartment by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("Город", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = LightGray,
                            unfocusedIndicatorColor = MutedTerracotta
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Row {
                        OutlinedTextField(
                            value = street,
                            onValueChange = { street = it },
                            label = { Text("Улица", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                            modifier = Modifier.weight(0.7f),
                            textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = LightGray,
                                unfocusedIndicatorColor = MutedTerracotta
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = house,
                            onValueChange = { house = it },
                            label = { Text("Дом", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                            modifier = Modifier.weight(0.3f),
                            textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = LightGray,
                                unfocusedIndicatorColor = MutedTerracotta
                            )
                        )
                    }
                    OutlinedTextField(
                        value = apartment,
                        onValueChange = { apartment = it },
                        label = { Text("Квартира", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = LightGray,
                            unfocusedIndicatorColor = LightGray
                        )
                    )


                    // кнопка сохранения адреса
                    Button(
                        onClick = {
                            if (city.isBlank() || street.isBlank() || house.isBlank()) {
                                Toast.makeText(context, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SageGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Использовать этот адрес", fontFamily = fontFamily)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Способ оплаты", fontWeight = FontWeight.Bold, fontFamily = fontFamily,fontSize = 26.sp)
        listOf("Наличные", "По карте").forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPayment = option }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPayment == option,
                    onClick  = { selectedPayment = option },
                    colors   = RadioButtonDefaults.colors(
                        selectedColor           = DarkBrown,
                        unselectedColor         = Color.Gray,
                        disabledSelectedColor   = Color.LightGray,
                        disabledUnselectedColor = Color.LightGray
                    )
                )
                Text(option, fontFamily = fontFamily, fontSize = 18.sp)
            }
        }

        Spacer(Modifier.height(16.dp))



        Text("Товары", fontWeight = FontWeight.Bold, fontFamily = fontFamily, fontSize = 26.sp)
        Spacer(Modifier.height(8.dp))

        if (cartState?.items.isNullOrEmpty()) {
            Text("Корзина пуста", fontFamily = fontFamily, color = Color.Gray)
        } else {
            cartState?.items?.forEach { cartItem ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            cartItem.product.name,
                            fontFamily = fontFamily,
                            maxLines   = 2,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.padding(end = 8.dp)
                        )
                        Text("${cartItem.product.price} ₽", fontFamily = fontFamily)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { cartViewModel.decreaseItemQuantity(cartItem) }) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Уменьшить",
                                tint = DarkBrown)
                        }
                        Text(cartItem.quantity.toString(), fontFamily = fontFamily)

                        IconButton(onClick = { cartViewModel.increaseItemQuantity(cartItem) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Увеличить",
                                tint = DarkBrown)
                        }
                        IconButton(onClick = { cartViewModel.removeItem(cartItem) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить товар",
                                tint = DarkBrown)
                        }
                    }
                }
            }
        }

        val totalPrice = cartState?.items?.sumOf { it.product.price * it.quantity } ?: 0
        Text(
            "Итого: $totalPrice ₽",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily, color = DarkBrown)
        )
        var wishes by remember { mutableStateOf("") }
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = wishes,
            onValueChange = { wishes = it },
            label = { Text("Ваши пожелания к заказу", color = DarkBrown, fontFamily = GilroyBlack, fontSize = 18.sp) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontFamily = GilroyBlack, fontSize = 18.sp, color = DarkBrown),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = MutedTerracotta,
                unfocusedIndicatorColor = LightGray
            )
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Заблокировано (Вадим)", Toast.LENGTH_SHORT).show()
                // onOrderConfirm() // вызов
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape  = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MutedTerracotta)
        ) {
            Text("Оформить заказ", fontFamily = fontFamily, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

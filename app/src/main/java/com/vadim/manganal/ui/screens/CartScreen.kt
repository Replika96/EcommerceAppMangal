package com.vadim.manganal.ui.theme.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vadim.manganal.R
import com.vadim.manganal.domain.entity.Cart
import com.vadim.manganal.domain.entity.CartItem
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.SoftOrange
import com.vadim.manganal.ui.viewModel.CartViewModel


@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckoutClick: () -> Unit,
    cartViewModel: CartViewModel
) {
    val carts by cartViewModel.cart.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            //.padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        CartTop(onBack)

        Spacer(modifier = Modifier.height(8.dp))

        carts?.let { cart ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cart.items) { cartItem ->
                    CartItemCard(cartItem = cartItem, cartViewModel = cartViewModel)
                }

            }
        }

        CartBottom(carts, onCheckoutClick)
    }
}

@Composable
fun CartTop(onBack: () -> Unit) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(LightBeige)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_icons_arrow_left),
                contentDescription = "Назад",
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = "Корзина",
            style = TextStyle(
                fontSize = 26.sp,
                color = DarkBrown,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.align(Alignment.Center)
        )
        /*IconButton(
            onClick = { /* логика поиска */ },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Поиск",
                modifier = Modifier.size(24.dp)
            )
        }*/
    }
}

@Composable
fun CartItemCard(cartItem: CartItem, cartViewModel: CartViewModel) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF8F1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // изображение товара
            AsyncImage(
                model = cartItem.product.imageUrl,
                contentDescription = cartItem.product.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.ic_error_image)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // описание и управление количеством товара
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = cartItem.product.name,
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = DarkBrown,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${cartItem.product.price} ₽",
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = MutedTerracotta,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Normal
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                // блок управления количеством товара
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftOrange)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { cartViewModel.decreaseItemQuantity(cartItem) },
                                enabled = cartItem.quantity > 1,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Уменьшить количество",
                                    tint = if (cartItem.quantity > 1) Color(0xFF6A3E1D) else Color.Gray
                                )
                            }

                            Text(
                                text = cartItem.quantity.toString(),
                                style = TextStyle(
                                    color = Color(0xFF6A3E1D),
                                    fontSize = 16.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            IconButton(
                                onClick = { cartViewModel.increaseItemQuantity(cartItem) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Увеличить количество",
                                    tint = Color(0xFF6A3E1D)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(8f))
                    IconButton(
                        onClick = {
                            cartViewModel.removeItem(cartItem)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить товар",
                            tint = DarkBrown
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartBottom(cart: Cart?, onCheckOutClick: () -> Unit) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
        Row(
            modifier = Modifier.padding(top = 5.dp)
        ){
            Spacer(modifier = Modifier.height(10.dp))


            cart?.let {
                val count = it.items.sumOf { cart -> cart.quantity }
                val item = getProductDeclension(count)
                Text(
                    text = "$count $item" ,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            val totalPrice = cart?.items?.sumOf{ it.product.price * it.quantity} ?:0
            Text(
                text = "$totalPrice ₽",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A3E1D)
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onCheckOutClick()
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SoftOrange,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .width(250.dp)
                .height(40.dp)
                .clip(CircleShape)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Оформить заказ",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A3E1D)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

            }
        }
    }
}
fun getProductDeclension(count: Int): String {
    val mod10 = count%10
    val mod100 = count%100
    return when{
        mod10 == 1 && mod100!=11 -> "товар"
        mod10 in 2..4 && mod100 !in 12..14 -> "товара"
        else -> "товаров"
    }
}





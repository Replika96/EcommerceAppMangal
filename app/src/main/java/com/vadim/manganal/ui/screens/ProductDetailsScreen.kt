package com.vadim.manganal.ui.theme.screens


import android.text.Html
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vadim.manganal.R
import com.vadim.manganal.ui.viewModel.FavoritesViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.SoftOrange
import com.vadim.manganal.ui.viewModel.CartViewModel
import com.vadim.manganal.ui.viewModel.ProductDetailsViewModel


@Composable
fun ProductDetailsScreen(
    productId: String,
    productViewModel: ProductDetailsViewModel,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    onBack: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val product by productViewModel.product.collectAsState()
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    val scrollState = rememberScrollState()

    val favorites by favoritesViewModel.favorites.collectAsState()
    val isFavorite = product?.id in favorites

    LaunchedEffect(productId) {
        productViewModel.loadProduct(productId)
    }

    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(LightBeige)) {
        if (product == null) {
            // экран загрузки
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = DarkBrown,
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Секундочку...",
                    color = DarkBrown,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = fontFamily, fontSize = 40.sp),
                    modifier = Modifier.alpha(0.8f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 100.dp)
            ) {
                AsyncImage(
                    model = product!!.imageUrl,
                    contentDescription = product!!.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    error = painterResource(R.drawable.ic_error_image)
                )


                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center){
                        Column(modifier = Modifier
                            .weight(6f)) {
                            Text(
                                text = product!!.name,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    color = DarkBrown,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column(modifier = Modifier
                            .weight(1f)) {
                            IconButton(
                                onClick = {
                                    // Вызываем toggle из FavoritesViewModel
                                    favoritesViewModel.toggle(product!!.id)
                                    val message = if (isFavorite) {
                                        "Удалено из избранного"
                                    } else {
                                        "Добавлено в избранное"
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(35.dp)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Избранное",
                                    modifier = Modifier.size(28.dp),
                                    tint = if (isFavorite) Color.Red else Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // описание товара с html
                    val htmlText = product!!.description.trimIndent()
                    val spanned = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
                    val annotatedString = buildAnnotatedString { append(spanned.toString()) }
                    Text(
                        text = annotatedString,
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = DarkBrown.copy(alpha = 0.8f),
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // плавающая кнопка "закрыть"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Назад",
                        tint = Color(0xFF6A3E1D),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // плавающая кнопка
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, end = 18.dp, start = 18.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                val context = LocalContext.current
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            cartViewModel.addItem(product!!)
                            Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftOrange,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cart1),
                            contentDescription = "Добавить в корзину",
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF6A3E1D)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${product!!.price} ₽",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A3E1D)
                            )
                        )
                    }

                    // кнопка "Оформить"
                    Button(
                        onClick = {
                            cartViewModel.addItem(product!!)
                            onCheckoutClick()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB6D7A8),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Оформить",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF264319)
                            )
                        )
                    }
                }
            }

        }
    }
}

// доделать зум и сделать пролистывание фоток
@Composable
fun ZoomableImage(imageUrl: String, productName: String) {
    var expanded by remember { mutableStateOf(false) }
    var scale by remember { mutableFloatStateOf(1f) }

    val transformState = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
    }

    if (expanded) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBeige)
                .clickable { expanded = false }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = productName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = 0f
                        translationY = 0f
                    }
                    .transformable(state = transformState)
            )
        }
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = productName,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clickable { expanded = true },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.ic_error_image)
        )
    }
}




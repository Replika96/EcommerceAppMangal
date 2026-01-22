package com.vadim.manganal.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.entity.ProductState
import com.vadim.manganal.domain.entity.SortOption
import com.vadim.manganal.ui.viewModel.FavoritesViewModel
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.viewModel.MangalViewModel

val  fontFamily = FontFamily(Font(R.font.gilroyblack))

@Composable
fun FavoritesScreen(
    mangalViewModel: MangalViewModel,
    favoritesViewModel: FavoritesViewModel,
    onDetailsClick: (String) -> Unit,
    onCartClick: () -> Unit = {}
) {
    val state by mangalViewModel.state.collectAsState()
    when (state) {
        is ProductState.Loading -> {
            Text("Загрузка...")
        }

        is ProductState.Error -> {
            Text("Ошибка: ${(state as ProductState.Error).message}")
        }

        is ProductState.Success -> {
            val products = (state as ProductState.Success).products
            val favorites by favoritesViewModel.favorites.collectAsState()
            var searchQuery by remember { mutableStateOf("") }
            var selectedCategory by remember { mutableStateOf("Все") }
            var sortOption by remember {
                mutableStateOf(
                    SortOption(
                        SortField.PRICE,
                        SortOrder.ASCENDING
                    )
                )
            }

            val categories = remember {
                listOf(
                    "Все", "Мангальные зоны", "Костровые чаши", "Флюгера",
                    "Адресные таблички", "Мангалы в виде животных", "Дымники",
                    "Костровые Сферы", "Дровницы", "Чан банный", "Сотовый стол для лазерного станка"
                )
            }

            // фильтруем товары, чтобы отображать только избранные
            val favoriteProducts by remember(products, favorites) {
                derivedStateOf {
                    products.filter { it.id in favorites }
                }
            }

            // сортировка и фильтрация с учетом поиска
            val sortedFilteredProducts by remember(
                favoriteProducts,
                selectedCategory,
                sortOption,
                searchQuery
            ) {
                derivedStateOf {
                    // фильтрация по категории
                    val filtered = if (selectedCategory == "Все") favoriteProducts
                    else favoriteProducts.filter { it.category == selectedCategory }

                    // фильтрация по поисковому запросу
                    val searched = if (searchQuery.isBlank()) filtered
                    else filtered.filter { product ->
                        product.name.contains(searchQuery, ignoreCase = true) ||
                                product.description?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) ?: false ||
                                product.category.contains(searchQuery, ignoreCase = true)
                    }

                    // сортировка
                    when (sortOption.field) {
                        SortField.PRICE -> if (sortOption.order == SortOrder.ASCENDING)
                            searched.sortedBy { it.price }
                        else
                            searched.sortedByDescending { it.price }

                        SortField.NAME -> if (sortOption.order == SortOrder.ASCENDING)
                            searched.sortedBy { it.name }
                        else
                            searched.sortedByDescending { it.name }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBeige)
            ) {

                SearchBar(
                    onSearch = { query -> searchQuery = query }
                )

                CategoriesDropdown(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryClick = { selectedCategory = it }
                )

                SortBar(
                    currentSort = sortOption,
                    onSortChange = { sortOption = it }
                )

                if (sortedFilteredProducts.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (favorites.isEmpty()) "У вас пока нет избранных товаров"
                            else "Ничего не найдено по вашему запросу",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkBrown,
                            fontFamily = fontFamily
                        )
                    }
                } else {
                    ContentGrid(
                        products = sortedFilteredProducts,
                        onDetailsClick = onDetailsClick,
                        favoritesViewModel = favoritesViewModel,
                        favorites = favorites
                    )
                }
            }
        }
    }
}


@Composable
fun ContentGrid(
    products: List<Product>,
    favorites: List<String>,
    favoritesViewModel: FavoritesViewModel,
    onDetailsClick: (String) -> Unit
) {
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(2.dp),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products) { product ->
            val cardShape = RoundedCornerShape(20.dp)
            // проверяем, находится ли продукт в избранном (должно быть true, так как это FavoritesScreen)
            val isFavorite = product.id in favorites
            Card(
                colors = CardDefaults.cardColors(containerColor = LightBeige),
                shape = RectangleShape,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) { product.id.let(onDetailsClick) }
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(cardShape)
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.ic_error_image)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {

                                    favoritesViewModel.remove(product.id)
                                    Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(35.dp)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Избранное",
                                    modifier = Modifier.size(28.dp),
                                    tint = Color.Red
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = DarkBrown,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${product.price} ₽",
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = MutedTerracotta,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


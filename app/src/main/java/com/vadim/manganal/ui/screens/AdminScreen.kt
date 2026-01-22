package com.vadim.manganal.ui.theme.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.R
import com.vadim.manganal.domain.entity.ProductState
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.ViewModel.MangalViewModel
import com.vadim.manganal.ui.theme.ViewModel.ImageViewModel
import com.vadim.manganal.utils.UriUtils



@Composable
fun AdminScreen(
    mangalViewModel: MangalViewModel = hiltViewModel(),
    imageViewModel: ImageViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by mangalViewModel.state.collectAsState()
    when(state){
        is ProductState.Loading ->{
            Text("Загрузка")
        }
        is ProductState.Error ->{
            Text("Ошибка: ${(state as ProductState.Error).message}")
        }
        is ProductState.Success ->{
            val products = (state as ProductState.Success).products
            val uploadStatus by imageViewModel.uploadStatus.observeAsState()
            val uploadedImageUrl by imageViewModel.uploadedImageUrl.observeAsState()

            var productName by remember { mutableStateOf("") }
            var productPrice by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("") }
            var productDescription by remember { mutableStateOf("") }
            var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = { uri ->
                    uri?.let {
                        selectedImageUri = it
                        val file = UriUtils.getFileFromUri(context, it)
                        imageViewModel.uploadImage(file)
                    }
                }
            )

            val isFormValid = productName.isNotBlank() && productPrice.toIntOrNull() != null && category.isNotBlank()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // заголовок
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.padding(10.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_icons_arrow_left),
                                contentDescription = stringResource(R.string.back),
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.admin_panel),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    ProductForm(
                        productName = productName,
                        onProductNameChange = { productName = it },
                        productPrice = productPrice,
                        onProductPriceChange = { productPrice = it },
                        category = category,
                        onCategoryChange = { category = it },
                        productDescription = productDescription,
                        onProductDescriptionChange = { productDescription = it }
                    )

                    uploadStatus?.let { status ->
                        if (status.contains("Ошибка")) {
                            Text(
                                text = status,
                                color = Color.Red,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MutedTerracotta,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.select_image))
                    }

                    Button(
                        onClick = {
                            uploadedImageUrl?.let { imageUrl ->
                                mangalViewModel.addProduct(
                                    Product(
                                        id = "",
                                        name = productName,
                                        price = productPrice.toIntOrNull() ?: 0,
                                        category = category,
                                        description = productDescription,
                                        imageUrl = imageUrl
                                    )
                                )
                                productName = ""
                                productPrice = ""
                                category = ""
                                productDescription = ""
                                selectedImageUri = null
                                imageViewModel.resetUploadStatus()
                                imageViewModel.resetUploadedImageUrl()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MutedTerracotta,
                            contentColor = Color.White
                        ),
                        enabled = uploadedImageUrl != null && isFormValid
                    ) {
                        Text(stringResource(R.string.add_product))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                ProductList(
                    products = products,
                    onDelete = { mangalViewModel.deleteProduct(it) },
                    modifier = Modifier
                        .weight(0.5f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductForm(
    productName: String,
    onProductNameChange: (String) -> Unit,
    productPrice: String,
    onProductPriceChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    productDescription: String,
    onProductDescriptionChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = productName,
            onValueChange = onProductNameChange,
            label = { Text(stringResource(R.string.product_name)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp,)
        )
        OutlinedTextField(
            value = productPrice,
            onValueChange = onProductPriceChange,
            label = { Text(stringResource(R.string.product_price)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        var expanded by remember { mutableStateOf(false) }
        fun Modifier.simpleFieldStyle(): Modifier = this
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        val textFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = DarkBrown,
            cursorColor = DarkBrown
        )
        val categories = listOf(
            "Мангальные зоны", "Костровые чаши", "Флюгера", "Адресные таблички",
            "Мангалы в виде животных", "Дымники", "Костровые Сферы", "Дровницы",
            "Чан сибирский", "Сотовый стол для лазерного станка"
        )
        OutlinedTextField(
            value = productDescription,
            onValueChange = onProductDescriptionChange,
            label = { Text(stringResource(R.string.product_description)) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.simpleFieldStyle()
        ) {
            TextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.category), fontFamily = fontFamily) },
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier.menuAnchor(),
                colors = textFieldColors,
                textStyle = TextStyle(fontFamily = fontFamily)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat, fontFamily = fontFamily) },
                        onClick = {
                            onCategoryChange(cat)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun ProductList(
    products: List<Product>,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(products) { product ->
            AdminProductItem(product = product, onDelete = onDelete)
        }
    }
}


@Composable
fun AdminProductItem(product: Product, onDelete: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "${stringResource(R.string.name)}: ${product.name}", fontWeight = FontWeight.Bold)
            Text(text = "${stringResource(R.string.price)}: ${product.price}")
        }
        Column {
            Button(
                onClick = { onDelete(product.id) },
                modifier = Modifier.padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MutedTerracotta,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}


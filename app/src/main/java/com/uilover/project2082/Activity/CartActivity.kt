package com.uilover.project2082.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.database.core.view.Change
import com.uilover.project2082.Model.ItemsModel
import com.uilover.project2082.R


class CartActivity : BaseActivity() {
    private val managmentCart by lazy { ManagmentCart(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CartScreen(
                managmentCart = managmentCart,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
private fun CartScreen(
    managmentCart: ManagmentCart = ManagmentCart(LocalContext.current),
    onBackClick: () -> Unit
) {
    // Observa los cambios en la lista del carrito
    val cartItems = remember { mutableStateOf(managmentCart.getListCart()) }
    val tax = remember { mutableStateOf(0.0) }

    // Calcula el tax inicial
    LaunchedEffect(Unit) {
        calculatorCart(managmentCart, tax)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .size(24.dp)
            )
            Text(
                text = "Your Cart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            // Espacio vacío para centrar el título
            Box(modifier = Modifier.size(24.dp))
        }

        if (cartItems.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cart Is Empty",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(cartItems.value) { item ->
                    CartItem(
                        item = item,
                        onPlusClick = {
                            managmentCart.plusItem(cartItems.value, cartItems.value.indexOf(item),
                                object : ChangeNumberItemsListener {
                                    override fun onChanged() {
                                        cartItems.value = managmentCart.getListCart()
                                        calculatorCart(managmentCart, tax)
                                    }
                                })
                        },
                        onMinusClick = {
                            managmentCart.minusItem(cartItems.value, cartItems.value.indexOf(item),
                                object : ChangeNumberItemsListener {
                                    override fun onChanged() {
                                        cartItems.value = managmentCart.getListCart()
                                        calculatorCart(managmentCart, tax)
                                    }
                                })
                        }
                    )
                }
            }

            // Resumen y botón de checkout
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Item Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Item Total:", color = Color.Gray)
                    Text("$${managmentCart.getTotalFee()}")
                }

                // Tax
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tax:", color = Color.Gray)
                    Text("$${tax.value}")
                }

                // Delivery
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery:", color = Color.Gray)
                    Text("$10.0")
                }

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.LightGray
                )

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total:", fontWeight = FontWeight.Bold)
                    Text(
                        "$${managmentCart.getTotalFee() + tax.value + 10.0}",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Checkout Button
                Button(
                    onClick = { /* Implementar checkout */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Check Out", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CartItem(
    item: ItemsModel,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen del producto
        Image(
            painter = rememberAsyncImagePainter(item.picUrl[0]),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        )

        // Información del producto
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$${item.price}",
                color = Color(0xFF6200EE)
            )
            Text(
                text = "$${item.price * item.numberInCart}",
                fontWeight = FontWeight.Bold
            )
        }

        // Controles de cantidad
        Row(
            modifier = Modifier
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .clickable(onClick = onMinusClick),
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontSize = 18.sp)
            }

            Text(
                text = "${item.numberInCart}",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF6200EE), RoundedCornerShape(6.dp))
                    .clickable(onClick = onPlusClick),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

fun calculatorCart(managmentCart: ManagmentCart, tax: MutableState<Double>) {
    val percentTax = 0.2
    tax.value = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0
}

@Composable
fun CartList(cartItems:ArrayList<ItemsModel>,
             managmentCart: ManagmentCart,
             onItemChange:()->Unit) {
    LazyColumn (Modifier.padding(top = 15.dp) ){
        items(cartItems){item->
            CartItem(cartItems,item=item,
                managmentCart=managmentCart,
                onItemChange=onItemChange
            )
        }
    }
}

@Composable
fun CartItem(
    cartItems: ArrayList<ItemsModel>,
    item:ItemsModel,managmentCart: ManagmentCart,
    onItemChange: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ){
        val (pic,titleTxt,feeEachTime,totalEachItem,Quantity)=createRefs()
        Image(
            painter = rememberAsyncImagePainter(item.picUrl[0]),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .background(colorResource(R.color.lightGrey),
                    shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
                .constrainAs(pic){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(
            text = item.title,
            modifier = Modifier
                .constrainAs(titleTxt){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(text = "$${item.price}", color = colorResource(R.color.purple),
            modifier = Modifier
                .constrainAs(feeEachTime){
                    start.linkTo(titleTxt.start)
                    top.linkTo(titleTxt.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(
            text = "$${item.numberInCart*item.price}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier
                .constrainAs(totalEachItem){
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(titleTxt.bottom)
                }
                .padding(start = 8.dp)
        )

        ConstraintLayout(modifier = Modifier
            .width(100.dp)
            .constrainAs(Quantity){
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .background(colorResource(R.color.lightGrey), shape = RoundedCornerShape(10.dp))
        ){
          val(plusCartBtn,minusCartBtn,numberItemTxt)=createRefs()
            Text(text = item.numberInCart.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(numberItemTxt){
                    end.linkTo(parent.end)
                    start.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            Box(modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
                .background(colorResource(R.color.purple),
                    shape = RoundedCornerShape(10.dp))
                .constrainAs(plusCartBtn){
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable {
                    managmentCart.plusItem(cartItems,cartItems.indexOf(item),
                        object :ChangeNumberItemsListener{
                            override fun onChanged() {
                                onItemChange()
                            }

                        })
                }
            ){
                Text(
                    text = "+",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(colorResource(R.color.white),
                        shape = RoundedCornerShape(10.dp))
                    .constrainAs(minusCartBtn){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        managmentCart.minusItem(cartItems,
                            cartItems.indexOf(item),object :ChangeNumberItemsListener{
                                override fun onChanged() {
                                    onItemChange()
                                }

                            }
                        )
                    }
            ){
                Text(
                    text = "-",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                    )
            }
        }

    }

}



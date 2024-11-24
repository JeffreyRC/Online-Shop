package com.uilover.project2082.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.uilover.project2082.Model.CategoryModel
import com.uilover.project2082.Model.ItemsModel
import com.uilover.project2082.Model.SliderModel
import com.uilover.project2082.R
import com.uilover.project2082.ViewModel.MainViewModel


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
        MainActivityScreen {
    startActivity(Intent(this,CartActivity::class.java))
        }
        }
    }
}


@Composable
fun MainActivityScreen(onCartClick:()->Unit){
    val viewModel=MainViewModel()
    val context = LocalContext.current
    val banners= remember { mutableStateListOf<SliderModel>() }
    val categories= remember { mutableStateListOf<CategoryModel>() }
    val recommended= remember { mutableStateListOf<ItemsModel>() }
    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showRecommendedLoading by remember { mutableStateOf(true) }

    //Banner
    LaunchedEffect(Unit) {
        viewModel.loadBanners()
        viewModel.banners.observeForever{
            banners.clear()
            banners.addAll(it)
            showBannerLoading=false
        }
    }

    //Category
    LaunchedEffect(Unit) {
        viewModel.loadCategory()
        viewModel.categories.observeForever{
            categories.clear()
            categories.addAll(it)
            showCategoryLoading = false

        }
    }

    //Recommended
    LaunchedEffect(Unit) {
        viewModel.loadRecommended()
        viewModel.recommended.observeForever{
            recommended.clear()
            recommended.addAll(it)
            showRecommendedLoading = false
        }
    }

    ConstraintLayout (modifier = Modifier.background(Color.White)){
        val (scrollList,bottomMenu)=createRefs()
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ){
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column {
                        Text("Welcome Back", color = Color.Black)

                        Text("Jackie",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                            )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.fav_icon),
                            contentDescription = "",
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = "",
                        )
                    }
                }
            }
            
            //Banners
            item {
                if (showBannerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    Banners(banners)
                }
            }
            item {
                SectionTitle("Categories", "See All")
            }
            item{
                if (showCategoryLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }else{
                    CategoryList(categories)
                }
            }
            item {
                SectionTitle("Recommendation", "See All")
            }
            item {
                if (showRecommendedLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }else{
                    ListItems(recommended)
                }
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu) {
                    bottom.linkTo(parent.bottom)
                },
            onItemClick = onCartClick
        )
    }
}

@Composable
fun CategoryList(categories: SnapshotStateList<CategoryModel>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val context= LocalContext.current
    LazyRow (modifier = Modifier
        .fillMaxWidth()
        , horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {

        items(categories.size){ index ->
            CategoryItem(item = categories[index],
                isSelected = selectedIndex==index,
                onItemClick = {
                    selectedIndex=index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent=Intent(context,ListItemsActivity::class.java).apply {
                            putExtra("id",categories[index].id.toString())
                            putExtra("title",categories[index].title)
                        }
                        startActivity(context,intent,null)
                    },1000)
                })
        }
    }
}

@Composable
fun CategoryItem(item:CategoryModel,isSelected:Boolean,onItemClick:()->Unit) {
    Row (modifier = Modifier
        .clickable(onClick = onItemClick)
        .background(
            color = if (isSelected) colorResource(R.color.purple) else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
        ),
        verticalAlignment =  Alignment.CenterVertically
    ){
        AsyncImage(
            model=(item.picUrl),
            contentDescription = item.title,

            modifier = Modifier
                .size(45.dp)
                .background(
                    color = if (isSelected) Color.Transparent else colorResource(R.color.lightGrey),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if (isSelected){
                ColorFilter.tint(Color.White)

            }else{
                ColorFilter.tint(Color.Black)
            })
        if (isSelected){
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier =  Modifier.padding(end = 8.dp)
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: List<SliderModel>) {
        AutoSlidingCarousel(banners = banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier=Modifier,
    pagerState: PagerState= remember { PagerState() },
    banners: List<SliderModel>
) {

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    Column ( modifier = modifier.fillMaxSize()){
        HorizontalPager(count = banners.size, state = pagerState) {
            page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription = null ,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    .height(150.dp)
            )
        }

        DotIndicator(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = if(isDragged) pagerState.currentPage else pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}

@Composable
fun DotIndicator(
    modifier: Modifier=Modifier,
    totalDots:Int,
    selectedIndex:Int,
    selectedColor: Color= colorResource(R.color.purple),
    unSelectedColor: Color= colorResource(R.color.grey),
    dotSize: Dp
){
    LazyRow (
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ){
        items(totalDots){
            index ->
            IndicatorDot(
            color = if ( index == selectedIndex )selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index!=totalDots-1){
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(modifier: Modifier=Modifier,
                 size:Dp,
                 color: Color
                 ) {
        Box(modifier= modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
        )
}
@Composable
fun SectionTitle(title:String, actionText:String){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text=title,
            color=Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = actionText,
            color = colorResource(R.color.purple)
        )
    }
}
@Composable
fun BottomMenu(modifier: Modifier, onItemClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(
                    colorResource(R.color.purple),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuItem(icon = painterResource(R.drawable.btn_1), text = "Explorer")
            BottomMenuItem(icon = painterResource(R.drawable.btn_2), text = "Cart", onItemClick = onItemClick)
            BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Favorite")
            BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Orders")
            BottomMenuItem(icon = painterResource(R.drawable.btn_5), text = "Profile")
        }
    }
}

@Composable
fun BottomMenuItem(icon: Painter, text: String, onItemClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .clickable { onItemClick?.invoke() }
            .padding(8.dp, 4.dp)
            .height(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp
        )
    }
}

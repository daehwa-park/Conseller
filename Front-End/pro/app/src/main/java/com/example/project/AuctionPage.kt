package com.example.project

import FilterButton
import FormattedDateText
import PaginationControls
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.project.api.AuctionFilterDTO
import com.example.project.ui.theme.logocolor
import com.example.project.viewmodels.AuctionViewModel
import convertNameToNum

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AuctionPage(navController: NavHostController) {
    val auctionViewModel: AuctionViewModel = hiltViewModel()
    val auctionItems by auctionViewModel.auctionItems.collectAsState()
    val error by auctionViewModel.error.collectAsState()
    val scrollState = rememberScrollState()

    var currentPage by remember { mutableIntStateOf(1) } // 현재 페이지 초기값
    val itemsPerPage = 10 // 페이지 당 표시할 항목 수

    var showSnackbar by remember { mutableStateOf(false) } // 에러처리스낵바
    var snackbarText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        auctionViewModel.fetchAuctionItems()
    }
    LaunchedEffect(error) {
        if (error != null) {
            showSnackbar = true
            snackbarText = error!!
        }
    }
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            kotlinx.coroutines.delay(5000)
            showSnackbar = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                if (showSnackbar) {
                    Snackbar(
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        Text(text = snackbarText, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 검색창
                    var searchText by remember { mutableStateOf("") }
                    val keyboardController = LocalSoftwareKeyboardController.current
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    auctionViewModel.searchItems(searchText)
                                    keyboardController?.hide() // 키보드 숨기기
                                }
                                    .padding(16.dp)
                            )
                        },
                        placeholder = { Text("검색") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            auctionViewModel.searchItems(searchText)
                            keyboardController?.hide() // 키보드 숨기기
                        })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    var filter1Selected by remember { mutableStateOf("대분류") } // 필터1의 초기값
                    var filter2Selected by remember { mutableStateOf("소분류") } // 필터2의 초기값
                    var filter3Selected by remember { mutableStateOf("등록일") } // 필터3의 초기값

                    // 필터 버튼
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterButton(
                                selectedOption = filter1Selected,
                                options = listOf(
                                    "대분류",
                                    "버거/치킨/피자",
                                    "편의점",
                                    "카페/베이커리",
                                    "아이스크림",
                                    "기타"
                                ),
                            ) {
                                filter1Selected = it
                                filter2Selected = "소분류"
                                val (filter1Id, filter2Id, filter3Id) = convertNameToNum(
                                    filter1Selected,
                                    filter2Selected,
                                    filter3Selected
                                )
                                auctionViewModel.applyFilter(
                                    AuctionFilterDTO(
                                        filter1Id,
                                        filter2Id,
                                        filter3Id,
                                        searchText,
                                        currentPage
                                    )
                                )
                            }

                        }
                        item {
                            FilterButton(
                                selectedOption = filter2Selected,
                                options = when (filter1Selected) {
                                    "버거/치킨/피자" -> listOf("전체", "버거", "치킨", "피자")
                                    "편의점" -> listOf("전체", "금액권", "과자", "음료", "도시락/김밥류", "기타")
                                    "카페/베이커리" -> listOf("전체", "카페", "베이커리", "기타")
                                    "아이스크림" -> listOf("전체", "베스킨라빈스", "기타")
                                    "기타" -> listOf("전체")
                                    else -> listOf("전체")
                                }
                            ) {
                                filter2Selected = it
                                val (filter1Id, filter2Id, filter3Id) = convertNameToNum(
                                    filter1Selected,
                                    filter2Selected,
                                    filter3Selected
                                )
                                auctionViewModel.applyFilter(
                                    AuctionFilterDTO(
                                        filter1Id,
                                        filter2Id,
                                        filter3Id,
                                        searchText,
                                        currentPage
                                    )
                                )
                            }
                        }
                        item {
                            FilterButton(
                                selectedOption = filter3Selected,
                                options = listOf("등록일", "유효기한", "입찰가", "즉시구입가")
                            ) {
                                filter3Selected = it
                                val (filter1Id, filter2Id, filter3Id) = convertNameToNum(
                                    filter1Selected,
                                    filter2Selected,
                                    filter3Selected
                                )
                                auctionViewModel.applyFilter(
                                    AuctionFilterDTO(
                                        filter1Id,
                                        filter2Id,
                                        filter3Id,
                                        searchText,
                                        currentPage
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(logocolor)
            )

            // AuctionItem 넣기
            auctionItems.forEach { item ->
                AuctionItem(
                    image = item.gifticonDataImageName,
                    name = item.gifticonName,
                    gifticonTime = item.gifticonEndDate,
                    auctionTime = item.auctionEndDate,
                    isDeposit = item.deposit,
                    upperprice = item.upperPrice,
                    nowprice = item.auctionHighestBid,
                    onItemClick = {
                        navController.navigate("AuctionDetailPage/${item.auctionIdx}")
                        Log.d("AuctionPage", "auctionIdx: ${item.auctionIdx}")

                    }

                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 페이지네이션 컨트롤
            PaginationControls(
                totalItems = auctionItems.size,
                currentPage = currentPage,
                itemsPerPage = itemsPerPage
            ) { newPage ->
                currentPage = newPage
                auctionViewModel.changePage(newPage)  // 페이지 변경 시 데이터 받기
            }
        }
        // 등록하기
        FloatingActionButton(
            onClick = { navController.navigate("AuctionCreatePage") },
            modifier = Modifier
                .align(Alignment.BottomEnd) // 우하단
                .padding(16.dp) // 화면의 가장자리 간격
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                // 글자 크기 조절
                Text("등록하기", fontSize = 18.sp)
            }

        }
    }
}


@Composable
fun AuctionItem(
    image: String,
    name: String,
    gifticonTime: String,
    auctionTime: String,
    isDeposit: Boolean,
    upperprice: Int,
    nowprice: Int,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White)
            .padding(8.dp)
            .clickable { onItemClick() }
    ) {
        // 65% 이미지
        Box(
            modifier = Modifier
                .weight(0.65f)
                .fillMaxWidth()
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            // 이미지
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        // 10% 이름 및 유효기간
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            FormattedDateText(gifticonTime,"유효기간")
        }

        // 구분 줄
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color.Gray, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(modifier = Modifier.height(4.dp))

        // 15% 박스1
        Box(
            modifier = Modifier
                .weight(0.2f)
                .padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 40% 인기
                Text(
                    text = if (isDeposit) "보증금 있음" else "보증금 없음",
                    modifier = Modifier.weight(0.4f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                // 60% 박스2
                Column(
                    modifier = Modifier.weight(0.6f),
                    horizontalAlignment = Alignment.End
                ) {
                    // 30% 즉시구매가
                    Text("즉시구매가 : $upperprice 원", modifier = Modifier.weight(0.4f), fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    // 70% 현재입찰가
                    Text("현재입찰가 : $nowprice 원", modifier = Modifier.weight(0.6f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // 5% 경매기간
        FormattedDateText(
            gifticonTime = auctionTime,
            prefix = "마감일",
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
    }
}

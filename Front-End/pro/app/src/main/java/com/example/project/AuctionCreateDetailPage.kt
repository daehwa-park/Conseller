package com.example.project

import SelectButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.project.viewmodels.AuctionViewModel
import com.example.project.viewmodels.MygifticonViewModel
import formattedNumber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AuctionCreateDetailPage(navController: NavHostController, selectedItemIndex: String?, mygifticonViewModel: MygifticonViewModel) {
    val auctionViewModel: AuctionViewModel = hiltViewModel()
    val gifticonItems by mygifticonViewModel.gifticonItems.collectAsState()
    val navigateToIdx by auctionViewModel.navigateToAuctionDetail.collectAsState() // 등록시 idx받기
    val error by auctionViewModel.error.collectAsState()
    val myerror by mygifticonViewModel.error.collectAsState()

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val selectedGifticon = gifticonItems?.find { it.gifticonIdx == selectedItemIndex!!.toLong() }

    var showSnackbar by remember { mutableStateOf(false) } // 에러처리스낵바
    var snackbarText by remember { mutableStateOf("") }

    // 상한가, 하한가, 게시글 내용을 위한 상태값
    var upperPrice by remember { mutableStateOf(0) }
    var lowerPrice by remember { mutableStateOf(0) }
    var postContent by remember { mutableStateOf("") }
    var showRegisterConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        if (error != null) {
            showSnackbar = true
            snackbarText = error!!
        }
    }
    LaunchedEffect(myerror) {
        if (myerror != null) {
            showSnackbar = true
            snackbarText = myerror!!
        }
    }
    LaunchedEffect(navigateToIdx) {
        navigateToIdx?.let { auctionIdx ->
            auctionViewModel.resetNavigation()
            navController.navigate("AuctionDetailPage/${auctionIdx}")
        }
    }
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            kotlinx.coroutines.delay(5000)
            showSnackbar = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (showSnackbar) {
            Snackbar(
            ) {
                Text(text = snackbarText, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
        // 내용 입력 부분
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "경매 글 등록",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            selectedGifticon?.let {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val imagePainter = rememberAsyncImagePainter(model = it.gifticonImageName)
                    Image(
                        painter = imagePainter,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.gifticonName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("상한가", modifier = Modifier.padding(bottom = 8.dp), fontSize = 20.sp)
            OutlinedTextField(
                value = formattedNumber(upperPrice.toString()),
                onValueChange = { newValue ->
                    val pureValue = newValue.filter { it.isDigit() }
                    // 숫자만 입력되도록 체크
                    if (pureValue.isEmpty()) {
                        upperPrice = 0
                    } else if (pureValue.all { it.isDigit() }) {
                        val potentialValue = pureValue.toLong()
                        if (potentialValue <= 1_000_000_000) {  // 10억으로 제한
                            upperPrice = potentialValue.toInt()
                        } else {
                            upperPrice = 1_000_000_000
                        }
                    }
                    if (upperPrice <= lowerPrice) {
                        upperPrice = lowerPrice + 1
                        showSnackbar = true
                        snackbarText = "상한가는 하한가보다 높아야 합니다."
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide() // 키보드 숨기기
                }),
                placeholder = {
                    Text(text = "원단위로 숫자만 입력해주세요", color = Color.Gray)
                },
                trailingIcon = {
                    Text(text = "원")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("하한가", modifier = Modifier.padding(bottom = 8.dp), fontSize = 20.sp)
            OutlinedTextField(
                value = formattedNumber(lowerPrice.toString()),
                onValueChange = { newValue ->
                    val pureValue = newValue.filter { it.isDigit() }
                    // 숫자만 입력되도록 체크
                    if (pureValue.isEmpty()) {
                        lowerPrice = 0
                    } else if (pureValue.all { it.isDigit() }) {
                        val potentialValue = pureValue.toLong()
                        if (potentialValue <= 1_000_000_000) {  // 10억으로 제한
                            lowerPrice = potentialValue.toInt()
                        } else {
                            lowerPrice = 1_000_000_000
                        }
                    }
                    if (lowerPrice >= upperPrice) {
                        lowerPrice = upperPrice - 1
                        showSnackbar = true
                        snackbarText = "하한가는 상한가보다 낮아야 합니다."
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide() // 키보드 숨기기
                }),
                placeholder = {
                    Text(text = "원단위로 숫자만 입력해주세요", color = Color.Gray)
                },
                trailingIcon = {
                    Text(text = "원")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("게시글 내용", modifier = Modifier.padding(bottom = 8.dp), fontSize = 20.sp)
            OutlinedTextField(
                value = postContent,
                onValueChange = { postContent = it },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Red,
                    unfocusedBorderColor = Color.Gray
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide() // 키보드 숨기기
                })
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 이전, 등록 버튼 부분
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SelectButton(
                text = "이전",
                onClick = {
                    navController.navigate("AuctionCreatePage")
                },
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp)
            )

            Spacer(modifier = Modifier.width(24.dp))

            SelectButton(
                text = "등록",
                onClick = {
                    showRegisterConfirmDialog = true
                },
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp, minHeight = 50.dp)
            )
        }

        // 등록 확인 대화상자
        if (showRegisterConfirmDialog) {
            AlertDialog(
                onDismissRequest = {
                    showRegisterConfirmDialog = false
                },
                title = {
                    Text(text = "게시글 등록")
                },
                text = {
                    Text("등록하시겠습니까?")
                },
                confirmButton = {
                    SelectButton(
                        text = "아니오",
                        onClick = {
                            showRegisterConfirmDialog = false
                        }
                    )
                },
                dismissButton = {
                    SelectButton(
                        text = "예",
                        onClick = {
                            val selectedIndex: Long = selectedGifticon?.gifticonIdx ?: -1L
                            auctionViewModel.registerAuctionItem(
                                upperPrice,
                                lowerPrice,
                                postContent,
                                selectedIndex
                            )
                            showRegisterConfirmDialog = false
                        }
                    )
                }
            )
        }
    }
}
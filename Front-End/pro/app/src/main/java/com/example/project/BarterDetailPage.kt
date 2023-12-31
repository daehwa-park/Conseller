package com.example.project

import FormattedDateDot
import SelectButton
import UserDetailDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.project.viewmodels.BarterViewModel

@Composable
fun BarterDetailPage(index: String?, navController: NavHostController) {
    val barterViewModel: BarterViewModel = hiltViewModel()
    val barterDetail by barterViewModel.barterDetail.collectAsState()   // 상세보기로 들고온 리스트
    val scrollState = rememberScrollState()
    val error by barterViewModel.error.collectAsState()
    val barterCancelNavi by barterViewModel.barterCancelNavi.collectAsState()
    val userIdFromPreference = barterViewModel.getUserIdFromPreference()

    var selectedItemIndex by remember { mutableStateOf(userIdFromPreference) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteRequestDialog by remember { mutableStateOf(false) }
    var showUserDetailDialog by remember { mutableStateOf(false) } // 유저 자세히보기

    var showSnackbar by remember { mutableStateOf(false) } // 에러처리스낵바
    var snackbarText by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        index?.toLongOrNull()?.let {
            barterViewModel.fetchBarterDetail(it)
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            showSnackbar = true
            snackbarText = error!!
        }
    }
    LaunchedEffect(barterCancelNavi) {
        if (barterCancelNavi != null) {
            navController.navigate("BarterDetailPage/${index}")
        }
    }
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            kotlinx.coroutines.delay(5000)
            showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = if (showSnackbar) 50.dp else 8.dp)
    ) {
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Text(
                    text = snackbarText,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {
            barterDetail?.let { detail ->

                // 테두리를 위한 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "${barterDetail?.barterName}",
                                fontSize = 18.sp,
                                color = Color.DarkGray
                                , fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier=Modifier.height(4.dp))
                        // 이미지들을 보여주는 LazyRow
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(detail.barterImageList) { imageItem ->
                                val imagePainter =
                                    rememberAsyncImagePainter(model = imageItem.gifticonDataImageName)
                                Image(
                                    painter = imagePainter,
                                    contentDescription = null,
                                    modifier = Modifier.size(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // gifticonName들을 나열
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(detail.barterImageList) { imageItem ->
                                Text(
                                    text = "${imageItem.gifticonName} /",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )

                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // gifticonEndDate들을 나열
                        detail.barterImageList.forEach { imageItem ->
                            FormattedDateDot(
                                imageItem.gifticonEndDate, fontSize = 18.sp, label = "물물교환 기한 :"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 판매자 이름
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "판매자 : ${detail.barterUserNickname}",
                                fontSize = 18.sp,
                                modifier = Modifier.clickable(indication = rememberRipple(),
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    showUserDetailDialog = true
                                },
                                textDecoration = TextDecoration.Underline
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("원하는 품목 : ${barterDetail?.preper}", fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "물물교환 설명",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("${barterDetail?.barterText}", fontSize = 18.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (selectedItemIndex != barterDetail?.barterUserIdx) {
                    if (barterDetail?.barterRequestIdx?.toInt() == 0) {
                        SelectButton(text = "제안하기",
                            onClick = { navController.navigate("BarterTradeSelectPage/${index}") })
                    } else {
                        SelectButton(text = "제안 취소하기", onClick = { showDeleteRequestDialog = true })
                    }
                } else {
                    Spacer(modifier = Modifier.width(24.dp))

                    SelectButton(text = "수정하기",
                        onClick = { navController.navigate("barterUpdate/${index}") })

                    Spacer(modifier = Modifier.width(24.dp))

                    SelectButton(text = "삭제하기", onClick = { showDeleteDialog = true })
                }
            }

            if (showDeleteDialog) {
                AlertDialog(onDismissRequest = {
                    showDeleteDialog = false
                }, title = {
                    Text(text = "게시글 삭제")
                }, text = {
                    Text("정말 삭제하시겠습니까?", fontSize = 18.sp)
                }, dismissButton = {
                    SelectButton(text = "네", onClick = {
                        barterViewModel.deleteBarterItem(index!!.toLong())
                        if (error == null) {
                            navController.navigate("BarterPage")
                            showDeleteDialog = false
                        }
                    })
                }, confirmButton = {
                    SelectButton(text = "아니오", onClick = { showDeleteDialog = false })
                })
            }
            // 판매자 상세보기
            if (showUserDetailDialog) {
                UserDetailDialog(userImageUrl = barterDetail?.barterUserProfileUrl,
                    userNickname = barterDetail?.barterUserNickname,
                    userDeposit = barterDetail?.barterUserDeposit,
                    onDismiss = { showUserDetailDialog = false },
                    onReportClick = {
                        navController.navigate("Inquiry/${barterDetail?.barterUserIdx}")
                    },
                    onMessageClick = {
                        // Handle message sending logic here
                    })
            }
            //판매자 삭제
            if (showDeleteRequestDialog) {
                AlertDialog(onDismissRequest = {
                    showDeleteDialog = false
                }, title = {
                    Text(text = "제안 삭제")
                }, text = {
                    Text("정말 제안을 취소하시겠습니까?", fontSize = 18.sp)
                }, dismissButton = {
                    SelectButton(text = "네", onClick = {
                        barterViewModel.proposeCancleBarterTrade(barterDetail?.barterRequestIdx)
                    })
                }, confirmButton = {
                    SelectButton(text = "아니오", onClick = { showDeleteRequestDialog = false })
                })
            }
        }
    }
}

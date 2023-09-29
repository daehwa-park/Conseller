package com.example.project

import SelectButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.project.viewmodels.BarterViewModel
import kotlinx.coroutines.delay

@Composable
fun BarterTradePage(navController: NavHostController, selectedItemIndices: List<Long>, index: String?) {
    val barterViewModel: BarterViewModel = hiltViewModel()
    val selectedItems = barterViewModel.getSelectedItems(selectedItemIndices) // 내가 고른사진
    val barterDetail by barterViewModel.barterDetail.collectAsState()   // 게시글 정보
    val error by barterViewModel.error.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) } // 취소 대화상자 표시 상태
    var showTradeProposalDialog by remember { mutableStateOf(false) } //거래신청 대화상자

    var showSnackbar by remember { mutableStateOf(false) } // 에러처리스낵바
    var snackbarText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = index) {
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
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            delay(5000)
            showSnackbar = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showSnackbar) {
            Snackbar() {
                Text(
                    text = snackbarText,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
            }
        }

        // 게시글의 이미지들을 보여주는 LazyRow
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(barterDetail?.barterImageList.orEmpty()) { item ->
                val imagePainter = rememberAsyncImagePainter(model = item.gifticonDataImageName)
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 선택한 내 이미지들을 보여주는 LazyRow
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedItems) { item ->
                Image(
                    painter = rememberAsyncImagePainter(model = item.gifticonDataImageName),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectButton(
                text = "거래신청",
                onClick = { showTradeProposalDialog = true },
                modifier = Modifier.weight(1f)
            )

            SelectButton(
                text = "취소하기",
                onClick = { showCancelDialog = true },
                modifier = Modifier.weight(1f)
            )
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCancelDialog = false
                },
                title = {
                    Text(text = "거래 취소")
                },
                text = {
                    Text("거래를 그만두고 돌아가시겠습니까?")
                },
                dismissButton = {
                    SelectButton(
                        text = "네",
                        onClick = {
                            if(error == null) {
                                navController.navigate("BarterDetailPage/${index}")
                            }
                        }
                    )
                },
                confirmButton = {
                    SelectButton(
                        text = "아니오",
                        onClick = {
                            showCancelDialog = false
                        }
                    )
                }
            )
        }

        if (showTradeProposalDialog) {
            AlertDialog(
                onDismissRequest = {
                    showTradeProposalDialog = false
                },
                title = {
                    Text(text = "거래 제안")
                },
                text = {
                    Text("거래를 제안하시겠습니까?")
                },
                dismissButton = {
                    SelectButton(
                        text = "예",
                        onClick = {
                            showTradeProposalDialog = false
                            barterViewModel.proposeBarterTrade(
                                index?.toLongOrNull() ?: return@SelectButton, selectedItemIndices
                            )
                            // TODO: 거래 제안 결과에 따른 메시지 처리 로직 추가
                        }
                    )
                },
                confirmButton = {
                    SelectButton(
                        text = "아니오",
                        onClick = {
                            showTradeProposalDialog = false
                        }
                    )
                }
            )
        }
    }
}
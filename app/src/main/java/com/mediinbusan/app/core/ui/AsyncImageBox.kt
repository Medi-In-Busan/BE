package com.mediinbusan.app.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.mediinbusan.app.core.designsystem.CoralPrimary

/**
 * F-019: 이미지가 없는 병원/장소 카드에서도 화면이 깨지지 않도록 감싸는 공용 컴포넌트.
 * 그리드를 아래로 스크롤했다 다시 위로 올릴 때, 화면 밖으로 나갔던 카드의 이미지가 Coil
 * 메모리 캐시에서 밀려나 다시 요청되는 경우가 있다 — 로딩 중엔 스피너를, 재요청이 실패했을
 * 때(Error)는 깨진 이미지 아이콘을 보여준다. 둘 다 없으면 카드 배경색만 남아 "빈 흰 카드"처럼
 * 보이는데, 특히 Error는 영영 그 상태로 남는다.
 *
 * SubcomposeAsyncImage는 로딩/에러 슬롯마다 서브컴포지션을 새로 돌기 때문에, 그리드를 스크롤하며
 * 카드가 계속 재구성되는 이 화면에서 프레임 드랍(jank)을 유발한다 — 대신 plain AsyncImage +
 * onState 콜백으로 상태만 별도로 들고 있다가 오버레이를 그린다.
 */
@Composable
fun AsyncImageBox(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var state by remember(model) { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }
    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            onState = { state = it }
        )
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = CoralPrimary,
                        strokeWidth = 2.dp
                    )
                }
            }
            is AsyncImagePainter.State.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.BrokenImage,
                        contentDescription = null,
                        tint = CoralPrimary.copy(alpha = 0.35f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            else -> Unit
        }
    }
}

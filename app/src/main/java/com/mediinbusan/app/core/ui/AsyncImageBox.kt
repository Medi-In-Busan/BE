package com.mediinbusan.app.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.mediinbusan.app.core.designsystem.CoralPrimary

/**
 * F-019: 이미지가 없는 병원/장소 카드에서도 화면이 깨지지 않도록 감싸는 공용 컴포넌트.
 * 그리드를 아래로 스크롤했다 다시 위로 올릴 때, 화면 밖으로 나갔던 카드의 이미지가 Coil
 * 메모리 캐시에서 밀려나 다시 요청되는 경우가 있다 — 로딩 중엔 스피너를, 재요청이 실패했을
 * 때(Error)는 깨진 이미지 아이콘을 보여준다. 둘 다 없으면 카드 배경색만 남아 "빈 흰 카드"처럼
 * 보이는데, 특히 Error는 영영 그 상태로 남는다.
 *
 * onState 콜백으로 별도 상태 변수를 들고 있던 이전 버전 대신 SubcomposeAsyncImage를 쓴다 —
 * Coil이 공식적으로 안내하는 로딩/에러 슬롯 API라 painter 상태와 화면에 그려지는 내용이
 * 어긋날 여지가 없다.
 */
@Composable
fun AsyncImageBox(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.fillMaxSize(),
        loading = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = CoralPrimary,
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.BrokenImage,
                    contentDescription = null,
                    tint = CoralPrimary.copy(alpha = 0.35f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    )
}

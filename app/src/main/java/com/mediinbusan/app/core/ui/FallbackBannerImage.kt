package com.mediinbusan.app.core.ui

import com.mediinbusan.app.R

// 실제 사진(imageUrl)이 없는 병원/즐겨찾기/최근 본 항목 카드에 채워 넣는 대체 이미지. Home 배너와
// 같은 리소스를 재사용한다 — itemId 기준으로 고정 배정해서 같은 항목은 스크롤/재조회해도 항상
// 같은 배너를 보여준다.
private val FallbackBannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)

fun fallbackBannerImageFor(itemId: String): Int =
    FallbackBannerImages[(itemId.hashCode() and Int.MAX_VALUE) % FallbackBannerImages.size]

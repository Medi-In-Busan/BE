package com.mediinbusan.app.data.recent

/**
 * F-016 최근 본 항목 전용 타입. `data/favorite/FavoriteItemType`(HOSPITAL, PLACE)와 별개로 둔다 —
 * Favorite 기능은 관광 카탈로그 항목을 즐겨찾기하지 않으므로, 여기서만 필요한 TOURISM_ITEM을
 * 억지로 FavoriteItemType에 추가해 Favorite 쪽 exhaustive when까지 건드리지 않기 위함이다.
 */
enum class RecentItemType { HOSPITAL, PLACE, TOURISM_ITEM }

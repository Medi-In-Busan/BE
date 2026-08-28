package com.mediinbusan.app.core.i18n

import com.mediinbusan.app.core.datastore.SupportedLanguage
import com.mediinbusan.app.domain.tourism.BusanDistrict
import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismCatalogGroup

/**
 * TourismCatalogCategory.label(한국어)은 카테고리 식별자로 계속 쓰고, 화면에 그릴 때만 이 함수로
 * 번역한다(core/i18n/MedicalCategoryStrings.kt의 MedicalCategory.translatedLabel과 같은 패턴).
 */
fun TourismCatalogCategory.translatedLabel(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> label
    SupportedLanguage.EN -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "Busan Tourist Spots"
        TourismCatalogCategory.ACCESSIBLE -> "Accessible Tourism"
        TourismCatalogCategory.PHOTOS -> "Busan Photo Gallery"
        TourismCatalogCategory.PLACES_EN -> "Busan in English"
        TourismCatalogCategory.PLACES_JA -> "Busan in Japanese"
        TourismCatalogCategory.PLACES_ZH -> "Busan in Chinese"
        TourismCatalogCategory.RELATED -> "Related Attractions"
        TourismCatalogCategory.HUBS -> "Regional Tourism Hubs"
        TourismCatalogCategory.WALKING -> "Busan Walking Trails"
        TourismCatalogCategory.AUDIO -> "Audio Tours"
        TourismCatalogCategory.CROWDING -> "Crowding Forecast"
    }
    SupportedLanguage.ZH -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "釜山旅游景点"
        TourismCatalogCategory.ACCESSIBLE -> "无障碍旅游"
        TourismCatalogCategory.PHOTOS -> "釜山旅游相册"
        TourismCatalogCategory.PLACES_EN -> "英文釜山旅游"
        TourismCatalogCategory.PLACES_JA -> "日文釜山旅游"
        TourismCatalogCategory.PLACES_ZH -> "中文釜山旅游"
        TourismCatalogCategory.RELATED -> "相关景点"
        TourismCatalogCategory.HUBS -> "地区旅游中心"
        TourismCatalogCategory.WALKING -> "釜山徒步旅行路线"
        TourismCatalogCategory.AUDIO -> "语音导览"
        TourismCatalogCategory.CROWDING -> "拥挤度预测"
    }
    SupportedLanguage.JA -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "釜山観光地"
        TourismCatalogCategory.ACCESSIBLE -> "バリアフリー観光"
        TourismCatalogCategory.PHOTOS -> "釜山観光写真"
        TourismCatalogCategory.PLACES_EN -> "英語版釜山観光"
        TourismCatalogCategory.PLACES_JA -> "日本語の釜山観光"
        TourismCatalogCategory.PLACES_ZH -> "中国語版釜山観光"
        TourismCatalogCategory.RELATED -> "関連観光地"
        TourismCatalogCategory.HUBS -> "地域観光ハブ"
        TourismCatalogCategory.WALKING -> "釜山ウォーキングコース"
        TourismCatalogCategory.AUDIO -> "オーディオツアー"
        TourismCatalogCategory.CROWDING -> "観光地混雑予報"
    }
}

fun TourismCatalogCategory.translatedDescription(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "관광지·음식점·숙박·쇼핑"
        TourismCatalogCategory.ACCESSIBLE -> "이동 편의 정보를 포함한 관광지"
        TourismCatalogCategory.PHOTOS -> "한국관광공사 관광사진"
        TourismCatalogCategory.PLACES_EN -> "영문 관광정보"
        TourismCatalogCategory.PLACES_JA -> "일문 관광정보"
        TourismCatalogCategory.PLACES_ZH -> "중문 관광정보"
        TourismCatalogCategory.RELATED -> "빅데이터 기반 연관 관광지"
        TourismCatalogCategory.HUBS -> "구·군별 방문 중심 관광지"
        TourismCatalogCategory.WALKING -> "두루누비 부산 걷기 여행길"
        TourismCatalogCategory.AUDIO -> "부산 중심부 오디오 콘텐츠"
        TourismCatalogCategory.CROWDING -> "관광지별 예상 혼잡 정보"
    }
    SupportedLanguage.EN -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "Attractions, restaurants, lodging, shopping"
        TourismCatalogCategory.ACCESSIBLE -> "Attractions with accessibility info"
        TourismCatalogCategory.PHOTOS -> "Photos from Korea Tourism Organization"
        TourismCatalogCategory.PLACES_EN -> "English-language tourism info"
        TourismCatalogCategory.PLACES_JA -> "Japanese-language tourism info"
        TourismCatalogCategory.PLACES_ZH -> "Chinese-language tourism info"
        TourismCatalogCategory.RELATED -> "Related attractions from tourism big data"
        TourismCatalogCategory.HUBS -> "Key attractions by district"
        TourismCatalogCategory.WALKING -> "Durunubi Busan walking trails"
        TourismCatalogCategory.AUDIO -> "Audio content around central Busan"
        TourismCatalogCategory.CROWDING -> "Expected crowding by attraction"
    }
    SupportedLanguage.ZH -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "景点·餐厅·住宿·购物"
        TourismCatalogCategory.ACCESSIBLE -> "包含无障碍信息的景点"
        TourismCatalogCategory.PHOTOS -> "韩国旅游发展局旅游照片"
        TourismCatalogCategory.PLACES_EN -> "英文旅游信息"
        TourismCatalogCategory.PLACES_JA -> "日文旅游信息"
        TourismCatalogCategory.PLACES_ZH -> "中文旅游信息"
        TourismCatalogCategory.RELATED -> "基于大数据的相关景点"
        TourismCatalogCategory.HUBS -> "各区·郡的主要景点"
        TourismCatalogCategory.WALKING -> "杜鲁努比釜山徒步旅行路线"
        TourismCatalogCategory.AUDIO -> "釜山市中心语音导览内容"
        TourismCatalogCategory.CROWDING -> "各景点预计拥挤度信息"
    }
    SupportedLanguage.JA -> when (this) {
        TourismCatalogCategory.PLACES_KO -> "観光地・飲食店・宿泊・ショッピング"
        TourismCatalogCategory.ACCESSIBLE -> "移動サポート情報を含む観光地"
        TourismCatalogCategory.PHOTOS -> "韓国観光公社の観光写真"
        TourismCatalogCategory.PLACES_EN -> "英語版観光情報"
        TourismCatalogCategory.PLACES_JA -> "日本語版観光情報"
        TourismCatalogCategory.PLACES_ZH -> "中国語版観光情報"
        TourismCatalogCategory.RELATED -> "ビッグデータに基づく関連観光地"
        TourismCatalogCategory.HUBS -> "区·郡別の中心観光地"
        TourismCatalogCategory.WALKING -> "トゥルヌビ釜山ウォーキングコース"
        TourismCatalogCategory.AUDIO -> "釜山中心部のオーディオコンテンツ"
        TourismCatalogCategory.CROWDING -> "観光地別の混雑予報情報"
    }
}

fun TourismCatalogGroup.translatedLabel(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> when (this) {
        TourismCatalogGroup.PLACES -> "관광지 탐색"
        TourismCatalogGroup.ROUTES -> "여행 동선"
        TourismCatalogGroup.INSIGHTS -> "여행 데이터"
    }
    SupportedLanguage.EN -> when (this) {
        TourismCatalogGroup.PLACES -> "Explore Places"
        TourismCatalogGroup.ROUTES -> "Travel Routes"
        TourismCatalogGroup.INSIGHTS -> "Travel Insights"
    }
    SupportedLanguage.ZH -> when (this) {
        TourismCatalogGroup.PLACES -> "探索景点"
        TourismCatalogGroup.ROUTES -> "旅行动线"
        TourismCatalogGroup.INSIGHTS -> "旅行数据"
    }
    SupportedLanguage.JA -> when (this) {
        TourismCatalogGroup.PLACES -> "観光地を探す"
        TourismCatalogGroup.ROUTES -> "旅の動線"
        TourismCatalogGroup.INSIGHTS -> "旅行データ"
    }
}

fun TourismCatalogGroup.translatedDescription(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> when (this) {
        TourismCatalogGroup.PLACES -> "부산의 장소와 사진, 이동 편의 정보를 확인해요."
        TourismCatalogGroup.ROUTES -> "함께 둘러볼 곳과 걷기·오디오 코스를 찾아요."
        TourismCatalogGroup.INSIGHTS -> "방문 흐름과 관광지 혼잡도를 참고해요."
    }
    SupportedLanguage.EN -> when (this) {
        TourismCatalogGroup.PLACES -> "See Busan places, photos, and accessibility info."
        TourismCatalogGroup.ROUTES -> "Find nearby spots plus walking and audio courses."
        TourismCatalogGroup.INSIGHTS -> "Check visitor flow and attraction crowding."
    }
    SupportedLanguage.ZH -> when (this) {
        TourismCatalogGroup.PLACES -> "查看釜山的场所、照片和无障碍信息。"
        TourismCatalogGroup.ROUTES -> "寻找周边景点及徒步·语音路线。"
        TourismCatalogGroup.INSIGHTS -> "参考访客流量与景点拥挤度。"
    }
    SupportedLanguage.JA -> when (this) {
        TourismCatalogGroup.PLACES -> "釜山の場所・写真・移動サポート情報を確認します。"
        TourismCatalogGroup.ROUTES -> "一緒に巡る場所とウォーキング·オーディオコースを探します。"
        TourismCatalogGroup.INSIGHTS -> "来訪の流れと観光地の混雑度を参考にします。"
    }
}

fun BusanDistrict.translatedLabel(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.KO -> label
    SupportedLanguage.EN -> when (this) {
        BusanDistrict.JUNG -> "Jung-gu"
        BusanDistrict.SEO -> "Seo-gu"
        BusanDistrict.DONG -> "Dong-gu"
        BusanDistrict.YEONGDO -> "Yeongdo-gu"
        BusanDistrict.BUSANJIN -> "Busanjin-gu"
        BusanDistrict.DONGNAE -> "Dongnae-gu"
        BusanDistrict.NAM -> "Nam-gu"
        BusanDistrict.BUK -> "Buk-gu"
        BusanDistrict.HAEUNDAE -> "Haeundae-gu"
        BusanDistrict.SAHA -> "Saha-gu"
        BusanDistrict.GEUMJEONG -> "Geumjeong-gu"
        BusanDistrict.GANGSEO -> "Gangseo-gu"
        BusanDistrict.YEONJE -> "Yeonje-gu"
        BusanDistrict.SUYEONG -> "Suyeong-gu"
        BusanDistrict.SASANG -> "Sasang-gu"
        BusanDistrict.GIJANG -> "Gijang-gun"
    }
    SupportedLanguage.ZH -> when (this) {
        BusanDistrict.JUNG -> "中区"
        BusanDistrict.SEO -> "西区"
        BusanDistrict.DONG -> "东区"
        BusanDistrict.YEONGDO -> "影岛区"
        BusanDistrict.BUSANJIN -> "釜山镇区"
        BusanDistrict.DONGNAE -> "东莱区"
        BusanDistrict.NAM -> "南区"
        BusanDistrict.BUK -> "北区"
        BusanDistrict.HAEUNDAE -> "海云台区"
        BusanDistrict.SAHA -> "沙下区"
        BusanDistrict.GEUMJEONG -> "金井区"
        BusanDistrict.GANGSEO -> "江西区"
        BusanDistrict.YEONJE -> "莲堤区"
        BusanDistrict.SUYEONG -> "水营区"
        BusanDistrict.SASANG -> "沙上区"
        BusanDistrict.GIJANG -> "机张郡"
    }
    SupportedLanguage.JA -> when (this) {
        BusanDistrict.JUNG -> "中区"
        BusanDistrict.SEO -> "西区"
        BusanDistrict.DONG -> "東区"
        BusanDistrict.YEONGDO -> "影島区"
        BusanDistrict.BUSANJIN -> "釜山鎮区"
        BusanDistrict.DONGNAE -> "東萊区"
        BusanDistrict.NAM -> "南区"
        BusanDistrict.BUK -> "北区"
        BusanDistrict.HAEUNDAE -> "海雲台区"
        BusanDistrict.SAHA -> "沙下区"
        BusanDistrict.GEUMJEONG -> "金井区"
        BusanDistrict.GANGSEO -> "江西区"
        BusanDistrict.YEONJE -> "蓮堤区"
        BusanDistrict.SUYEONG -> "水営区"
        BusanDistrict.SASANG -> "沙上区"
        BusanDistrict.GIJANG -> "機張郡"
    }
}

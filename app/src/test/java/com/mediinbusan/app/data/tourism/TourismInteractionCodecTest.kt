package com.mediinbusan.app.data.tourism

import com.mediinbusan.app.domain.tourism.TourismCatalogCategory
import com.mediinbusan.app.domain.tourism.TourismItemInteraction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TourismInteractionCodecTest {
    @Test
    fun `행동 시각과 키워드를 손실 없이 저장하고 복원한다`() {
        val interaction = TourismItemInteraction(
            itemId = "place|42",
            category = TourismCatalogCategory.WALKING,
            keywords = setOf("해운대", "바다,산책"),
            occurredAtEpochMillis = 2_000_000_000_000L
        )

        assertEquals(interaction, TourismInteractionCodec.decode(TourismInteractionCodec.encode(interaction)))
    }

    @Test
    fun `이전 버전이나 손상된 행동 값은 무시한다`() {
        assertNull(TourismInteractionCodec.decode("invalid"))
    }
}

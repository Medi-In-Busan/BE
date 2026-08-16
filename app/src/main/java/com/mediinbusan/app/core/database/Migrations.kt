package com.mediinbusan.app.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// v3 -> v4: favorites/recently_viewed에 subtitle/address/latitude/longitude 컬럼을 추가한다.
// 순수 컬럼 추가라 destructive fallback 없이도 기존 로컬 데이터(즐겨찾기·최근 본 항목)를
// 그대로 보존할 수 있어 실제 Migration을 작성한다.
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorites ADD COLUMN subtitle TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE favorites ADD COLUMN address TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE favorites ADD COLUMN latitude REAL")
        db.execSQL("ALTER TABLE favorites ADD COLUMN longitude REAL")
        db.execSQL("ALTER TABLE recently_viewed ADD COLUMN subtitle TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE recently_viewed ADD COLUMN address TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE recently_viewed ADD COLUMN latitude REAL")
        db.execSQL("ALTER TABLE recently_viewed ADD COLUMN longitude REAL")
    }
}

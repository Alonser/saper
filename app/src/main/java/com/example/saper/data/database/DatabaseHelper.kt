package com.example.saper.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.saper.data.models.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "saper_records.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "records"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_DIFFICULTY = "difficulty"
        private const val COLUMN_TIME = "time_seconds"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_BOARD_SIZE = "board_size"
        private const val COLUMN_MINES_COUNT = "mines_count"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DIFFICULTY TEXT NOT NULL,
                $COLUMN_TIME INTEGER NOT NULL,
                $COLUMN_DATE INTEGER NOT NULL,
                $COLUMN_BOARD_SIZE TEXT NOT NULL,
                $COLUMN_MINES_COUNT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)

        // Создаём индекс для быстрого поиска по сложности
        db.execSQL("CREATE INDEX idx_difficulty ON $TABLE_NAME ($COLUMN_DIFFICULTY)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    suspend fun insertRecord(record: Record) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DIFFICULTY, record.difficulty)
            put(COLUMN_TIME, record.timeSeconds)
            put(COLUMN_DATE, record.date)
            put(COLUMN_BOARD_SIZE, record.boardSize)
            put(COLUMN_MINES_COUNT, record.minesCount)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    suspend fun getTopRecords(difficulty: String): List<Record> = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_DIFFICULTY = ?",
            arrayOf(difficulty),
            null,
            null,
            "$COLUMN_TIME ASC",
            "10"
        )

        val records = mutableListOf<Record>()
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor))
        }
        cursor.close()
        db.close()
        records
    }

    suspend fun getBestTime(difficulty: String): Int? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TIME),
            "$COLUMN_DIFFICULTY = ?",
            arrayOf(difficulty),
            null,
            null,
            "$COLUMN_TIME ASC",
            "1"
        )

        val bestTime = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME))
        } else {
            null
        }
        cursor.close()
        db.close()
        bestTime
    }

    suspend fun clearRecords(difficulty: String) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_DIFFICULTY = ?", arrayOf(difficulty))
        db.close()
    }

    suspend fun getAllRecords(): List<Record> = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_TIME ASC"
        )

        val records = mutableListOf<Record>()
        while (cursor.moveToNext()) {
            records.add(cursorToRecord(cursor))
        }
        cursor.close()
        db.close()
        records
    }

    private fun cursorToRecord(cursor: Cursor): Record {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY))
        val time = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME))
        val date = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))
        val boardSize = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOARD_SIZE))
        val minesCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINES_COUNT))

        return Record(id, difficulty, time, date, boardSize, minesCount)
    }
}
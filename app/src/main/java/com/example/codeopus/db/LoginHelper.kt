package com.example.codeopus

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LoginHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_database"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USERNAME TEXT PRIMARY KEY," +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val insert = "INSERT INTO $TABLE_USERS ($COLUMN_USERNAME, $COLUMN_PASSWORD) VALUES (?, ?)"
        val stmt = db.compileStatement(insert)
        stmt.bindString(1, username)
        stmt.bindString(2, password)
        val result = stmt.executeInsert()
        db.close()
        return result
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun checkUsername(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}

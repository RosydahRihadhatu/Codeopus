import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE quiz_history ADD COLUMN id INTEGER PRIMARY KEY AUTOINCREMENT")
        database.execSQL("UPDATE quiz_history SET id = rowid")
    }
}

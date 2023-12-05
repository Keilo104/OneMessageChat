package br.edu.scl.ifsp.ads.onemessagechat.dao

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.scl.ifsp.ads.onemessagechat.R
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage
import java.sql.SQLException

class OneMessageDaoSqlite(context: Context) : OneMessageLocalDao {

    companion object Constant {
        private const val ONEMESSAGE_DATABASE_FILE = "onemessage_localdb"
        private const val ONEMESSAGE_TABLE = "onemessage"
        private const val IDENTIFIER_COLUMN = "identifier"
        private const val CONTENT_COLUMN = "content"
        private const val CREATE_ONEMESSAGE_TABLE_STATEMENT =
            "CREATE TABLE IF NOT EXISTS $ONEMESSAGE_TABLE (" +
                    "$IDENTIFIER_COLUMN TEXT PRIMARY KEY," +
                    "$CONTENT_COLUMN TEXT NOT NULL" +
                    ");"
    }

    private val oneMessageSqliteDatabase: SQLiteDatabase

    init {
        oneMessageSqliteDatabase =
            context.openOrCreateDatabase(ONEMESSAGE_DATABASE_FILE, MODE_PRIVATE, null)

        try {
            oneMessageSqliteDatabase.execSQL(CREATE_ONEMESSAGE_TABLE_STATEMENT)

        } catch (se: SQLException) {
            Log.e(context.getString(R.string.app_name), se.message.toString())
        }
    }

    override fun createOneMessage(oneMessage: OneMessage) {
        oneMessageSqliteDatabase.insert(
            ONEMESSAGE_TABLE,
            null,
            oneMessage.toContentValues()
        )
    }

    override fun retrieveOneMessage(identifier: String): OneMessage? {
        val cursor = oneMessageSqliteDatabase.rawQuery(
            "SELECT * FROM $ONEMESSAGE_TABLE WHERE $IDENTIFIER_COLUMN = ?",
            arrayOf(identifier)
        )

        val oneMessage = if(cursor.moveToFirst()) cursor.rowToOneMessage() else null
        cursor.close()
        return oneMessage
    }

    override fun retrieveOneMessages(): MutableList<OneMessage> {
        val oneMessageList = mutableListOf<OneMessage>()

        val cursor = oneMessageSqliteDatabase.rawQuery(
            "SELECT * FROM $ONEMESSAGE_TABLE ORDER BY $IDENTIFIER_COLUMN",
            null
        )

        while (cursor.moveToNext()) {
            oneMessageList.add(cursor.rowToOneMessage())
        }
        cursor.close()

        return oneMessageList
    }

    override fun updateOneMessage(oneMessage: OneMessage): Int = oneMessageSqliteDatabase.update(
        ONEMESSAGE_TABLE,
        oneMessage.toContentValues(),
        "$IDENTIFIER_COLUMN = ?",
        arrayOf(oneMessage.identifier)
    )

    override fun deleteOneMessage(oneMessage: OneMessage): Int = oneMessageSqliteDatabase.delete(
        ONEMESSAGE_TABLE,
        "$IDENTIFIER_COLUMN = ?",
        arrayOf(oneMessage.identifier)
    )

    private fun Cursor.rowToOneMessage(): OneMessage = OneMessage(
        getString(getColumnIndexOrThrow(IDENTIFIER_COLUMN)),
        getString(getColumnIndexOrThrow(CONTENT_COLUMN)),
    )

    private fun OneMessage.toContentValues(): ContentValues = with(ContentValues()) {
        put(IDENTIFIER_COLUMN, identifier)
        put(CONTENT_COLUMN, content)
        this
    }

}
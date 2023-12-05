package br.edu.scl.ifsp.ads.onemessagechat.dao

import android.content.Context
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage

class OneMessageDaoSqlite(context: Context) : OneMessageLocalDao {
    override fun createOneMessage(oneMessage: OneMessage) {
        TODO("Not yet implemented")
    }

    override fun retrieveOneMessage(identifier: String): OneMessage? {
        TODO("Not yet implemented")
    }

    override fun retrieveOneMessages(): MutableList<OneMessage> {
        TODO("Not yet implemented")
    }

    override fun updateOneMessage(oneMessage: OneMessage): Int {
        TODO("Not yet implemented")
    }

    override fun deleteOneMessage(oneMessage: OneMessage): Int {
        TODO("Not yet implemented")
    }
}
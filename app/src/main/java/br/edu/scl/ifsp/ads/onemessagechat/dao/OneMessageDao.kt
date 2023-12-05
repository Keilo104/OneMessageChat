package br.edu.scl.ifsp.ads.onemessagechat.dao

import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage

interface OneMessageDao {
    fun createOneMessage(oneMessage: OneMessage)

    fun retrieveOneMessage(identifier: String): OneMessage?

    fun retrieveOneMessages(): MutableList<OneMessage>

    fun updateOneMessage(oneMessage: OneMessage): Int

    fun deleteOneMessage(oneMessage: OneMessage): Int

    fun softSubscribeToMessage(identifier: String): Int

    fun hardSubscribeToMessage(identifier: String): Int

    fun unsubscribeFromMessage(identifier: String): Int
}
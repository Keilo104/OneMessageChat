package br.edu.scl.ifsp.ads.onemessagechat.controller

import android.os.Message
import android.provider.Settings.Secure
import android.provider.Settings.Secure.ANDROID_ID
import br.edu.scl.ifsp.ads.onemessagechat.dao.OneMessageDao
import br.edu.scl.ifsp.ads.onemessagechat.dao.OneMessageDaoFirebase
import br.edu.scl.ifsp.ads.onemessagechat.model.Constant.ONEMESSAGE_ARRAY
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage
import br.edu.scl.ifsp.ads.onemessagechat.view.MainActivity

class OneMessageController(private val mainActivity: MainActivity) {
    private val userUid: String by lazy {
        Secure.getString(mainActivity.contentResolver, ANDROID_ID)
    }

    private val oneMessageDaoImpl: OneMessageDao by lazy {
        OneMessageDaoFirebase(userUid)
    }

    fun insertOneMessage(oneMessage: OneMessage) {
        Thread {
            oneMessageDaoImpl.subscribeToMessage(oneMessage.identifier)
            oneMessageDaoImpl.createOneMessage(oneMessage)
        }.start()
    }

    fun getOneMessage(identifier: String): OneMessage? {
        return oneMessageDaoImpl.retrieveOneMessage(identifier)
    }

    fun getOneMessages() {
        Thread {
            val returnList = oneMessageDaoImpl.retrieveOneMessages()

            val message = Message()
            message.data.putParcelableArray(
                ONEMESSAGE_ARRAY,
                returnList.toTypedArray()
            )

            mainActivity.updateOneMessageListHandler.sendMessage(message)

        }.start()
    }

    fun editOneMessage(oneMessage: OneMessage) {
        Thread {
            oneMessageDaoImpl.updateOneMessage(oneMessage)
        }.start()
    }

    fun subscribeToMessage(identifier: String) {
        Thread {
            oneMessageDaoImpl.subscribeToMessage(identifier)
        }.start()
    }


    fun unsubscribeToMessage(identifier: String) {
        Thread {
            oneMessageDaoImpl.unsubscribeFromMessage(identifier)
        }.start()
    }

    fun removeOneMessage(oneMessage: OneMessage) {
        Thread {
            oneMessageDaoImpl.deleteOneMessage(oneMessage)
        }.start()
    }
}
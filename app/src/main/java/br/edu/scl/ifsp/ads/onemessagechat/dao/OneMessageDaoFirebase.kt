package br.edu.scl.ifsp.ads.onemessagechat.dao

import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class OneMessageDaoFirebase : OneMessageDao {

    companion object {
        private const val ONEMESSAGE_LIST_ROOT_NODE = "oneMessageList"
    }

    private val oneMessageFirebaseReference = Firebase.database.getReference(ONEMESSAGE_LIST_ROOT_NODE)

    private val oneMessageList: MutableList<OneMessage> = mutableListOf()

    init {
        oneMessageFirebaseReference.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val oneMessage: OneMessage? = snapshot.getValue<OneMessage>()

                oneMessage?.also { _oneMessage ->
                    if (!oneMessageList.any { it.identifier.equals(_oneMessage.identifier) }) {
                        oneMessageList.add(_oneMessage)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val oneMessage: OneMessage? = snapshot.getValue<OneMessage>()

                oneMessage?.also { _oneMessage ->
                    oneMessageList.apply {
                        this[indexOfFirst { it.identifier.equals(_oneMessage.identifier) }] = _oneMessage
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val oneMessage: OneMessage? = snapshot.getValue<OneMessage>()

                oneMessage?.also { _oneMessage ->
                    oneMessageList.remove(_oneMessage)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // NSA
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })

        oneMessageFirebaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val oneMessageMap = snapshot.getValue<Map<String, OneMessage>>()

                oneMessageList.clear()

                oneMessageMap?.values?.also {
                    oneMessageList.addAll(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })
    }

    private fun createOrUpdateOneMessage(oneMessage: OneMessage) {
        oneMessageFirebaseReference.child(oneMessage.identifier).setValue(oneMessage).toString()
    }

    override fun createOneMessage(oneMessage: OneMessage) {
        createOrUpdateOneMessage(oneMessage)
    }

    override fun updateOneMessage(oneMessage: OneMessage): Int {
        createOrUpdateOneMessage(oneMessage)
        return 1
    }

    override fun retrieveOneMessage(identifier: String): OneMessage? {
        return oneMessageList[oneMessageList.indexOfFirst { it.identifier.equals(identifier) }]
    }

    override fun retrieveOneMessages(): MutableList<OneMessage> {
        return oneMessageList
    }

    override fun deleteOneMessage(oneMessage: OneMessage): Int {
        oneMessageFirebaseReference.child(oneMessage.identifier).removeValue()
        return 1
    }
}
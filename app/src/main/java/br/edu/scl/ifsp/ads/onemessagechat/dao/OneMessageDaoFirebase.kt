package br.edu.scl.ifsp.ads.onemessagechat.dao

import android.util.Log
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class OneMessageDaoFirebase(val userUid: String) : OneMessageDao {

    companion object {
        private const val ONEMESSAGE_LIST_ROOT_NODE = "oneMessageList"
        private const val SUBSCRIPTION_LIST_ROOT_NODE = "subscriptionList"
    }

    private val oneMessageFirebaseReference = Firebase.database.getReference(ONEMESSAGE_LIST_ROOT_NODE)
    private val subscriptionFirebaseReference = Firebase.database.getReference(SUBSCRIPTION_LIST_ROOT_NODE)
    private val subscribedList: MutableList<String> = mutableListOf()
    private val oneMessageList: MutableList<OneMessage> = mutableListOf()

    init {
        oneMessageFirebaseReference.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val oneMessage: OneMessage? = snapshot.getValue<OneMessage>()

                oneMessage?.also { _oneMessage ->
                    if (_oneMessage.identifier in subscribedList &&
                        !oneMessageList.any { it.identifier.equals(_oneMessage.identifier) }) {
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
                    unsubscribeFromMessage(_oneMessage.identifier)
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

                oneMessageMap?.values?.onEach {
                    if (it.identifier in subscribedList) {
                        oneMessageList.add(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })

        subscriptionFirebaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subscriptionMap = snapshot.getValue<Map<String, Map<String, Boolean>>>()

                subscribedList.clear()

                subscriptionMap?.get(userUid)?.let {
                    subscribedList.addAll(it.keys)
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

    override fun softSubscribeToMessage(identifier: String): Int {
        subscribedList.add(identifier)
        subscriptionFirebaseReference.child(userUid).child(identifier).setValue(true)

        return 1
    }

    override fun hardSubscribeToMessage(identifier: String): Int {
        oneMessageFirebaseReference.child(identifier).get().addOnSuccessListener {
            val oneMessage = it.getValue<OneMessage>()
            if (oneMessage !== null &&
                !oneMessageList.any { it.identifier.equals(oneMessage.identifier) }) {
                oneMessageList.add(oneMessage)

                softSubscribeToMessage(identifier)
            }
        }

        return 1
    }

    override fun unsubscribeFromMessage(identifier: String): Int {
        subscribedList.remove(identifier)
        oneMessageList.removeIf { it.identifier.equals(identifier) }
        subscriptionFirebaseReference.child(userUid).child(identifier).removeValue()
        return 1
    }
}
package com.google.firebase.codelab.friendlychat

import android.util.Log
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class FirebaseRepository {

    @ExperimentalCoroutinesApi
    fun getMessages() = callbackFlow {

        var messagesRef: DatabaseReference? = null
        try {
            val db = Firebase.database
            messagesRef = db.reference.child(MESSAGES_CHILD)

        } catch(e: Throwable) {
            // If Firebase cannot be initialized, close the stream of data
            // flow consumers will stop collecting and the coroutine will resume
            close(e)
        }
        val subscription = messagesRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val messages: List<FriendlyMessage> = snapshot.children.map { child ->
                        child.getValue(FriendlyMessage::class.java)!!
                    }
                    offer(messages)
                } catch(e: Throwable) {
                    Log.e(TAG, "Error getting messages")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Listener cancelled")
            }
        })
        awaitClose { subscription?.let{ messagesRef?.removeEventListener(subscription) } }
    }

    companion object {
        const val TAG = "FirebaseRepository"
        const val MESSAGES_CHILD = "messages"
    }
}
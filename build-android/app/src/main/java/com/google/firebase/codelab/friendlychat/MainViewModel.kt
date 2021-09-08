package com.google.firebase.codelab.friendlychat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.codelab.friendlychat.model.FriendlyMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val _messages = MutableStateFlow<List<FriendlyMessage>>(emptyList())
    val messages: StateFlow<List<FriendlyMessage>>
        get() = _messages

    fun onGetMessages() {
        viewModelScope.launch {
            launch {
                repository.getMessages().collect {
                    _messages.value = it
                }
            }
        }
    }

    fun onNewMessage(message: FriendlyMessage) {
        Log.d("VM", message.toString())
    }
}
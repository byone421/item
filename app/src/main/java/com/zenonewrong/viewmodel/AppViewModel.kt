package com.zenonewrong.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zenonewrong.Screen
import com.zenonewrong.entity.Classify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _backEvent = MutableSharedFlow<Unit>()
    val backEvent = _backEvent.asSharedFlow()

    var currentTab by mutableStateOf(Screen.Home.route)

//    private val _selectedClassify = MutableStateFlow<Classify?>(null)
//    val selectedClassify: StateFlow<Classify?> = _selectedClassify


    // 配置更新事件通知
//    private val _expireConfigUpdateEvent = MutableSharedFlow<String>()
//    val expireConfigUpdateEvent = _expireConfigUpdateEvent.asSharedFlow()

    private val _sourceScreen = MutableStateFlow<String?>(null)
    val sourceScreen: StateFlow<String?> = _sourceScreen

//    fun selectClassify(classify: Classify) {
//        _selectedClassify.value = classify
//    }
//
//
//    fun clearClassify() {
//        _selectedClassify.value = null
//    }


//    fun expireDaysUpdateEvent(){
//        viewModelScope.launch {
//            _expireConfigUpdateEvent.emit("update")
//        }
//    }
//


    fun navigateTo(source: String,route: String) {
        _sourceScreen.value = source
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _backEvent.emit(Unit)
        }
    }
}
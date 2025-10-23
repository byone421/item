package com.zenonewrong.bean


data class ClassifyState(
    val id: Long? =  null,
    val name: String = "",
    val sortOrder: String = "",
    val showOnHome: Boolean = true,
    val isDialogVisible: Boolean = false,
    val isDelDialogVisible: Boolean = false,
    val editingClassify: Boolean = false
)


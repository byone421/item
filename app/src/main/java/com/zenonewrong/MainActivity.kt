package com.zenonewrong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zenonewrong.ui.screen.MainScreen
import com.zenonewrong.ui.theme.ItemTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ItemTheme {
                AppEntryPoint()
            }
        }
    }

}



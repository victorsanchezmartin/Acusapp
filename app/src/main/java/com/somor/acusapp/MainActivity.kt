package com.somor.acusapp

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.somor.acusapp.ui.core.ContentWrapper
import com.somor.acusapp.ui.theme.AcusAppTheme
import com.somor.acusapp.ui.theme.MyMaroon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navigationController : NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
        setContent {
            AcusAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MyMaroon //Este color granate es personal
                ) {
                    val context = LocalContext.current
                    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                    navigationController = rememberNavController()
                    ContentWrapper(navigationController)
                }
            }
        }
    }

}


package com.chesspro.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.chesspro.app.ui.screens.ChessMainScreen
import com.chesspro.app.ui.theme.ChineseChessProTheme

/**
 * 象棋Pro 主Activity
 */
class MainActivity : ComponentActivity() {
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 相机权限结果
        if (!isGranted) {
            // 权限被拒绝，可以显示提示
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 检查相机权限
        checkCameraPermission()
        
        setContent {
            ChineseChessProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChessMainScreen()
                }
            }
        }
    }
    
    /**
     * 检查并请求相机权限
     */
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已有权限
            }
            else -> {
                // 请求权限
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

package com.chesspro.app

import android.app.Application
import org.opencv.android.OpenCVLoader

/**
 * 象棋Pro Application类
 * 初始化全局资源
 */
class ChineseChessProApp : Application() {
    
    companion object {
        lateinit var instance: ChineseChessProApp
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化OpenCV
        initOpenCV()
    }
    
    /**
     * 初始化OpenCV库
     */
    private fun initOpenCV(): Boolean {
        return try {
            val success = OpenCVLoader.initLocal()
            if (success) {
                android.util.Log.i("ChineseChessProApp", "OpenCV初始化成功")
            } else {
                android.util.Log.e("ChineseChessProApp", "OpenCV初始化失败")
            }
            success
        } catch (e: Exception) {
            android.util.Log.e("ChineseChessProApp", "OpenCV初始化异常", e)
            false
        }
    }
}

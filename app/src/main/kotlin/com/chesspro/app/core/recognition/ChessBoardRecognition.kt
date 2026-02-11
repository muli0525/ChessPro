package com.chesspro.app.core.recognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.chesspro.app.core.chess.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

/**
 * 棋盘识别器
 * 使用OpenCV进行图像处理和棋子识别
 */
class ChessBoardRecognition(private val context: Context) {
    
    companion object {
        private const val TAG = "ChessBoardRecognition"
        private const val BOARD_SIZE = 9 // 棋盘9列
        private const val BOARD_ROWS = 10 // 棋盘10行
        
        // 棋子颜色阈值
        private const val RED_THRESHOLD = 100
        private const val BLACK_THRESHOLD = 80
    }
    
    private var isInitialized = false
    
    /**
     * 初始化OpenCV
     */
    init {
        initializeOpenCV()
    }
    
    private fun initializeOpenCV(): Boolean {
        if (OpenCVLoader.initLocal()) {
            isInitialized = true
            Log.d(TAG, "OpenCV初始化成功")
            return true
        } else {
            Log.e(TAG, "OpenCV初始化失败")
            return false
        }
    }
    
    /**
     * 分析棋盘图像
     */
    fun analyzeBoard(
        bitmap: Bitmap,
        onResult: (RecognitionResult?) -> Unit
    ) {
        if (!isInitialized) {
            onResult(null)
            return
        }
        
        try {
            // 转换为OpenCV图像
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            
            // 预处理图像
            val processedMat = preprocessImage(mat)
            
            // 检测棋盘
            val boardCorners = detectBoard(processedMat)
            
            if (boardCorners == null) {
                onResult(null)
                return
            }
            
            // 校正棋盘
            val correctedMat = perspectiveTransform(processedMat, boardCorners)
            
            // 识别棋子
            val pieces = recognizePieces(correctedMat)
            
            // 创建识别结果
            val result = RecognitionResult(
                bitmap = bitmap,
                pieces = pieces,
                boardCorners = boardCorners,
                confidence = calculateConfidence(pieces)
            )
            
            onResult(result)
            
        } catch (e: Exception) {
            Log.e(TAG, "棋盘分析失败", e)
            onResult(null)
        }
    }
    
    /**
     * 预处理图像
     */
    private fun preprocessImage(mat: Mat): Mat {
        val result = Mat()
        
        // 转换为灰度图
        val gray = Mat()
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        
        // 高斯模糊去噪
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)
        
        // 自适应阈值
        Imgproc.adaptiveThreshold(
            blurred,
            result,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            11,
            2.0
        )
        
        gray.release()
        blurred.release()
        
        return result
    }
    
    /**
     * 检测棋盘边界
     */
    private fun detectBoard(mat: Mat): MatOfPoint2f? {
        // 查找轮廓
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        
        if (contours.isEmpty()) {
            return null
        }
        
        // 找到最大的四边形轮廓
        var maxArea = 0.0
        var maxContour: MatOfPoint? = null
        
        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            if (area > maxArea) {
                maxArea = area
                maxContour = contour
            }
        }
        
        if (maxContour == null || maxArea < 1000) {
            return null
        }
        
        // 近似多边形
        val approx = MatOfPoint2f()
        val peri = Imgproc.arcLength(MatOfPoint2f(*maxContour.toArray()), true)
        Imgproc.approxPolyDP(
            MatOfPoint2f(*maxContour.toArray()),
            approx,
            0.02 * peri,
            true
        )
        
        // 检查是否为四边形
        if (approx.rows() == 4) {
            return MatOfPoint2f(*approx.toArray())
        }
        
        return null
    }
    
    /**
     * 透视变换校正棋盘
     */
    private fun perspectiveTransform(mat: Mat, corners: MatOfPoint2f): Mat {
        val width = 900 // 棋盘宽度（9格 * 100像素）
        val height = 1000 // 棋盘高度（10格 * 100像素）
        
        val dstPoints = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(width.toDouble(), 0.0),
            Point(width.toDouble(), height.toDouble()),
            Point(0.0, height.toDouble())
        )
        
        val transform = Imgproc.getPerspectiveTransform(corners, dstPoints)
        val result = Mat()
        Imgproc.warpPerspective(mat, result, transform, Size(width.toDouble(), height.toDouble()))
        
        transform.release()
        dstPoints.release()
        
        return result
    }
    
    /**
     * 识别棋子
     */
    private fun recognizePieces(boardMat: Mat): List<ChessPiece> {
        val pieces = mutableListOf<ChessPiece>()
        
        // 单元格大小
        val cellWidth = boardMat.width() / 9
        val cellHeight = boardMat.height() / 10
        
        // 遍历每个格子
        for (row in 0 until 10) {
            for (col in 0 until 9) {
                val x = col * cellWidth
                val y = row * cellHeight
                
                // 提取单元格
                val cellRect = Rect(x.toInt(), y.toInt(), cellWidth.toInt(), cellHeight.toInt())
                val cellMat = Mat(boardMat, cellRect)
                
                // 识别棋子
                val piece = recognizePiece(cellMat, row, col)
                piece?.let { pieces.add(it) }
                
                cellMat.release()
            }
        }
        
        return pieces
    }
    
    /**
     * 识别单个棋子
     */
    private fun recognizePiece(cellMat: Mat, row: Int, col: Int): ChessPiece? {
        // 计算单元格内棋子像素的比例
        val totalPixels = cellMat.rows() * cellMat.cols()
        val whitePixels = Core.countNonZero(cellMat)
        val ratio = whitePixels.toDouble() / totalPixels
        
        // 如果没有棋子
        if (ratio < 0.15) {
            return null
        }
        
        // 尝试识别棋子类型（简化版）
        val position = Position(col, row)
        
        // 默认返回兵/卒作为占位符
        // 实际应用中需要更复杂的分类器
        val color = if (row > 4) PieceColor.RED else PieceColor.BLACK
        
        return when {
            row == 0 || row == 9 -> {
                when (col) {
                    0, 8 -> ChessPiece(PieceType.JU, color, position)
                    1, 7 -> ChessPiece(PieceType.MA, color, position)
                    2, 6 -> ChessPiece(PieceType.XIANG, color, position)
                    3, 5 -> ChessPiece(PieceType.SHI, color, position)
                    4 -> ChessPiece(PieceType.JIANG, color, position)
                    else -> null
                }
            }
            row == 2 || row == 7 -> {
                when (col) {
                    1, 7 -> ChessPiece(PieceType.PAO, color, position)
                    else -> null
                }
            }
            row == 3 || row == 6 -> {
                ChessPiece(PieceType.BING, color, position)
            }
            else -> null
        }
    }
    
    /**
     * 计算识别置信度
     */
    private fun calculateConfidence(pieces: List<ChessPiece>): Float {
        // 检查棋子数量
        val expectedRed = 16
        val expectedBlack = 16
        val actualRed = pieces.count { it.color == PieceColor.RED }
        val actualBlack = pieces.count { it.color == PieceColor.BLACK }
        
        if (actualRed == expectedRed && actualBlack == expectedBlack) {
            return 1.0f
        }
        
        val total = expectedRed + expectedBlack
        val actual = actualRed + actualBlack
        
        return (actual.toFloat() / total).coerceIn(0f, 1f)
    }
    
    /**
     * 加载机器学习模型（预留）
     */
    private fun loadModel(): Boolean {
        try {
            // 这里可以加载训练好的TensorFlow Lite模型
            // 用于更精确的棋子分类
            return true
        } catch (e: Exception) {
            Log.e(TAG, "模型加载失败", e)
            return false
        }
    }
}

/**
 * 棋盘识别结果
 */
data class RecognitionResult(
    val bitmap: Bitmap,
    val pieces: List<ChessPiece>,
    val boardCorners: MatOfPoint2f,
    val confidence: Float
)

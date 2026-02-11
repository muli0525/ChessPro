package com.chesspro.app.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chesspro.app.core.chess.*
import com.chesspro.app.ui.ChessViewModel
import com.chesspro.app.ui.GameMode
import com.chesspro.app.ui.components.*

/**
 * 象棋APP主屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessMainScreen(
    viewModel: ChessViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPosition by viewModel.selectedPosition.collectAsState()
    val suggestedMove by viewModel.suggestedMove.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    
    var showSettings by remember { mutableStateOf(false) }
    var currentTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("中国象棋 Pro") },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    currentTab = tab
                    when (tab) {
                        0 -> viewModel.setMode(GameMode.AI_VS_PLAYER)
                        1 -> viewModel.setMode(GameMode.PLAYER_VS_PLAYER)
                        2 -> viewModel.setMode(GameMode.BOARD_EDIT)
                        3 -> viewModel.setMode(GameMode.CAMERA_RECOGNITION)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                0, 1, 2 -> {
                    // 棋盘对战/摆棋模式
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 状态栏
                        GameStatusBar(
                            currentPlayer = uiState.currentPlayer,
                            moveCount = uiState.moveCount,
                            gameState = uiState.gameState
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 棋盘
                        ChessBoardView(
                            board = viewModel.chessBoard,
                            selectedPosition = selectedPosition,
                            suggestedMove = suggestedMove,
                            onPositionClick = { viewModel.onPositionClick(it) },
                            onPieceDrag = { from, to -> viewModel.onPieceDrag(from, to) },
                            boardSize = 350.dp,
                            modifier = Modifier.padding(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // 建议走法提示
                        suggestedMove?.let { move ->
                            MoveSuggestionView(
                                move = move,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        // 操作按钮
                        ActionButtons(
                            onUndo = { viewModel.undoMove() },
                            onRestart = { viewModel.restart() },
                            isThinking = uiState.isThinking
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                3 -> {
                    // 相机识别模式
                    CameraRecognitionScreen(
                        onImageCaptured = { bitmap ->
                            viewModel.recognizeImage(bitmap)
                        },
                        suggestedMove = suggestedMove,
                        isRecognizing = uiState.isRecognizing,
                        recognitionConfidence = uiState.recognitionConfidence,
                        onCalculateMove = { viewModel.calculateSuggestedMove() }
                    )
                }
            }
            
            // 游戏结束对话框
            if (uiState.gameState != GameState.PLAYING) {
                GameOverDialog(
                    gameState = uiState.gameState,
                    onRestart = { viewModel.restart() }
                )
            }
        }
    }
    
    // 设置对话框
    if (showSettings) {
        SettingsDialog(onDismiss = { showSettings = false })
    }
}

/**
 * 底部导航栏
 */
@Composable
fun BottomNavigationBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Computer, contentDescription = "人机对战") },
            label = { Text("人机") }
        )
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Default.People, contentDescription = "双人对战") },
            label = { Text("对战") }
        )
        NavigationBarItem(
            selected = currentTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Default.Edit, contentDescription = "摆棋模式") },
            label = { Text("摆棋") }
        )
        NavigationBarItem(
            selected = currentTab == 3,
            onClick = { onTabSelected(3) },
            icon = { Icon(Icons.Default.CameraAlt, contentDescription = "相机识别") },
            label = { Text("相机") }
        )
    }
}

/**
 * 游戏状态栏
 */
@Composable
fun GameStatusBar(
    currentPlayer: PieceColor,
    moveCount: Int,
    gameState: GameState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 当前执子方
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (currentPlayer == PieceColor.RED) Icons.Default.Flag else Icons.Default.Flag,
                contentDescription = null,
                tint = if (currentPlayer == PieceColor.RED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentPlayer == PieceColor.RED) "红方走棋" else "黑方走棋",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        // 步数
        Text(
            text = "第 ${moveCount / 2 + 1} 步",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 操作按钮
 */
@Composable
fun ActionButtons(
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    isThinking: Boolean
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = onUndo,
            enabled = !isThinking
        ) {
            Icon(Icons.Default.Undo, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("撤销")
        }
        
        Button(onClick = onRestart) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("重新开始")
        }
    }
}

/**
 * 相机识别屏幕
 */
@Composable
fun CameraRecognitionScreen(
    onImageCaptured: (Bitmap) -> Unit,
    suggestedMove: Move?,
    isRecognizing: Boolean,
    recognitionConfidence: Float,
    onCalculateMove: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 相机预览
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp)
        ) {
            CameraPreview(
                onImageCaptured = onImageCaptured,
                isEnabled = !isRecognizing
            )
        }
        
        // 识别状态
        if (isRecognizing) {
            CircularProgressIndicator()
            Text(
                text = "正在识别棋盘...",
                modifier = Modifier.padding(16.dp)
            )
        } else if (recognitionConfidence > 0) {
            Text(
                text = "识别置信度: ${(recognitionConfidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = if (recognitionConfidence > 0.7) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 建议走法
        suggestedMove?.let { move ->
            MoveSuggestionView(
                move = move,
                modifier = Modifier.padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onCalculateMove) {
                Text("重新计算走法")
            }
        } ?: run {
            Text(
                text = "拍摄棋盘照片以获取走法建议",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

/**
 * 游戏结束对话框
 */
@Composable
fun GameOverDialog(
    gameState: GameState,
    onRestart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = when (gameState) {
                    GameState.RED_WINS -> "红方胜！"
                    GameState.BLACK_WINS -> "黑方胜！"
                    GameState.DRAW -> "和棋"
                    else -> ""
                }
            )
        },
        confirmButton = {
            Button(onClick = onRestart) {
                Text("再来一局")
            }
        }
    )
}

/**
 * 设置对话框
 */
@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    var aiDifficulty by remember { mutableIntStateOf(3) }
    var showCoordinates by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置") },
        text = {
            Column {
                // AI难度
                Text("AI难度", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = aiDifficulty.toFloat(),
                    onValueChange = { aiDifficulty = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3
                )
                Text(
                    text = when (aiDifficulty) {
                        1 -> "简单"
                        2 -> "入门"
                        3 -> "中等"
                        4 -> "困难"
                        5 -> "大师"
                        else -> ""
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 显示坐标
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("显示坐标")
                    Switch(
                        checked = showCoordinates,
                        onCheckedChange = { showCoordinates = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

package com.chesspro.app.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chesspro.app.core.chess.*
import com.chesspro.app.core.recognition.ChessBoardRecognition
import com.chesspro.app.core.recognition.RecognitionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 象棋APP主ViewModel
 * 管理棋盘状态、AI计算、相机识别等
 */
class ChessViewModel(application: Application) : AndroidViewModel(application) {
    
    // 棋盘实例
    private val _chessBoard = ChessBoard()
    val chessBoard: ChessBoard get() = _chessBoard
    
    // 象棋AI
    private val chessAI = ChessAI(maxDepth = 3)
    
    // 棋盘识别器
    private val recognition = ChessBoardRecognition(application.applicationContext)
    
    // UI状态
    private val _uiState = MutableStateFlow(ChessUiState())
    val uiState: StateFlow<ChessUiState> = _uiState.asStateFlow()
    
    // 当前选中的位置
    private val _selectedPosition = MutableStateFlow<Position?>(null)
    val selectedPosition: StateFlow<Position?> = _selectedPosition.asStateFlow()
    
    // 建议走法
    private val _suggestedMove = MutableStateFlow<Move?>(null)
    val suggestedMove: StateFlow<Move?> = _suggestedMove.asStateFlow()
    
    // 当前模式
    private val _currentMode = MutableStateFlow(GameMode.AI_VS_PLAYER)
    val currentMode: StateFlow<GameMode> = _currentMode.asStateFlow()
    
    /**
     * 点击棋盘位置
     */
    fun onPositionClick(position: Position) {
        when (_currentMode.value) {
            GameMode.AI_VS_PLAYER, GameMode.PLAYER_VS_PLAYER -> {
                handleBoardClick(position)
            }
            GameMode.BOARD_EDIT -> {
                handleEditClick(position)
            }
            GameMode.CAMERA_RECOGNITION -> {
                // 相机模式下不处理点击
            }
        }
    }
    
    /**
     * 处理棋盘点击（走棋模式）
     */
    private fun handleBoardClick(position: Position) {
        val piece = _chessBoard.getPieceAt(position)
        
        // 如果有选中的棋子，尝试移动
        _selectedPosition.value?.let { selected ->
            if (selected == position) {
                // 点击同一位置，取消选中
                _selectedPosition.value = null
                return
            }
            
            // 尝试移动
            val move = Move(
                from = selected,
                to = position,
                piece = _chessBoard.getPieceAt(selected)!!
            )
            
            if (_chessBoard.makeMove(move)) {
                // 移动成功
                _selectedPosition.value = null
                
                // 如果是AI对战模式，触发AI思考
                if (_currentMode.value == GameMode.AI_VS_PLAYER && 
                    _chessBoard.currentPlayer != getPlayerColor()) {
                    makeAiMove()
                }
                
                // 更新UI状态
                updateUiState()
            } else {
                // 移动失败，检查是否选中了己方棋子
                if (piece != null && piece.color == _chessBoard.currentPlayer) {
                    _selectedPosition.value = position
                }
            }
        } ?: run {
            // 没有选中棋子，选中当前点击的己方棋子
            if (piece != null && piece.color == _chessBoard.currentPlayer) {
                _selectedPosition.value = position
            }
        }
    }
    
    /**
     * 处理摆棋模式点击
     */
    private fun handleEditClick(position: Position) {
        // 在摆棋模式下，可以添加或移除棋子
        val existingPiece = _chessBoard.getPieceAt(position)
        
        if (existingPiece != null) {
            // 移除棋子
            // 注意：这里需要修改ChessBoard的访问级别
        } else {
            // 添加棋子（显示选择对话框）
            _uiState.value = _uiState.value.copy(
                showPiecePicker = true,
                pickedPosition = position
            )
        }
    }
    
    /**
     * 执行AI走法
     */
    private fun makeAiMove() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isThinking = true)
            
            withContext(Dispatchers.Default) {
                val bestMove = chessAI.findBestMove(_chessBoard)
                bestMove
            }?.let { move ->
                _chessBoard.makeMove(move)
                _suggestedMove.value = move
            }
            
            _uiState.value = _uiState.value.copy(isThinking = false)
            updateUiState()
        }
    }
    
    /**
     * 处理拖拽走法
     */
    fun onPieceDrag(from: Position, to: Position) {
        val piece = _chessBoard.getPieceAt(from) ?: return
        
        if (piece.color != _chessBoard.currentPlayer) return
        
        val move = Move(from = from, to = to, piece = piece)
        
        if (_chessBoard.makeMove(move)) {
            // 移动成功
            if (_currentMode.value == GameMode.AI_VS_PLAYER && 
                _chessBoard.currentPlayer != getPlayerColor()) {
                makeAiMove()
            }
            updateUiState()
        }
    }
    
    /**
     * 识别相机图像
     */
    fun recognizeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRecognizing = true)
            
            withContext(Dispatchers.Default) {
                recognition.analyzeBoard(bitmap) { result ->
                    result?.let { handleRecognitionResult(it) }
                }
            }
            
            _uiState.value = _uiState.value.copy(isRecognizing = false)
        }
    }
    
    /**
     * 处理识别结果
     */
    private fun handleRecognitionResult(result: RecognitionResult) {
        if (result.confidence > 0.7f) {
            // 设置识别到的局面
            _chessBoard.setPosition(result.pieces)
            
            _uiState.value = _uiState.value.copy(
                recognitionConfidence = result.confidence,
                showRecognitionSuccess = true
            )
            
            // 计算建议走法
            calculateSuggestedMove()
        } else {
            _uiState.value = _uiState.value.copy(
                recognitionConfidence = result.confidence,
                showRecognitionError = true
            )
        }
    }
    
    /**
     * 计算建议走法
     */
    fun calculateSuggestedMove() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isThinking = true)
            
            withContext(Dispatchers.Default) {
                chessAI.findBestMove(_chessBoard)
            }?.let { move ->
                _suggestedMove.value = move
            }
            
            _uiState.value = _uiState.value.copy(isThinking = false)
        }
    }
    
    /**
     * 设置游戏模式
     */
    fun setMode(mode: GameMode) {
        _currentMode.value = mode
        _selectedPosition.value = null
        _suggestedMove.value = null
        
        when (mode) {
            GameMode.AI_VS_PLAYER -> {
                // AI先手
                _chessBoard.reset()
            }
            GameMode.PLAYER_VS_PLAYER -> {
                _chessBoard.reset()
            }
            GameMode.BOARD_EDIT -> {
                _chessBoard.reset()
            }
            GameMode.CAMERA_RECOGNITION -> {
                // 等待相机识别
            }
        }
        
        updateUiState()
    }
    
    /**
     * 获取玩家颜色
     */
    private fun getPlayerColor(): PieceColor {
        return when (_currentMode.value) {
            GameMode.AI_VS_PLAYER -> PieceColor.RED // 玩家红方
            GameMode.PLAYER_VS_PLAYER -> _chessBoard.currentPlayer
            GameMode.BOARD_EDIT -> PieceColor.RED
            GameMode.CAMERA_RECOGNITION -> PieceColor.RED
        }
    }
    
    /**
     * 撤销上一步
     */
    fun undoMove() {
        if (_chessBoard.undoMove()) {
            // 连续撤销两步（AI的一步和玩家的一步）
            if (_currentMode.value == GameMode.AI_VS_PLAYER) {
                _chessBoard.undoMove()
            }
            _suggestedMove.value = null
            updateUiState()
        }
    }
    
    /**
     * 重新开始
     */
    fun restart() {
        _chessBoard.reset()
        _selectedPosition.value = null
        _suggestedMove.value = null
        updateUiState()
    }
    
    /**
     * 更新UI状态
     */
    private fun updateUiState() {
        _uiState.value = _uiState.value.copy(
            currentPlayer = _chessBoard.currentPlayer,
            gameState = _chessBoard.gameState,
            moveCount = _chessBoard.getMoveCount()
        )
    }
    
    /**
     * 添加棋子（摆棋模式）
     */
    fun addPiece(type: PieceType, color: PieceColor, position: Position) {
        // 注意：ChessBoard需要添加添加/移除棋子的方法
        val piece = ChessPiece(type, color, position)
        // 这里需要ChessBoard提供添加棋子的接口
    }
    
    /**
     * 清除所有棋子（摆棋模式）
     */
    fun clearBoard() {
        // 清除棋盘上所有棋子
    }
}

/**
 * 象棋APP UI状态
 */
data class ChessUiState(
    val currentPlayer: PieceColor = PieceColor.RED,
    val gameState: GameState = GameState.PLAYING,
    val moveCount: Int = 0,
    val isThinking: Boolean = false,
    val isRecognizing: Boolean = false,
    val recognitionConfidence: Float = 0f,
    val showPiecePicker: Boolean = false,
    val pickedPosition: Position? = null,
    val showRecognitionSuccess: Boolean = false,
    val showRecognitionError: Boolean = false
)

/**
 * 游戏模式
 */
enum class GameMode {
    AI_VS_PLAYER,      // 人机对战
    PLAYER_VS_PLAYER,   // 双人对战
    BOARD_EDIT,         // 摆棋模式
    CAMERA_RECOGNITION  // 相机识别
}

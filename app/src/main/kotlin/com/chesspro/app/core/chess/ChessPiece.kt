package com.chesspro.app.core.chess

/**
 * 象棋棋子类
 * @param type 棋子类型
 * @param color 棋子颜色
 * @param position 当前位置
 */
data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val position: Position
) {
    /**
     * 获取棋子显示符号（中文）
     */
    fun getSymbol(): String {
        return when (type) {
            PieceType.JU -> "車"
            PieceType.MA -> "馬"
            PieceType.XIANG -> "象"
            PieceType.SHI -> "士"
            PieceType.JIANG -> if (color == PieceColor.RED) "帥" else "將"
            PieceType.PAO -> "炮"
            PieceType.BING -> if (color == PieceColor.RED) "兵" else "卒"
        }
    }

    /**
     * 获取棋子简写符号
     */
    fun getShortSymbol(): String {
        return when (type) {
            PieceType.JU -> "車"
            PieceType.MA -> "馬"
            PieceType.XIANG -> "象"
            PieceType.SHI -> "士"
            PieceType.JIANG -> if (color == PieceColor.RED) "帥" else "將"
            PieceType.PAO -> "炮"
            PieceType.BING -> if (color == PieceColor.RED) "兵" else "卒"
        }
    }

    /**
     * 获取棋子英文标识
     */
    fun getEnglishSymbol(): String {
        return "${color.name.take(1)}_${type.name}"
    }

    /**
     * 判断该棋子是否在正确位置
     */
    fun isInCorrectPosition(): Boolean {
        return when (color) {
            PieceColor.RED -> {
                when (type) {
                    PieceType.JU -> position.y == 9 && position.x in listOf(0, 8)
                    PieceType.MA -> position.y == 9 && position.x in listOf(1, 7)
                    PieceType.XIANG -> position.y == 9 && position.x in listOf(2, 6)
                    PieceType.SHI -> position.y == 9 && position.x == 4
                    PieceType.JIANG -> position.y == 9 && position.x == 4
                    PieceType.PAO -> position.y == 7 && position.x in listOf(1, 7)
                    PieceType.BING -> position.y == 6 && position.x in listOf(0, 2, 4, 6, 8)
                }
            }
            PieceColor.BLACK -> {
                when (type) {
                    PieceType.JU -> position.y == 0 && position.x in listOf(0, 8)
                    PieceType.MA -> position.y == 0 && position.x in listOf(1, 7)
                    PieceType.XIANG -> position.y == 0 && position.x in listOf(2, 6)
                    PieceType.SHI -> position.y == 0 && position.x == 4
                    PieceType.JIANG -> position.y == 0 && position.x == 4
                    PieceType.PAO -> position.y == 2 && position.x in listOf(1, 7)
                    PieceType.BING -> position.y == 3 && position.x in listOf(0, 2, 4, 6, 8)
                }
            }
        }
    }

    /**
     * 创建副本，更新位置
     */
    fun withPosition(newPosition: Position): ChessPiece {
        return copy(position = newPosition)
    }
}

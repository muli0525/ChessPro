package com.chesspro.app.core.chess

/**
 * 棋盘位置类
 * @param x 横向坐标（0-8，从左到右）
 * @param y 纵向坐标（0-9，从下到上）
 */
data class Position(val x: Int, val y: Int) {
    init {
        require(x in 0..8) { "x坐标必须在0-8之间，当前值：$x" }
        require(y in 0..9) { "y坐标必须在0-9之间，当前值：$y" }
    }

    /**
     * 判断是否在九宫格内
     */
    fun isInPalace(color: PieceColor): Boolean {
        return when (color) {
            PieceColor.RED -> x in 3..5 && y in 7..9
            PieceColor.BLACK -> x in 3..5 && y in 0..2
        }
    }

    /**
     * 判断是否过河（兵/卒）
     */
    fun isAcrossRiver(color: PieceColor): Boolean {
        return when (color) {
            PieceColor.RED -> y < 5
            PieceColor.BLACK -> y > 4
        }
    }

    override fun toString(): String {
        val xStr = "一二三四五六七八九"[x]
        val yStr = when (y) {
            0 -> "一"
            1 -> "二"
            2 -> "三"
            3 -> "四"
            4 -> "五"
            5 -> "六"
            6 -> "七"
            7 -> "八"
            8 -> "九"
            9 -> "十"
            else -> ""
        }
        return "$xStr$yStr"
    }

    companion object {
        /**
         * 从字符串解析位置
         */
        fun fromString(str: String): Position? {
            if (str.length != 2) return null
            val xStr = str[0]
            val yStr = str[1]
            
            val x = when (xStr) {
                '一' -> 0
                '二' -> 1
                '三' -> 2
                '四' -> 3
                '五' -> 4
                '六' -> 5
                '七' -> 6
                '八' -> 7
                '九' -> 8
                else -> return null
            }
            
            val y = when (yStr) {
                '一' -> 0
                '二' -> 1
                '三' -> 2
                '四' -> 3
                '五' -> 4
                '六' -> 5
                '七' -> 6
                '八' -> 7
                '九' -> 8
                '十' -> 9
                else -> return null
            }
            
            return Position(x, y)
        }
    }
}

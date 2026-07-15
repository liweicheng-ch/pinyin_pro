package com.pinyinkids.game

import java.io.Serializable

/** 游戏类别 */
enum class GameType(val label: String, val icon: String) {
    PATTERN("找规律", "🧩"),
    CLASSIFY("分类能力", "📦"),
    SORTING("排序", "📊"),
    SPATIAL("空间能力", "🧊")
}

/** 一个游戏关卡 */
data class GameLevel(
    val id: Int,
    val type: GameType,
    val title: String,
    val question: String,
    val items: List<GameItem>,
    val correctOrder: List<Int>,   // 正确顺序的 item id 列表
    val options: List<Int>? = null // 选择题选项索引(针对分类/规律)
) : Serializable

/** 游戏元素 */
data class GameItem(
    val id: Int,
    val label: String,
    val emoji: String,
    val color: Int = 0xFFE0E0E0.toInt(),
    val category: String? = null  // 分类用
) : Serializable

/** 游戏题库 */
object GameData {

    // ==================== 找规律 (图案/数字) ====================
    private val patternLevels = listOf(
        GameLevel(
            101, GameType.PATTERN, "颜色交替",
            "接下来该是什么颜色？",
            listOf(
                GameItem(1, "红色", "🔴", 0xFFFF6B6B.toInt()),
                GameItem(2, "蓝色", "🔵", 0xFF4D96FF.toInt()),
                GameItem(3, "红色", "🔴", 0xFFFF6B6B.toInt()),
                GameItem(4, "蓝色", "🔵", 0xFF4D96FF.toInt()),
                GameItem(5, "红色", "🔴", 0xFFFF6B6B.toInt()),
                GameItem(6, "?", "❓", 0xFFE0E0E0.toInt())
            ),
            listOf(1, 2, 3, 4, 5, 6),
            listOf(2) // 蓝色
        ),
        GameLevel(
            102, GameType.PATTERN, "形状交替",
            "下一个是什么形状？",
            listOf(
                GameItem(1, "圆形", "○"),
                GameItem(2, "方形", "□"),
                GameItem(3, "圆形", "○"),
                GameItem(4, "方形", "□"),
                GameItem(5, "圆形", "○"),
                GameItem(6, "?", "❓")
            ),
            listOf(1, 2, 3, 4, 5, 6),
            listOf(2) // 方形
        ),
        GameLevel(
            103, GameType.PATTERN, "数字递增",
            "下一个数字是什么？2, 4, 6, 8, ?",
            listOf(
                GameItem(1, "2", "2️⃣"),
                GameItem(2, "4", "4️⃣"),
                GameItem(3, "6", "6️⃣"),
                GameItem(4, "8", "8️⃣"),
                GameItem(5, "?", "❓")
            ),
            listOf(1, 2, 3, 4, 5),
            listOf(10) // 10
        ),
        GameLevel(
            104, GameType.PATTERN, "表情规律",
            "观察表情的顺序，接下来该是什么？",
            listOf(
                GameItem(1, "开心", "😊"),
                GameItem(2, "伤心", "😢"),
                GameItem(3, "开心", "😊"),
                GameItem(4, "伤心", "😢"),
                GameItem(5, "开心", "😊"),
                GameItem(6, "?", "❓")
            ),
            listOf(1, 2, 3, 4, 5, 6),
            listOf(2) // 伤心
        ),
        GameLevel(
            105, GameType.PATTERN, "大小规律",
            "接下来是大的还是小的？",
            listOf(
                GameItem(1, "大", "🐘"),
                GameItem(2, "小", "🐁"),
                GameItem(3, "大", "🐘"),
                GameItem(4, "小", "🐁"),
                GameItem(5, "大", "🐘"),
                GameItem(6, "?", "❓")
            ),
            listOf(1, 2, 3, 4, 5, 6),
            listOf(2) // 小
        )
    )

    // ==================== 分类能力 ====================
    private val classifyLevels = listOf(
        GameLevel(
            201, GameType.CLASSIFY, "动物分类",
            "选出下面所有的动物",
            listOf(
                GameItem(1, "猫", "🐱", category = "animal"),
                GameItem(2, "苹果", "🍎", category = "food"),
                GameItem(3, "狗", "🐶", category = "animal"),
                GameItem(4, "椅子", "🪑", category = "furniture"),
                GameItem(5, "兔子", "🐰", category = "animal"),
                GameItem(6, "汽车", "🚗", category = "vehicle")
            ),
            listOf(1, 3, 5)
        ),
        GameLevel(
            202, GameType.CLASSIFY, "食物分类",
            "选出下面所有的食物",
            listOf(
                GameItem(1, "西瓜", "🍉", category = "food"),
                GameItem(2, "太阳", "☀️", category = "nature"),
                GameItem(3, "蛋糕", "🎂", category = "food"),
                GameItem(4, "书本", "📚", category = "toy"),
                GameItem(5, "冰淇淋", "🍦", category = "food"),
                GameItem(6, "月亮", "🌙", category = "nature")
            ),
            listOf(1, 3, 5)
        ),
        GameLevel(
            203, GameType.CLASSIFY, "交通工具",
            "选出下面所有的交通工具",
            listOf(
                GameItem(1, "飞机", "✈️", category = "vehicle"),
                GameItem(2, "大树", "🌳", category = "nature"),
                GameItem(3, "自行车", "🚲", category = "vehicle"),
                GameItem(4, "小鱼", "🐟", category = "animal"),
                GameItem(5, "轮船", "🚢", category = "vehicle"),
                GameItem(6, "花朵", "🌸", category = "nature")
            ),
            listOf(1, 3, 5)
        ),
        GameLevel(
            204, GameType.CLASSIFY, "水果分类",
            "选出下面所有的水果",
            listOf(
                GameItem(1, "草莓", "🍓", category = "fruit"),
                GameItem(2, "铅笔", "✏️", category = "tool"),
                GameItem(3, "香蕉", "🍌", category = "fruit"),
                GameItem(4, "桌子", "🪑", category = "furniture"),
                GameItem(5, "葡萄", "🍇", category = "fruit"),
                GameItem(6, "电视", "📺", category = "electronics")
            ),
            listOf(1, 3, 5)
        )
    )

    // ==================== 排序能力 ====================
    private val sortingLevels = listOf(
        GameLevel(
            301, GameType.SORTING, "从小到大",
            "把下面的数字从小到大排列",
            listOf(
                GameItem(1, "3", "3️⃣"),
                GameItem(2, "1", "1️⃣"),
                GameItem(3, "5", "5️⃣"),
                GameItem(4, "2", "2️⃣"),
                GameItem(5, "4", "4️⃣")
            ),
            listOf(2, 4, 1, 5, 3) // 1,2,3,4,5
        ),
        GameLevel(
            302, GameType.SORTING, "动物大小",
            "把动物从小到大排列",
            listOf(
                GameItem(1, "大象", "🐘"),
                GameItem(2, "蚂蚁", "🐜"),
                GameItem(3, "狗", "🐶"),
                GameItem(4, "老鼠", "🐁")
            ),
            listOf(2, 4, 3, 1) // ant, mouse, dog, elephant
        ),
        GameLevel(
            303, GameType.SORTING, "高矮排序",
            "把动物从矮到高排列",
            listOf(
                GameItem(1, "长颈鹿", "🦒"),
                GameItem(2, "兔子", "🐰"),
                GameItem(3, "老虎", "🐯"),
                GameItem(4, "乌龟", "🐢")
            ),
            listOf(4, 2, 3, 1) // turtle, rabbit, tiger, giraffe
        ),
        GameLevel(
            304, GameType.SORTING, "从大到小",
            "把数字从大到小排列",
            listOf(
                GameItem(1, "7", "7️⃣"),
                GameItem(2, "3", "3️⃣"),
                GameItem(3, "9", "9️⃣"),
                GameItem(4, "1", "1️⃣"),
                GameItem(5, "5", "5️⃣")
            ),
            listOf(3, 1, 5, 2, 4) // 9,7,5,3,1
        )
    )

    // ==================== 空间能力 ====================
    private val spatialLevels = listOf(
        GameLevel(
            401, GameType.SPATIAL, "左右辨别",
            "小猫在小狗的哪边？",
            listOf(
                GameItem(1, "猫", "🐱"),
                GameItem(2, "狗", "🐶")
            ),
            listOf(1), // 左边 → 选"左边"
            listOf(1)  // 左
        ),
        GameLevel(
            402, GameType.SPATIAL, "上下辨别",
            "小鸟在大树哪里？",
            listOf(
                GameItem(1, "大树", "🌳"),
                GameItem(2, "小鸟", "🐦")
            ),
            listOf(2), // 上面
            listOf(2)  // 上
        ),
        GameLevel(
            403, GameType.SPATIAL, "拼图",
            "哪块拼图能补全正方形？",
            listOf(
                GameItem(1, "选项 A", "⬛"),
                GameItem(2, "选项 B", "⬜"),
                GameItem(3, "选项 C", "🔲"),
                GameItem(4, "选项 D", "✅")  // 正确答案
            ),
            listOf(4),
            listOf(4)
        ),
        GameLevel(
            404, GameType.SPATIAL, "前后辨别",
            "排队顺序：谁排在最后？",
            listOf(
                GameItem(1, "小熊", "🧸"),
                GameItem(2, "皮球", "⚽"),
                GameItem(3, "积木", "🧱"),
                GameItem(4, "娃娃", "🎎")
            ),
            listOf(4),
            listOf(4)
        )
    )

    /** 获取所有关卡 */
    fun allLevels(): List<GameLevel> =
        patternLevels + classifyLevels + sortingLevels + spatialLevels

    /** 按类型获取关卡 */
    fun levelsByType(type: GameType): List<GameLevel> = when (type) {
        GameType.PATTERN -> patternLevels
        GameType.CLASSIFY -> classifyLevels
        GameType.SORTING -> sortingLevels
        GameType.SPATIAL -> spatialLevels
    }

    /** 随机选关 */
    fun randomLevels(type: GameType, count: Int = 3): List<GameLevel> =
        levelsByType(type).shuffled().take(count)
}

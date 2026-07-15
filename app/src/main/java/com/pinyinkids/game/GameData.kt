package com.pinyinkids.game

import java.io.Serializable

/** 游戏类别 */
enum class GameType(val label: String, val icon: String, val color: Int) {
    PATH("小猴子过河", "🐵", 0xFFFF6B6B.toInt()),
    CLASSIFY("帮小熊整理", "🧸", 0xFF4ECDC4.toInt()),
    SHAPE("修复机器人", "🤖", 0xFFA8E6CF.toInt()),
    TREASURE("找宝藏", "💎", 0xFFFFD93D.toInt()),
    PATTERN("魔法森林", "🌳", 0xFF6C5CE7.toInt()),
    MAZE("太空迷宫", "🚀", 0xFF4D96FF.toInt()),
    SHOPPING("超市购物", "🛒", 0xFFFFA94D.toInt()),
}

/** 关卡 */
data class GameLevel(
    val id: Int,
    val type: GameType,
    val title: String,
    val story: String,
    val question: String,
    val items: List<GameItem>,
    val correctAnswer: List<Int>,  // item id(s) = 正确答案
    val options: List<Int>? = null, // 干扰项 item id 或选项索引
    val hint: String = "",
    val stars: Int = 3
) : Serializable

/** 游戏元素 */
data class GameItem(
    val id: Int,
    val label: String,
    val emoji: String,
    val category: String? = null
) : Serializable

/** 冒险主题题库 */
object GameData {

    // ======= 1. 小猴子过河 (路径规划) =======
    // 看到石头排列, 选正确顺序的过河路线
    private val pathLevels = listOf(
        GameLevel(
            101, GameType.PATH, "三块石头",
            "小猴子要过河摘桃子 🍑，河里有三块石头。\n帮它找到正确的路线！",
            "点石头: 从左边跳到右边",
            listOf(
                GameItem(1, "石头A", "🪨"),
                GameItem(2, "石头B", "🪨"),
                GameItem(3, "石头C", "🪨"),
            ),
            listOf(1, 2, 3),
            null,
            "从左到右依次跳过去",
            3
        ),
        GameLevel(
            102, GameType.PATH, "四块石头",
            "小猴子过了第一条河，前面还有一条！\n河里有四块石头，哪条路最安全？",
            "从下到上选路线",
            listOf(
                GameItem(1, "石头A", "🪨"),
                GameItem(2, "石头B", "🪨"),
                GameItem(3, "石头C", "🪨"),
                GameItem(4, "石头D", "🪨"),
            ),
            listOf(1, 3, 2, 4),
            null,
            "踩第1→3→2→4块石头",
            3
        ),
        GameLevel(
            103, GameType.PATH, "躲避鳄鱼",
            "河里有鳄鱼 🐊！只能踩安全的大石头。\n哪些石头能安全过河？",
            "选中所有安全的石头",
            listOf(
                GameItem(1, "安全", "🪨"),
                GameItem(2, "危险!", "🐊"),
                GameItem(3, "安全", "🪨"),
                GameItem(4, "危险!", "🐊"),
                GameItem(5, "安全", "🪨"),
            ),
            listOf(1, 3, 5),
            null,
            "选所有安全的石头，跳过鳄鱼",
            3
        ),
    )

    // ======= 2. 帮小熊整理玩具 (分类) =======
    private val classifyLevels = listOf(
        GameLevel(
            201, GameType.CLASSIFY, "玩具分类",
            "小熊的玩具堆成山了 😅\n帮它把玩具放进两个筐子里！",
            "选所有玩具车, 点确认",
            listOf(
                GameItem(1, "小汽车", "🚗", "vehicle"),
                GameItem(2, "泰迪熊", "🧸", "doll"),
                GameItem(3, "火车", "🚂", "vehicle"),
                GameItem(4, "洋娃娃", "🎎", "doll"),
                GameItem(5, "飞机", "✈️", "vehicle"),
            ),
            listOf(1, 3, 5),
            null,
            "找出所有交通工具",
            3
        ),
        GameLevel(
            202, GameType.CLASSIFY, "水果和零食",
            "小熊开派对 🎉，要把水果和零食分开摆盘。",
            "选出所有水果",
            listOf(
                GameItem(1, "苹果", "🍎", "fruit"),
                GameItem(2, "饼干", "🍪", "snack"),
                GameItem(3, "香蕉", "🍌", "fruit"),
                GameItem(4, "糖果", "🍬", "snack"),
                GameItem(5, "草莓", "🍓", "fruit"),
                GameItem(6, "巧克力", "🍫", "snack"),
            ),
            listOf(1, 3, 5),
            null,
            "找水果, 不要零食",
            3
        ),
        GameLevel(
            203, GameType.CLASSIFY, "三个筐子",
            "小熊有三个筐子: 玩具、书本、衣服。\n帮它分清楚！",
            "选出所有衣服类物品",
            listOf(
                GameItem(1, "帽子", "🧢", "clothes"),
                GameItem(2, "故事书", "📖", "book"),
                GameItem(3, "积木", "🧱", "toy"),
                GameItem(4, "鞋子", "👟", "clothes"),
                GameItem(5, "涂色书", "🎨", "book"),
                GameItem(6, "小熊玩偶", "🐻", "toy"),
            ),
            listOf(1, 4),
            null,
            "找出帽子和鞋子",
            3
        ),
    )

    // ======= 3. 修复机器人 (图形拼接) =======
    private val shapeLevels = listOf(
        GameLevel(
            301, GameType.SHAPE, "补上轮胎",
            "机器人小铁的轮子掉了！🔧\n帮它找到正确的轮子安上。",
            "哪个图形能当轮子？",
            listOf(
                GameItem(1, "汽车", "🚗"),
                GameItem(2, "轮子", "⬤"),
                GameItem(3, "方块", "⬛"),
                GameItem(4, "三角", "▲"),
            ),
            listOf(2),
            listOf(1, 3, 4),
            "轮子是圆形的哦",
            3
        ),
        GameLevel(
            302, GameType.SHAPE, "装上天线",
            "机器人的天线断了 📡\n需要找一个长长的零件。",
            "哪个零件最适合当天线？",
            listOf(
                GameItem(1, "棍子", "📏"),
                GameItem(2, "球", "⚽"),
                GameItem(3, "方块", "📦"),
                GameItem(4, "绳子", "🧵"),
            ),
            listOf(1),
            listOf(2, 3, 4),
            "天线是长长的、直直的",
            3
        ),
        GameLevel(
            303, GameType.SHAPE, "修补胸口",
            "机器人胸口缺了一块屏幕 🖥️\n哪块拼图能补上？",
            "选中正确的拼图",
            listOf(
                GameItem(1, "方形屏幕", "🟦"),
                GameItem(2, "圆形", "🟠"),
                GameItem(3, "星形", "⭐"),
                GameItem(4, "心形", "❤️"),
            ),
            listOf(1),
            listOf(2, 3, 4),
            "屏幕是方形的",
            3
        ),
    )

    // ======= 4. 找宝藏 (观察与专注) =======
    private val treasureLevels = listOf(
        GameLevel(
            401, GameType.TREASURE, "寻找金币",
            "海盗藏了一袋金币 💰\n在箱子堆里找到它！",
            "哪个箱子里藏着金币？",
            listOf(
                GameItem(1, "木箱", "📦"),
                GameItem(2, "宝箱", "🧰"),
                GameItem(3, "桶", "🪣"),
                GameItem(4, "陶罐", "🏺"),
            ),
            listOf(2),
            null,
            "金币藏在宝箱里, 不是普通箱子",
            3
        ),
        GameLevel(
            402, GameType.TREASURE, "森林寻宝",
            "森林里藏着5颗宝石 ✨\n把它们都找出来！",
            "找到所有的宝石 (点选)",
            listOf(
                GameItem(1, "红宝石", "🔴"),
                GameItem(2, "树叶", "🍃"),
                GameItem(3, "蓝宝石", "🔵"),
                GameItem(4, "蘑菇", "🍄"),
                GameItem(5, "绿宝石", "🟢"),
                GameItem(6, "石头", "🪨"),
                GameItem(7, "钻石", "💎"),
            ),
            listOf(1, 3, 5, 7),
            null,
            "闪光的才是宝石！",
            3
        ),
        GameLevel(
            403, GameType.TREASURE, "藏宝图",
            "老船长给了你藏宝图 🗺️\n按图上的线索找到宝藏位置！",
            "哪个位置藏着宝藏？线索: 大树下, 石头旁",
            listOf(
                GameItem(1, "大树底下", "🌳"),
                GameItem(2, "石头旁边", "🪨"),
                GameItem(3, "小河对岸", "🌊"),
                GameItem(4, "山洞里面", "🕳️"),
            ),
            listOf(1),
            null,
            "先看大树下",
            3
        ),
    )

    // ======= 5. 魔法森林 (找规律) =======
    private val patternLevels = listOf(
        GameLevel(
            501, GameType.PATTERN, "蘑菇排序",
            "魔法森林的小蘑菇排着队跳舞 💃\n下一个该什么颜色？",
            "观察规律: 🟠🟣🟠🟣?",
            listOf(
                GameItem(1, "橙色", "🟠"),
                GameItem(2, "紫色", "🟣"),
                GameItem(3, "橙色", "🟠"),
                GameItem(4, "紫色", "🟣"),
                GameItem(5, "?", "❓"),
            ),
            listOf(2),
            null,
            "橙紫交替出现",
            3
        ),
        GameLevel(
            502, GameType.PATTERN, "精灵舞蹈",
            "森林小精灵在跳舞 🧚\n它们按照顺序出场！",
            "接下来该哪个小精灵？👼🧚👼🧚?",
            listOf(
                GameItem(1, "天使", "👼"),
                GameItem(2, "精灵", "🧚"),
                GameItem(3, "天使", "👼"),
                GameItem(4, "精灵", "🧚"),
                GameItem(5, "?", "❓"),
            ),
            listOf(2),
            null,
            "天使精灵交替",
            3
        ),
        GameLevel(
            503, GameType.PATTERN, "魔法药水",
            "猫头鹰巫师在调配魔法药水 🧙\n瓶子的颜色有规律, 下一瓶是什么？",
            "🔴🔵🔴🔵🔴?",
            listOf(
                GameItem(1, "红色", "🔴"),
                GameItem(2, "蓝色", "🔵"),
                GameItem(3, "红色", "🔴"),
                GameItem(4, "蓝色", "🔵"),
                GameItem(5, "红色", "🔴"),
                GameItem(6, "?", "❓"),
            ),
            listOf(2),
            null,
            "红蓝交替",
            3
        ),
    )

    // ======= 6. 太空迷宫 (空间思维) =======
    private val mazeLevels = listOf(
        GameLevel(
            601, GameType.MAZE, "左转右转",
            "小火箭要飞出地球 🚀\n控制它向左还是向右飞！",
            "火箭往哪边飞能躲开陨石？",
            listOf(
                GameItem(1, "← 左边", "⬅️"),
                GameItem(2, "→ 右边", "➡️"),
                GameItem(3, "↑ 上面", "⬆️"),
            ),
            listOf(2),
            null,
            "陨石在左边, 往右躲",
            3
        ),
        GameLevel(
            602, GameType.MAZE, "星际导航",
            "太空站的信号在哪个方向 📡\n帮飞船找到正确的方向！",
            "信号从哪个方向来？",
            listOf(
                GameItem(1, "左上方", "↖️"),
                GameItem(2, "右上方", "↗️"),
                GameItem(3, "左下方", "↙️"),
                GameItem(4, "右下方", "↘️"),
            ),
            listOf(1),
            null,
            "信号在左上方闪烁",
            3
        ),
        GameLevel(
            603, GameType.MAZE, "穿越小行星带",
            "前方是小行星带！\n走哪条路线最安全？",
            "选安全的飞行路线 (3步)",
            listOf(
                GameItem(1, "路线A: 左上右", "🔴"),
                GameItem(2, "路线B: 右右上", "🟢"),
                GameItem(3, "路线C: 上左上", "🔵"),
            ),
            listOf(3),
            null,
            "选绿色的路线",
            3
        ),
    )

    // ======= 7. 超市购物 (数学+分类) =======
    private val shoppingLevels = listOf(
        GameLevel(
            701, GameType.SHOPPING, "买水果",
            "妈妈给了3块钱 🪙\n去超市买水果吧！",
            "你有3元, 哪个水果买得起？",
            listOf(
                GameItem(1, "苹果 1元", "🍎"),
                GameItem(2, "西瓜 5元", "🍉"),
                GameItem(3, "香蕉 2元", "🍌"),
                GameItem(4, "葡萄 4元", "🍇"),
            ),
            listOf(1, 3),
            null,
            "只能买价格≤3元的水果",
            3
        ),
        GameLevel(
            702, GameType.SHOPPING, "采购清单",
            "要买这些东西:\n🍞🍳🥛 共需要几元？",
            "面包2元 + 鸡蛋3元 + 牛奶2元 = ?",
            listOf(
                GameItem(1, "5元", "5️⃣"),
                GameItem(2, "6元", "6️⃣"),
                GameItem(3, "7元", "7️⃣"),
                GameItem(4, "8元", "8️⃣"),
            ),
            listOf(3),
            null,
            "2+3+2 = ？",
            3
        ),
        GameLevel(
            703, GameType.SHOPPING, "找零钱",
            "你买了5元的东西, 给了10元 💵\n收银员该找你几元？",
            "10元 - 5元 = ?",
            listOf(
                GameItem(1, "3元", "3️⃣"),
                GameItem(2, "4元", "4️⃣"),
                GameItem(3, "5元", "5️⃣"),
                GameItem(4, "6元", "6️⃣"),
            ),
            listOf(3),
            null,
            "10-5=5",
            3
        ),
    )

    /** 冒险列表 */
    val adventures: List<Pair<GameType, List<GameLevel>>> = listOf(
        GameType.PATH to pathLevels,
        GameType.CLASSIFY to classifyLevels,
        GameType.SHAPE to shapeLevels,
        GameType.TREASURE to treasureLevels,
        GameType.PATTERN to patternLevels,
        GameType.MAZE to mazeLevels,
        GameType.SHOPPING to shoppingLevels,
    )

    fun levelsByType(type: GameType): List<GameLevel> =
        adventures.find { it.first == type }?.second ?: emptyList()
}

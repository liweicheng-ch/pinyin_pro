package com.pinyinkids.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.pinyinkids.R

/** 冒险引擎 — 每种游戏类型不同交互, 故事驱动 */
class GamePlayFragment : Fragment() {

    private var levels: List<GameLevel> = emptyList()
    private var currentIndex: Int = 0
    private var score: Int = 0
    private var stage: String = ""

    private var selectedIds: MutableList<Int> = mutableListOf()
    private var sortedIds: MutableList<Int> = mutableListOf()
    private var firstPick: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        levels = arguments?.getSerializable("levels") as? List<GameLevel> ?: emptyList()
        currentIndex = arguments?.getInt("index") ?: 0
        score = arguments?.getInt("score") ?: 0
        stage = arguments?.getString("stage") ?: levels.firstOrNull()?.type?.label ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, b: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_game_play, c, false)
        renderLevel(root)
        return root
    }

    private fun renderLevel(root: View) {
        if (currentIndex >= levels.size) {
            showResult(root)
            return
        }

        val level = levels[currentIndex]
        selectedIds.clear()
        firstPick = null

        root.findViewById<View>(R.id.gameConfirmBtn).visibility = View.GONE
        root.findViewById<View>(R.id.gameOptionRow).visibility = View.GONE

        // 冒险风格 header
        val type = level.type
        root.findViewById<TextView>(R.id.gameTypeLabel).text = "${type.icon} ${type.label}"
        root.findViewById<TextView>(R.id.gameProgress).text =
            "第${currentIndex + 1}关 / 共${levels.size}关 ⭐$score"

        // 故事 + 题目
        root.findViewById<TextView>(R.id.gameTitle).text = level.title
        root.findViewById<TextView>(R.id.gameQuestion).text = "${level.story}\n\n${level.question}"

        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        container.removeAllViews()

        when (type) {
            GameType.PATH -> renderPath(root, level)
            GameType.CLASSIFY -> renderClassify(root, level)
            GameType.SHAPE -> renderShape(root, level)
            GameType.TREASURE -> renderTreasure(root, level)
            GameType.PATTERN -> renderPattern(root, level)
            GameType.MAZE -> renderMaze(root, level)
            GameType.SHOPPING -> renderShopping(root, level)
        }
    }

    // ======= 小猴子过河: 按顺序点石头 =======
    private fun renderPath(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}"

        for (item in level.items) {
            val card = inflater.inflate(R.layout.item_game_card, container, false).apply {
                tag = item.id
            }
            card.findViewById<TextView>(R.id.gameCardText).apply {
                text = item.emoji
                textSize = 40f
            }
            card.findViewById<TextView>(R.id.gameCardLabel).apply {
                text = item.label
                visibility = View.VISIBLE
            }
            val targetOrder = level.correctAnswer
            card.setOnClickListener { v ->
                val id = v.tag as Int
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id)
                    v.setBackgroundColor(0xFFFFFFFF.toInt())
                } else {
                    selectedIds.add(id)
                    v.setBackgroundColor(0xFFA8E6CF.toInt())
                }
                // 自动判断: 选够了就检查
                if (selectedIds.size == targetOrder.size) {
                    checkAnswer(root)
                }
            }
            container.addView(card)
        }
    }

    // ======= 帮小熊整理: 分类点选+确认 =======
    private fun renderClassify(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}\n\n(点选物品, 点确认)"

        for (item in level.items.shuffled()) {
            val card = inflater.inflate(R.layout.item_game_card, container, false).apply {
                tag = item.id
            }
            card.findViewById<TextView>(R.id.gameCardText).apply {
                text = item.emoji
                textSize = 40f
            }
            card.findViewById<TextView>(R.id.gameCardLabel).apply {
                text = item.label
                visibility = View.VISIBLE
            }
            card.setOnClickListener { v ->
                val id = v.tag as Int
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id)
                    v.setBackgroundColor(0xFFFFFFFF.toInt())
                } else {
                    selectedIds.add(id)
                    v.setBackgroundColor(0xFFA8E6CF.toInt())
                }
            }
            container.addView(card)
        }

        root.findViewById<View>(R.id.gameConfirmBtn).apply {
            visibility = View.VISIBLE
            setOnClickListener {
                if (selectedIds.isEmpty()) {
                    Toast.makeText(context, "点选物品再确认喔", Toast.LENGTH_SHORT).show()
                } else {
                    checkAnswer(root)
                }
            }
        }
    }

    // ======= 修复机器人: 选择题 =======
    private fun renderShape(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}"

        for (item in level.items) {
            val card = inflater.inflate(R.layout.item_game_card, container, false).apply {
                tag = item.id
            }
            card.findViewById<TextView>(R.id.gameCardText).apply {
                text = item.emoji
                textSize = 48f
            }
            card.findViewById<TextView>(R.id.gameCardLabel).apply {
                text = item.label
                visibility = View.VISIBLE
            }
            card.setOnClickListener { v ->
                selectedIds = mutableListOf(v.tag as Int)
                checkAnswer(root)
            }
            container.addView(card)
        }
    }

    // ======= 找宝藏: 点选所有目标 =======
    private fun renderTreasure(root: View, level: GameLevel) {
        renderClassify(root, level) // 复用分类交互
        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}\n\n(点选所有目标, 点确认)"
    }

    // ======= 魔法森林: 找规律 =======
    private fun renderPattern(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}"

        for (item in level.items) {
            val card = inflater.inflate(R.layout.item_game_card, container, false)
            card.findViewById<TextView>(R.id.gameCardText).apply {
                text = item.emoji
                textSize = 36f
            }
            container.addView(card)
        }

        val optRow = root.findViewById<ViewGroup>(R.id.gameOptionRow)
        optRow.removeAllViews()
        optRow.visibility = View.VISIBLE

        val correctId = level.correctAnswer.firstOrNull() ?: return
        val correctItem = level.items.find { it.id == correctId } ?: return
        val distractors = level.items.filter { it.id != correctId && it.emoji != "❓" }.shuffled()
        val choices = mutableListOf(correctItem).also { it.addAll(distractors.take(2)) }.shuffled()

        for (choice in choices) {
            val card = inflater.inflate(R.layout.item_game_card, optRow, false)
            card.findViewById<TextView>(R.id.gameCardText).apply {
                text = choice.emoji
                textSize = 36f
            }
            card.findViewById<TextView>(R.id.gameCardLabel).apply {
                text = choice.label
                visibility = View.VISIBLE
            }
            card.setOnClickListener {
                selectedIds = mutableListOf(choice.id)
                checkAnswer(root)
            }
            optRow.addView(card)
        }
    }

    // ======= 太空迷宫: 方向选择 =======
    private fun renderMaze(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}"

        for (item in level.items) {
            val card = inflater.inflate(R.layout.item_game_card, container, false).apply {
                tag = item.id
            }
            card.findViewById<TextView>(R.id.gameCardText).apply {
                text = item.emoji
                textSize = 40f
            }
            card.findViewById<TextView>(R.id.gameCardLabel).apply {
                text = item.label
                visibility = View.VISIBLE
            }
            card.setOnClickListener { v ->
                selectedIds = mutableListOf(v.tag as Int)
                checkAnswer(root)
            }
            container.addView(card)
        }
    }

    // ======= 超市购物: 数学选择 =======
    private fun renderShopping(root: View, level: GameLevel) {
        renderMaze(root, level) // 复用点击选择
        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}"
    }

    // ======= 答案检查 =======
    private fun checkAnswer(root: View) {
        val level = levels[currentIndex]
        val correct = level.correctAnswer
        val isCorrect = when (level.type) {
            GameType.PATH, GameType.TREASURE, GameType.CLASSIFY ->
                selectedIds.sorted() == correct.sorted()
            GameType.SHAPE, GameType.MAZE, GameType.SHOPPING, GameType.PATTERN ->
                selectedIds.firstOrNull() == correct.firstOrNull()
        }

        if (isCorrect) {
            score++
            val msgs = listOf("太棒了！🌟", "答对了！🎉", "厉害！👏", "真聪明！✨", "好样的！💪")
            Toast.makeText(context, "✅ ${msgs.random()}", Toast.LENGTH_SHORT).show()
            root.postDelayed({ goNext(root) }, 600)
        } else {
            val hint = level.hint
            val msg = if (hint.isNotEmpty()) "再想想哦 💡 $hint" else "再想想哦 💪"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            // 允许重试, 不自动跳转
            selectedIds.clear()
            firstPick = null
            val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
            for (i in 0 until container.childCount) {
                container.getChildAt(i).setBackgroundColor(0xFFFFFFFF.toInt())
            }
        }
    }

    private fun goNext(root: View) {
        val frag = newInstance(ArrayList(levels), currentIndex + 1, score)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, frag)
            .commit()
    }

    private fun showResult(root: View) {
        root.findViewById<ViewGroup>(R.id.gameItemContainer).removeAllViews()
        root.findViewById<View>(R.id.gameConfirmBtn).visibility = View.GONE
        root.findViewById<View>(R.id.gameOptionRow).visibility = View.GONE
        root.findViewById<TextView>(R.id.gameQuestion).text = ""

        val type = levels.firstOrNull()?.type
        val icon = type?.icon ?: "🎮"
        val name = type?.label ?: "冒险"

        root.findViewById<TextView>(R.id.gameTitle).text = when {
            score >= levels.size -> icon + " 太厉害了！你完成了" + name + "的全部挑战！🏆"
            score >= levels.size / 2 -> icon + " " + name + "挑战完成！" + score.toString() + "/" + levels.size + "关通过 👍"
            else -> icon + " " + name + "冒险结束！" + score.toString() + "/" + levels.size + "关通过，再试一次吧 💪"
        }
        root.findViewById<TextView>(R.id.gameTypeLabel).text = "✨ 冒险完成"
        root.findViewById<TextView>(R.id.gameProgress).text = "⭐ 得分: $score/${levels.size}"

        root.findViewById<View>(R.id.gameConfirmBtn).also {
            it.visibility = View.VISIBLE
            (it as? TextView)?.text = "🔙 返回冒险大厅"
            it.setOnClickListener { parentFragmentManager.popBackStack() }
        }
    }

    companion object {
        fun newInstance(levels: ArrayList<GameLevel>, index: Int, score: Int = 0): GamePlayFragment {
            val f = GamePlayFragment()
            val args = Bundle()
            args.putSerializable("levels", levels)
            args.putInt("index", index)
            args.putInt("score", score)
            f.arguments = args
            return f
        }
    }
}

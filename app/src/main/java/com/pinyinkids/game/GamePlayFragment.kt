package com.pinyinkids.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.pinyinkids.R

/** 统合游戏关卡 - 根据 type 自动切换交互模式 */
class GamePlayFragment : Fragment() {

    private var levels: List<GameLevel> = emptyList()
    private var currentIndex: Int = 0
    private var score: Int = 0

    // 当前关卡交互状态
    private var selectedIds: MutableList<Int> = mutableListOf()  // 分类/规律点到
    private var sortedIds: MutableList<Int> = mutableListOf()    // 排序拖拽

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        levels = arguments?.getSerializable("levels") as? List<GameLevel> ?: emptyList()
        currentIndex = arguments?.getInt("index") ?: 0
        score = arguments?.getInt("score") ?: 0
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
        sortedIds = level.items.map { it.id }.toMutableList()

        // type 标识
        root.findViewById<TextView>(R.id.gameTypeLabel).text = "${level.type.icon} ${level.type.label}"
        root.findViewById<TextView>(R.id.gameTitle).text = level.title
        root.findViewById<TextView>(R.id.gameQuestion).text = level.question
        root.findViewById<TextView>(R.id.gameProgress).text = "第 ${currentIndex + 1}/${levels.size} 关"

        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        container.removeAllViews()

        when (level.type) {
            GameType.PATTERN -> renderPattern(root, level)
            GameType.CLASSIFY -> renderClassify(root, level)
            GameType.SORTING -> renderSorting(root, level)
            GameType.SPATIAL -> renderSpatial(root, level)
        }
    }

    // ========= 找规律：点选答案 =========
    private fun renderPattern(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        // 显示序列(前几个显示、最后一个显示❓)
        for (item in level.items) {
            val card = inflater.inflate(R.layout.item_game_card, container, false)
            val tv = card.findViewById<TextView>(R.id.gameCardText)
            tv.text = item.emoji
            tv.textSize = 36f
            container.addView(card)
        }

        // 选项
        val optRow = root.findViewById<ViewGroup>(R.id.gameOptionRow)
        optRow.removeAllViews()
        optRow.visibility = View.VISIBLE

        val options = level.correctOrder.take(3) + (level.options?.firstOrNull() ?: 0)
        val optionLabels = listOf("选项 A", "选项 B", "选项 C")
        val optionEmojis = listOf("🔴", "🔵", "🟡")

        for (i in 0 until 3) {
            val optCard = inflater.inflate(R.layout.item_game_card, optRow, false)
            optCard.findViewById<TextView>(R.id.gameCardText).text = optionEmojis[i]
            val label = optCard.findViewById<TextView>(R.id.gameCardLabel)
            label.text = optionLabels[i]
            label.visibility = View.VISIBLE

            val idx = i
            optCard.setOnClickListener {
                selectedIds = mutableListOf(if (idx == 0) options.last() else if (idx == 1) 2 else 1)
                checkAnswer(root)
            }
            optRow.addView(optCard)
        }
    }

    // ========= 分类：点击选中 =========
    private fun renderClassify(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        // 提示
        root.findViewById<TextView>(R.id.gameQuestion).text = "${level.question}\n\n(点击选中所有同类项)"

        for (item in level.items.shuffled()) {
            val card = inflater.inflate(R.layout.item_game_card, container, false)
            card.tag = item.id
            val tv = card.findViewById<TextView>(R.id.gameCardText)
            tv.text = item.emoji
            tv.textSize = 40f
            val label = card.findViewById<TextView>(R.id.gameCardLabel)
            label.text = item.label
            label.visibility = View.VISIBLE

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

        // 确认按钮
        val confirmBtn = root.findViewById<View>(R.id.gameConfirmBtn)
        confirmBtn.visibility = View.VISIBLE
        confirmBtn.setOnClickListener { checkAnswer(root) }
    }

    // ========= 排序：点击交换 =========
    private fun renderSorting(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text = "${level.question}\n\n(点击两个交换位置)"

        sortedIds = level.items.map { it.id }.toMutableList()
        val sortedItems = level.items.shuffled().toMutableList()
        sortedIds = sortedItems.map { it.id }.toMutableList()

        var firstPick: View? = null

        for (item in sortedItems) {
            val card = inflater.inflate(R.layout.item_game_card, container, false)
            card.tag = item.id
            val tv = card.findViewById<TextView>(R.id.gameCardText)
            tv.text = item.emoji
            tv.textSize = 40f
            val label = card.findViewById<TextView>(R.id.gameCardLabel)
            label.text = item.label
            label.visibility = View.VISIBLE

            card.setOnClickListener { v ->
                if (firstPick == null) {
                    firstPick = v
                    v.setBackgroundColor(0xFFFFD93D.toInt())
                } else {
                    // 交换
                    val id1 = firstPick!!.tag as Int
                    val id2 = v.tag as Int
                    val idx1 = sortedIds.indexOf(id1)
                    val idx2 = sortedIds.indexOf(id2)
                    sortedIds[idx1] = id2
                    sortedIds[idx2] = id1

                    firstPick!!.setBackgroundColor(0xFFFFFFFF.toInt())
                    firstPick = null
                    renderSortedCards(root)
                }
            }
            container.addView(card)
        }

        // 确认
        val confirmBtn = root.findViewById<View>(R.id.gameConfirmBtn)
        confirmBtn.visibility = View.VISIBLE
        confirmBtn.setOnClickListener { checkAnswer(root) }
    }

    private fun renderSortedCards(root: View) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)
        container.removeAllViews()

        for (id in sortedIds) {
            // 找 item
            val level = levels[currentIndex]
            val item = level.items.find { it.id == id } ?: continue
            val card = inflater.inflate(R.layout.item_game_card, container, false)
            card.tag = id
            val tv = card.findViewById<TextView>(R.id.gameCardText)
            tv.text = item.emoji
            tv.textSize = 40f
            val label = card.findViewById<TextView>(R.id.gameCardLabel)
            label.text = item.label
            label.visibility = View.VISIBLE
            container.addView(card)
        }
    }

    // ========= 空间能力 =========
    private fun renderSpatial(root: View, level: GameLevel) {
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text = level.question

        // 显示空间示意
        for (item in level.items) {
            val card = inflater.inflate(R.layout.item_game_card, container, false)
            card.tag = item.id
            val tv = card.findViewById<TextView>(R.id.gameCardText)
            tv.text = item.emoji
            tv.textSize = 48f
            val label = card.findViewById<TextView>(R.id.gameCardLabel)
            label.text = item.label
            label.visibility = View.VISIBLE
            container.addView(card)
        }

        // 选项
        val optRow = root.findViewById<ViewGroup>(R.id.gameOptionRow)
        optRow.removeAllViews()
        optRow.visibility = View.VISIBLE

        val choiceLabels = listOf("左边 / 上面", "右边 / 下面", "中间", "不确定")
        for (i in 0 until 4) {
            val optCard = inflater.inflate(R.layout.item_game_card, optRow, false)
            optCard.findViewById<TextView>(R.id.gameCardText).text = choiceLabels[i].take(2)
            optCard.findViewById<TextView>(R.id.gameCardText).textSize = 24f
            val label = optCard.findViewById<TextView>(R.id.gameCardLabel)
            label.text = choiceLabels[i]
            label.visibility = View.VISIBLE

            val idx = i
            optCard.setOnClickListener {
                selectedIds = mutableListOf(idx)
                checkAnswer(root)
            }
            optRow.addView(optCard)
        }
    }

    // ========= 答案检查 =========
    private fun checkAnswer(root: View) {
        val level = levels[currentIndex]
        val correct = level.correctOrder.toSet()
        val userSet = selectedIds.toSet()

        val isCorrect = when (level.type) {
            GameType.PATTERN, GameType.SPATIAL -> userSet == correct
            GameType.CLASSIFY -> userSet == correct
            GameType.SORTING -> sortedIds == level.correctOrder
        }

        if (isCorrect) {
            score++
            Toast.makeText(context, "✅ 回答正确！太棒了！", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "❌ 再想想哦～", Toast.LENGTH_SHORT).show()
        }

        // 下一关
        root.postDelayed({
            val frag = GamePlayFragment.newInstance(ArrayList(levels), currentIndex + 1, score)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .commit()
        }, if (isCorrect) 800 else 1500)
    }

    private fun showResult(root: View) {
        root.findViewById<ViewGroup>(R.id.gameItemContainer).removeAllViews()
        root.findViewById<View>(R.id.gameConfirmBtn).visibility = View.GONE
        root.findViewById<View>(R.id.gameOptionRow).visibility = View.GONE
        root.findViewById<TextView>(R.id.gameQuestion).text = ""

        val tv = root.findViewById<TextView>(R.id.gameTitle)
        tv.text = when {
            score >= levels.size -> "🎉 全部答对！你是小天才！"
            score >= levels.size / 2 -> "👍 答对了 $score/${levels.size} 题，继续加油！"
            else -> "💪 答对了 $score/${levels.size} 题，多练习会更好！"
        }

        root.findViewById<TextView>(R.id.gameTypeLabel).text = "✨ 游戏结束"
        root.findViewById<TextView>(R.id.gameProgress).text = "最终得分: $score/${levels.size}"

        val retryBtn = root.findViewById<View>(R.id.gameConfirmBtn).also {
            it.visibility = View.VISIBLE
            (it as? TextView)?.text = "返回游戏大厅"
        }
        retryBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
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

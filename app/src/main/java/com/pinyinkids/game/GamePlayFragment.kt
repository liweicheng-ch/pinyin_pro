package com.pinyinkids.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexboxLayout
import com.pinyinkids.R

class GamePlayFragment : Fragment() {

    private var levels: List<GameLevel> = emptyList()
    private var currentIndex: Int = 0
    private var score: Int = 0
    private var stage: String = ""

    private var selectedIds: MutableList<Int> = mutableListOf()
    private var sortedIds: MutableList<Int> = mutableListOf()
    private var firstPick: View? = null

    companion object {
        private const val ARG_LEVELS = "levels"
        private const val ARG_INDEX = "index"
        private const val ARG_SCORE = "score"
        private const val ARG_STAGE = "stage"

        fun newInstance(levels: ArrayList<GameLevel>, index: Int, score: Int = 0): GamePlayFragment {
            val f = GamePlayFragment()
            val args = Bundle()
            args.putSerializable(ARG_LEVELS, levels)
            args.putInt(ARG_INDEX, index)
            args.putInt(ARG_SCORE, score)
            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNCHECKED_CAST")
        levels = arguments?.getSerializable(ARG_LEVELS) as? List<GameLevel> ?: emptyList()

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(ARG_INDEX, 0)
            score = savedInstanceState.getInt(ARG_SCORE, 0)
            stage = savedInstanceState.getString(ARG_STAGE, levels.firstOrNull()?.type?.label ?: "")
            selectedIds = (savedInstanceState.getIntegerArrayList("selectedIds") ?: mutableListOf()).toMutableList()
        } else {
            currentIndex = arguments?.getInt(ARG_INDEX) ?: 0
            score = arguments?.getInt(ARG_SCORE) ?: 0
            stage = arguments?.getString(ARG_STAGE) ?: levels.firstOrNull()?.type?.label ?: ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_INDEX, currentIndex)
        outState.putInt(ARG_SCORE, score)
        outState.putString(ARG_STAGE, stage)
        outState.putIntegerArrayList("selectedIds", ArrayList(selectedIds))
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, b: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_game_play, c, false)
        root.findViewById<View>(R.id.gameBackBtn).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
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
        root.findViewById<View>(R.id.resultBackBtn).visibility = View.GONE

        val type = level.type
        root.findViewById<TextView>(R.id.gameTypeLabel).text = "${type.icon} ${type.label}"
        root.findViewById<TextView>(R.id.gameProgress).text =
            "第${currentIndex + 1}关 / ${levels.size}关 ⭐$score"

        root.findViewById<TextView>(R.id.gameTitle).text = level.title
        root.findViewById<TextView>(R.id.gameQuestion).text = "${level.story}\n\n${level.question}"

        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
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

        // 入场动画
        container.post { animateLevelIn(container) }
    }

    // ======= 小猴子过河: 按顺序点石头 =======
    private fun renderPath(root: View, level: GameLevel) {
        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}\n\n👆 点击选中石头，再点击取消"

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
                    animateCardClick(v, false)
                } else {
                    selectedIds.add(id)
                    v.setBackgroundColor(0xFFA8E6CF.toInt())
                    animateCardClick(v, true)
                }
                // 选够了就检查
                if (selectedIds.size == targetOrder.size) {
                    checkAnswer(root)
                }
            }
            container.addView(card)
        }
    }

    // ======= 帮小熊整理: 分类点选+确认 =======
    private fun renderClassify(root: View, level: GameLevel) {
        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
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
                    animateCardClick(v, false)
                } else {
                    selectedIds.add(id)
                    v.setBackgroundColor(0xFFA8E6CF.toInt())
                    animateCardClick(v, true)
                }
            }
            container.addView(card)
        }

        root.findViewById<View>(R.id.gameConfirmBtn).apply {
            visibility = View.VISIBLE
            setOnClickListener {
                if (selectedIds.isEmpty()) {
                    showToast("点选物品再确认喔")
                } else {
                    checkAnswer(root)
                }
            }
        }
    }

    // ======= 修复机器人: 选择题 =======
    // 单答案→点选即判，多答案→点选+确认（同classify）
    private fun renderShape(root: View, level: GameLevel) {
        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        val isMultiSelect = level.correctAnswer.size > 1
        val questionExtra = if (isMultiSelect) "\n\n(点选零件, 点确认)" else ""
        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}$questionExtra"

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
                val id = v.tag as Int
                if (isMultiSelect) {
                    if (selectedIds.contains(id)) {
                        selectedIds.remove(id)
                        v.setBackgroundColor(0xFFFFFFFF.toInt())
                        animateCardClick(v, false)
                    } else {
                        selectedIds.add(id)
                        v.setBackgroundColor(0xFFA8E6CF.toInt())
                        animateCardClick(v, true)
                    }
                } else {
                    // 单选-点即判
                    selectedIds = mutableListOf(id)
                    v.setBackgroundColor(0xFFA8E6CF.toInt())
                    animateCardClick(v, true)
                    checkAnswer(root)
                }
            }
            container.addView(card)
        }

        if (isMultiSelect) {
            root.findViewById<View>(R.id.gameConfirmBtn).apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    if (selectedIds.isEmpty()) {
                        showToast("点选零件再确认喔")
                    } else {
                        checkAnswer(root)
                    }
                }
            }
        }
    }

    // ======= 找宝藏: 点选所有目标 =======
    private fun renderTreasure(root: View, level: GameLevel) {
        renderClassify(root, level)
        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}\n\n(点选所有目标, 点确认)"
    }

    // ======= 魔法森林: 找规律 =======
    // 选项: 按emoji分组去重, 保证正确答案与干扰项emoji不同
    private fun renderPattern(root: View, level: GameLevel) {
        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}"

        // 显示模式序列 (含重复emoji)
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

        // 策略1: 如果level有options, 用指定的选项
        if (level.options != null) {
            val choiceItems = level.options.mapNotNull { id -> level.items.find { it.id == id } }
            for (choice in choiceItems.shuffled()) {
                addChoiceCard(optRow, inflater, choice) {
                    selectedIds = mutableListOf(choice.id)
                    checkAnswer(root)
                }
            }
            return
        }

        // 策略2: 按emoji分组, 每个emoji只取一项作为选项
        val uniqueItems = level.items
            .filter { !it.isPlaceholder }
            .distinctBy { it.emoji }

        val correctEmoji = correctItem.emoji
        // 从不同emoji组中挑选干扰项
        val distractors = uniqueItems
            .filter { it.emoji != correctEmoji }
            .shuffled()

        val choices = mutableListOf(correctItem)
        if (distractors.size >= 2) {
            choices.addAll(distractors.take(2))
        } else {
            // fallback: 同一emoji但label不同, 按label+emoji去重
            val fallbackItems = level.items
                .filter { !it.isPlaceholder }
                .distinctBy { "${it.emoji}:${it.label}" }
            val fallbackDistractors = fallbackItems
                .filter { it.id != correctId }
                .shuffled()
            choices.addAll(fallbackDistractors.take(2))
        }
        choices.shuffle()

        for (choice in choices) {
            addChoiceCard(optRow, inflater, choice) {
                selectedIds = mutableListOf(choice.id)
                checkAnswer(root)
            }
        }
    }

    private fun addChoiceCard(
        parent: ViewGroup, inflater: LayoutInflater,
        item: GameItem, onClick: () -> Unit
    ) {
        val card = inflater.inflate(R.layout.item_game_card, parent, false)
        card.findViewById<TextView>(R.id.gameCardText).apply {
            text = item.emoji
            textSize = 36f
        }
        card.findViewById<TextView>(R.id.gameCardLabel).apply {
            text = item.label
            visibility = View.VISIBLE
        }
        card.setOnClickListener { onClick() }
        parent.addView(card)
    }

    // ======= 太空迷宫: 方向选择 =======
    private fun renderMaze(root: View, level: GameLevel) {
        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
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
                v.setBackgroundColor(0xFFA8E6CF.toInt())
                animateCardClick(v, true)
                checkAnswer(root)
            }
            container.addView(card)
        }
    }

    // ======= 超市购物: 数学选择 =======
    // 单答案→点即判，多答案→点选+确认
    private fun renderShopping(root: View, level: GameLevel) {
        val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
        val inflater = LayoutInflater.from(context)

        val isMultiSelect = level.correctAnswer.size > 1
        val questionExtra = if (isMultiSelect) "\n\n(点选物品, 点确认)" else ""
        root.findViewById<TextView>(R.id.gameQuestion).text =
            "${level.story}\n\n${level.question}$questionExtra"

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
                val id = v.tag as Int
                if (isMultiSelect) {
                    if (selectedIds.contains(id)) {
                        selectedIds.remove(id)
                        v.setBackgroundColor(0xFFFFFFFF.toInt())
                        animateCardClick(v, false)
                    } else {
                        selectedIds.add(id)
                        v.setBackgroundColor(0xFFA8E6CF.toInt())
                        animateCardClick(v, true)
                    }
                } else {
                    selectedIds = mutableListOf(id)
                    v.setBackgroundColor(0xFFA8E6CF.toInt())
                    animateCardClick(v, true)
                    checkAnswer(root)
                }
            }
            container.addView(card)
        }

        if (isMultiSelect) {
            root.findViewById<View>(R.id.gameConfirmBtn).apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    if (selectedIds.isEmpty()) showToast("点选物品再确认喔")
                    else checkAnswer(root)
                }
            }
        }
    }

    private fun animateCardClick(v: View, isSelected: Boolean) {
        v.animate().scaleX(if (isSelected) 0.92f else 1.0f)
            .scaleY(if (isSelected) 0.92f else 1.0f)
            .setDuration(120)
            .start()
    }

    private fun animateShake(v: View) {
        v.animate().translationX(12f).setDuration(60).withEndAction {
            v.animate().translationX(-12f).setDuration(60).withEndAction {
                v.animate().translationX(6f).setDuration(60).withEndAction {
                    v.animate().translationX(0f).setDuration(60).start()
                }.start()
            }.start()
        }.start()
    }

    private fun animateStarBurst(root: View) {
        val star = LayoutInflater.from(context).inflate(R.layout.item_game_card, root.findViewById(R.id.gameItemContainer), false)
        star.findViewById<TextView>(R.id.gameCardText).apply { text = "⭐"; textSize = 60f }
        star.findViewById<View>(R.id.gameCardLabel).visibility = View.GONE
        val container = root.findViewById<ViewGroup>(R.id.gameItemContainer)
        container.addView(star)
        star.animate()
            .scaleX(2.5f).scaleY(2.5f).alpha(0f).setDuration(600).withEndAction {
                container.removeView(star)
            }.start()
    }

    private fun animateLevelIn(container: ViewGroup) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.alpha = 0f
            child.translationY = 40f
            child.animate()
                .alpha(1f).translationY(0f)
                .setDuration(300).startDelay = (i * 60).toLong()
        }
    }
    private var lastToastTime = 0L
    private fun showToast(msg: String) {
        val now = System.currentTimeMillis()
        if (now - lastToastTime < 1500) return
        lastToastTime = now
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun checkAnswer(root: View) {
        val level = levels[currentIndex]
        val correct = level.correctAnswer
        val isCorrect = when (level.type) {
            // PATH: correctAnswer连续递增→顺序重要, 否则→集合(选所有)
            GameType.PATH -> {
                val isSequential = correct.zipWithNext().all { (a, b) -> b - a == 1 }
                if (isSequential) selectedIds == correct
                else selectedIds.sorted() == correct.sorted()
            }
            // CLASSIFY, TREASURE: 集合相同即可 (分类/找宝藏)
            GameType.TREASURE, GameType.CLASSIFY ->
                selectedIds.sorted() == correct.sorted()
            // SHAPE: 单选或多选都用集合比较
            GameType.SHAPE ->
                selectedIds.sorted() == correct.sorted()
            // 其他: 单选
            GameType.MAZE, GameType.SHOPPING, GameType.PATTERN ->
                selectedIds.firstOrNull() == correct.firstOrNull()
        }

        if (isCorrect) {
            score++
            val msgs = listOf("太棒了！🌟", "答对了！🎉", "厉害！👏", "真聪明！✨", "好样的！💪")
            showToast("✅ ${msgs.random()}")
            animateStarBurst(root)
            root.postDelayed({ goNext(root) }, 600)
        } else {
            val hint = level.hint
            val msg = if (hint.isNotEmpty()) "再想想哦 💡 $hint" else "再想想哦 💪"
            showToast(msg)
            animateShake(root.findViewById(R.id.gameItemContainer))
            selectedIds.clear()
            firstPick = null
            val container = root.findViewById<FlexboxLayout>(R.id.gameItemContainer)
            for (i in 0 until container.childCount) {
                container.getChildAt(i).setBackgroundColor(0xFFFFFFFF.toInt())
            }
        }
    }

    private fun goNext(root: View) {
        // 复用当前Fragment，只更新数据，不新增backstack
        currentIndex++
        selectedIds.clear()
        renderLevel(root)
    }

    private fun showResult(root: View) {
        root.findViewById<FlexboxLayout>(R.id.gameItemContainer).removeAllViews()
        root.findViewById<View>(R.id.gameConfirmBtn).visibility = View.GONE
        root.findViewById<View>(R.id.gameOptionRow).visibility = View.GONE
        root.findViewById<View>(R.id.resultBackBtn).visibility = View.VISIBLE
        root.findViewById<TextView>(R.id.gameQuestion).text = ""

        val type = levels.firstOrNull()?.type
        val icon = type?.icon ?: "🎮"
        val name = type?.label ?: "冒险"

        root.findViewById<TextView>(R.id.gameTitle).text = when {
            score >= levels.size -> "$icon 太厉害了！你完成了${name}的全部挑战！🏆"
            score >= levels.size / 2 -> "$icon ${name}挑战完成！${score}/${levels.size}关通过 👍"
            else -> "$icon ${name}冒险结束！${score}/${levels.size}关通过，再试一次吧 💪"
        }
        root.findViewById<TextView>(R.id.gameTypeLabel).text = "✨ 冒险完成"
        root.findViewById<TextView>(R.id.gameProgress).text = "⭐ 得分: $score/${levels.size}"

        root.findViewById<View>(R.id.resultBackBtn).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}

package com.pinyinkids

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import androidx.core.content.ContextCompat
import com.pinyinkids.game.GameMenuFragment

class CategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_category, container, false)
        val containerLayout = root.findViewById<LinearLayout>(R.id.categoryContainer)

        val categories = PinyinData.load(requireContext())

        for (cat in categories) {
            val card = inflater.inflate(R.layout.item_category_card, containerLayout, false)
            card.findViewById<TextView>(R.id.categoryIcon).text = cat.icon
            card.findViewById<TextView>(R.id.categoryName).text = cat.name
            card.findViewById<TextView>(R.id.categoryCount).text = "${cat.entries.size}个拼音"

            val bgColor = requireContext().getColor(cat.colorRes)
            card.findViewById<MaterialCardView>(R.id.categoryCard).setCardBackgroundColor(bgColor)

            card.setOnClickListener {
                (activity as? MainActivity)?.openCategory(cat)
            }

            containerLayout.addView(card)
        }

        // 思维小游戏入口
        val gameCard = inflater.inflate(R.layout.item_category_card, containerLayout, false)
        gameCard.findViewById<TextView>(R.id.categoryIcon).text = "🧩"
        gameCard.findViewById<TextView>(R.id.categoryName).text = "思维小游戏"
        gameCard.findViewById<TextView>(R.id.categoryCount).text = "找规律·分类·排序·空间"
        gameCard.findViewById<MaterialCardView>(R.id.categoryCard).setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.game_card_purple)
        )
        gameCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, GameMenuFragment.newInstance())
                .addToBackStack("game")
                .commit()
        }
        containerLayout.addView(gameCard)

        return root
    }
}

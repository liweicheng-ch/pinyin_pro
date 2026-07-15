package com.pinyinkids.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.pinyinkids.R

/** 游戏主菜单 - 选择游戏类型 */
class GameMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, b: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_game_menu, c, false)

        root.findViewById<View>(R.id.btnPattern).setOnClickListener {
            openGame(GameType.PATTERN)
        }
        root.findViewById<View>(R.id.btnClassify).setOnClickListener {
            openGame(GameType.CLASSIFY)
        }
        root.findViewById<View>(R.id.btnSorting).setOnClickListener {
            openGame(GameType.SORTING)
        }
        root.findViewById<View>(R.id.btnSpatial).setOnClickListener {
            openGame(GameType.SPATIAL)
        }
        root.findViewById<View>(R.id.backButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return root
    }

    private fun openGame(type: GameType) {
        val levels = GameData.randomLevels(type, 3)
        val frag = GamePlayFragment.newInstance(ArrayList(levels), 0)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, frag)
            .addToBackStack("game")
            .commit()
    }

    companion object {
        fun newInstance() = GameMenuFragment()
    }
}

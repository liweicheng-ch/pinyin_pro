package com.pinyinkids.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.pinyinkids.R

class GameMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, b: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_game_menu, c, false)

        root.findViewById<View>(R.id.btnPath).setOnClickListener { openGame(GameType.PATH) }
        root.findViewById<View>(R.id.btnClassify).setOnClickListener { openGame(GameType.CLASSIFY) }
        root.findViewById<View>(R.id.btnShape).setOnClickListener { openGame(GameType.SHAPE) }
        root.findViewById<View>(R.id.btnTreasure).setOnClickListener { openGame(GameType.TREASURE) }
        root.findViewById<View>(R.id.btnPattern).setOnClickListener { openGame(GameType.PATTERN) }
        root.findViewById<View>(R.id.btnMaze).setOnClickListener { openGame(GameType.MAZE) }
        root.findViewById<View>(R.id.btnShopping).setOnClickListener { openGame(GameType.SHOPPING) }

        root.findViewById<View>(R.id.backButton).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return root
    }

    private fun openGame(type: GameType) {
        val levels = GameData.levelsByType(type)
        if (levels.isEmpty()) return
        // 每次开新游戏都新建Fragment，不从已有栈复用
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

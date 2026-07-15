package com.pinyinkids

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class PinyinPlayerFragment : Fragment() {

    private var viewPager: ViewPager2? = null
    private var adapter: PinyinPageAdapter? = null
    private var entries: List<PinyinEntry> = emptyList()
    private var categoryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        entries = arguments?.getSerializable("entries") as? List<PinyinEntry> ?: emptyList()
        categoryName = arguments?.getString("categoryName") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_pinyin_player, container, false)

        // Back
        root.findViewById<TextView>(R.id.backButton).setOnClickListener {
            (activity as? MainActivity)?.showCategory()
        }

        // Title
        val label = when (categoryName) {
            "声母" -> "声母 - 初始音"
            "韵母" -> "韵母 - 韵尾音"
            "整体认读音节" -> "整体认读音节"
            else -> categoryName
        }
        root.findViewById<TextView>(R.id.categoryTitle).text = label

        // Setup ViewPager2
        viewPager = root.findViewById(R.id.viewPager)
        adapter = PinyinPageAdapter(
            entries = entries,
            context = requireContext()
        )
        viewPager?.adapter = adapter

        // Progress
        val progressText = root.findViewById<TextView>(R.id.progressText)
        val progressBar = root.findViewById<com.google.android.material.progressindicator.LinearProgressIndicator>(R.id.progressIndicator)

        fun updateProgress(pos: Int) {
            progressText.text = "${pos + 1} / ${entries.size}"
            progressBar.progress = ((pos + 1) * 100) / entries.size
        }

        updateProgress(0)
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateProgress(position)
                adapter?.play(position)
            }
        })

        // Auto-play first on start
        root.post { adapter?.play(0) }

        return root
    }

    override fun onDestroy() {
        adapter?.release()
        super.onDestroy()
    }

    companion object {
        fun newInstance(category: PinyinCategory): PinyinPlayerFragment {
            val f = PinyinPlayerFragment()
            val args = Bundle()
            args.putSerializable("entries", ArrayList(category.entries))
            args.putString("categoryName", category.name)
            f.arguments = args
            return f
        }
    }
}

package com.pinyinkids

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PinyinPageAdapter(
    private val entries: List<PinyinEntry>,
    private val context: Context
) : RecyclerView.Adapter<PinyinPageAdapter.PageViewHolder>() {

    private var currentPlayer: MediaPlayer? = null

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pinyinText: TextView = itemView.findViewById(R.id.pinyinText)
        val charText: TextView = itemView.findViewById(R.id.charText)
        val root: View = itemView

        init {
            root.setOnClickListener {
                play(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pinyin_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val entry = entries[position]
        holder.pinyinText.text = entry.pinyin
        holder.charText.text = "（${entry.char}）"
    }

    override fun getItemCount() = entries.size

    fun play(position: Int) {
        if (position < 0 || position >= entries.size) return
        val entry = entries[position]

        try {
            currentPlayer?.release()
            val afd = context.assets.openFd("mp3/${entry.mp3}")
            currentPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
                setOnCompletionListener { release() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        currentPlayer?.release()
        currentPlayer = null
    }
}

package com.pinyinkids

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PinyinPageAdapter(
    private val entries: List<PinyinEntry>,
    private val context: Context
) : RecyclerView.Adapter<PinyinPageAdapter.PageViewHolder>() {

    private var currentPlayer: MediaPlayer? = null
    private var lastPlayedPosition: Int = -1

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
        lastPlayedPosition = position

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
            Toast.makeText(context, "音频加载失败: ${entry.mp3}", Toast.LENGTH_SHORT).show()
        }
    }

    fun pause() {
        currentPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun resume(position: Int) {
        currentPlayer?.let {
            if (!it.isPlaying && lastPlayedPosition == position) {
                it.start()
            } else {
                // Player was released or switched page, replay
                play(position)
            }
        } ?: play(position)
    }

    fun release() {
        currentPlayer?.release()
        currentPlayer = null
    }
}

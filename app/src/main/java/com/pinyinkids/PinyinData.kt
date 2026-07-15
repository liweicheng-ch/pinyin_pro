package com.pinyinkids

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable

/**
 * One pinyin entry from index.json
 */
data class PinyinEntry(
    val pinyin: String,
    val char: String,
    val mp3: String,
    val type: String
) : Serializable

/**
 * Category definition
 */
data class PinyinCategory(
    val name: String,
    val icon: String,
    val colorRes: Int,
    val entries: List<PinyinEntry>
)

/**
 * Loads and parses index.json from assets, groups by type.
 */
object PinyinData {
    private var _categories: List<PinyinCategory>? = null

    fun load(ctx: android.content.Context): List<PinyinCategory> {
        _categories?.let { return it }

        val json = readAsset(ctx, "index.json")
        val arr = JSONArray(json)
        val raw = mutableListOf<PinyinEntry>()
        for (i in 0 until arr.length()) {
            val obj: JSONObject = arr.getJSONObject(i)
            raw.add(
                PinyinEntry(
                    pinyin = obj.getString("pinyin"),
                    char = obj.getString("char"),
                    mp3 = obj.getString("mp3"),
                    type = obj.getString("type")
                )
            )
        }

        val grouped = raw.groupBy { it.type }

        _categories = listOf(
            PinyinCategory("声母", "🔤", R.color.shengmu, grouped["声母"] ?: emptyList()),
            PinyinCategory("韵母", "🔊", R.color.yunmu, grouped["韵母"] ?: emptyList()),
            PinyinCategory("整体认读音节", "📖", R.color.zhengti, grouped["整体认读音节"] ?: emptyList())
        )
        return _categories!!
    }

    private fun readAsset(ctx: android.content.Context, name: String): String {
        val `is` = ctx.assets.open(name)
        val reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
        val sb = StringBuilder()
        var line: String? = reader.readLine()
        while (line != null) {
            sb.append(line).append('\n')
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
}

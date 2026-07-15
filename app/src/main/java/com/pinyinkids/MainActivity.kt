package com.pinyinkids

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            showCategory()
        }
    }

    fun showCategory() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CategoryFragment())
            .commit()
    }

    fun openCategory(category: PinyinCategory) {
        val frag = PinyinPlayerFragment.newInstance(category)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, frag)
            .addToBackStack("player")
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}

package com.alexius.storyvibe.view.storydetail

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import com.alexius.storyvibe.R
import com.alexius.storyvibe.data.remote.response.ListStoryItem
import com.alexius.storyvibe.databinding.ActivityStoryDetailBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(getColor(R.color.vivid_teal)))
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
    }

    private fun setupAction() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }

    private fun setupView() {

        val storyItem = intent.getParcelableExtra<ListStoryItem>(STORY_ITEM)
        Glide.with(this)
            .load(storyItem?.photoUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
            .into(binding.storyImageView)
        binding.titleTextView.text = storyItem?.name
        binding.contentTextView.text = storyItem?.description


//        binding.scrollView.setOnScrollChangeListener{_, _, scrollY, _, oldScrollY->
//            val navigationIcon = binding.toolbar.navigationIcon
//            if (navigationIcon != null) {
//                if (scrollY > oldScrollY) {
//                    navigationIcon.alpha = 0
//                } else if (scrollY < oldScrollY) {
//                    if (navigationIcon.alpha == 255) return@setOnScrollChangeListener
//                    navigationIcon.alpha = 255
//                    Log.d("scroll", "scrolling up")
//                }
//            }
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val STORY_ITEM = "story_item"
    }
}
package com.alexius.storyvibe.view.homepage

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexius.storyvibe.R
import com.alexius.storyvibe.data.Result
import com.alexius.storyvibe.data.remote.paging.LoadingStateAdapter
import com.alexius.storyvibe.data.remote.response.ListStoryItem
import com.alexius.storyvibe.databinding.ActivityHomeBinding
import com.alexius.storyvibe.databinding.ItemStoryBinding
import com.alexius.storyvibe.view.ViewModelFactory
import com.alexius.storyvibe.view.map.MapsActivity
import com.alexius.storyvibe.view.storydetail.StoryDetailActivity
import com.alexius.storyvibe.view.uploadstory.UploadStoryActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val storyAdapter = ListStoryAdapter()

    private lateinit var uploadStoryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(getColor(R.color.vivid_teal)))
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
    }

    private fun setupView() {

        setSupportActionBar(binding.toolbar)

        getStoriesByPaging()
    }

    private fun getStoriesByPaging() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
        viewModel.getAllStoriesByPager().observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

//    private fun getStories() {
//        viewModel.getAllStories().observe(this) { response ->
//            when (response) {
//                is Result.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
//                }
//                is Result.Success -> {
//                    binding.progressBar.visibility = View.GONE
//                    val storyData = response.data
//                    val listStory: List<ListStoryItem?>? = storyData.listStory
//                    if (listStory != null) {
////                        storyAdapter.submitList(listStory) {
////                            binding.recyclerView.scrollToPosition(0)
////                        }
//                    }
//                }
//                is Result.Error -> {
//                    binding.progressBar.visibility = View.GONE
//                    Toast.makeText(
//                        this,
//                        "Error Fetching: " + response.error,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }
//
//        binding.recyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            setHasFixedSize(true)
//            adapter = storyAdapter
//        }
//    }

    private fun setupAction() {

        storyAdapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem, view: ItemStoryBinding) {

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@HomeActivity,
                    Pair(view.imgPoster, "image"),
                    Pair(view.tvItemName, "title"),
                    Pair(view.tvItemDesc, "description")
                )

                val intent = Intent(this@HomeActivity, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.STORY_ITEM, data)
                startActivity(intent, optionsCompat.toBundle())
            }
        })

        uploadStoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isUploaded = result.data?.getBooleanExtra(UploadStoryActivity.IS_UPLOADED, false) ?: false
                if (isUploaded) {
                    getStoriesByPaging()
                    result.data?.putExtra(UploadStoryActivity.IS_UPLOADED, false) // Reset the value to false
                }
            }
        }

        binding.fab.setOnClickListener{
            val intent = Intent(this, UploadStoryActivity::class.java)
            uploadStoryLauncher.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                finish()
                viewModel.logout().observe(this) {
                    if (it) {
                        Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            R.id.action_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val STORY_ITEM = "story_item"
    }
}
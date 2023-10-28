package com.example.androidintermedieatesubmission

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.navigationsubmission.data.preferences.AuthPreferences
import com.bangkit.navigationsubmission.data.preferences.dataStore
import com.bangkit.navigationsubmission.ui.viewmodel.ViewModelMainFactory
import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import com.example.androidintermedieatesubmission.databinding.ActivityListStoriesBinding
import com.example.androidintermedieatesubmission.helper.AuthData
import com.example.androidintermedieatesubmission.ui.adapter.LoadingStateAdapter
import com.example.androidintermedieatesubmission.ui.adapter.StoriesAdapter
import com.example.androidintermedieatesubmission.ui.viewmodel.AuthViewModel
import com.example.androidintermedieatesubmission.ui.viewmodel.StoryViewModel
import com.example.androidintermedieatesubmission.ui.viewmodel.ViewModelFactoryStory


class ListStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoriesBinding
    private lateinit var animator: ValueAnimator

    private lateinit var storyViewModel: StoryViewModel

    private lateinit var adapter: StoriesAdapter

    private var token: AuthData? = null
    private var story: StoryResponse? = null

    companion object {
        const val TOKEN_INTENT_KEY =  "TOKENINTENTKEY"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.profile -> {
                val intentDetail = Intent(this, ProfileActivity::class.java)
                intentDetail.putExtra(ProfileActivity.PROFILE_KEY, AuthData(nama = token?.nama, userId = token?.userId, token = token?.token))
                startActivity(intentDetail)
            }
            R.id.btn_map_activity -> {
                val intentDetail = Intent(this, MapsActivity::class.java)
                intentDetail.putExtra(MapsActivity.MAPS_ACTIVITY_INTENT_KEY, AuthData(nama = token?.nama, userId = token?.userId, token = token?.token))
                startActivity(intentDetail)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                adapter.refresh()
                adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        if (positionStart == 0) {
                            binding.rvStories.scrollToPosition(0)
                        }
                    }
                })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionbar = supportActionBar

        actionbar!!.title = getString(R.string.app_name)

        adapter = StoriesAdapter()

        adapter.withLoadStateFooter(footer = LoadingStateAdapter{
            adapter.retry()
        })

        binding.rvStories.adapter = adapter


        token = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(TOKEN_INTENT_KEY, AuthData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(TOKEN_INTENT_KEY) as AuthData
        }


        storyViewModel = ViewModelProvider(this, ViewModelFactoryStory(this, token?.token))[StoryViewModel::class.java]

        getData()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        binding.btnAddStories.setOnClickListener{
            val intentDetail = Intent(this, AddStoryActivity::class.java)
            intentDetail.putExtra(AddStoryActivity.ADD_STORY_KEY, AuthData(nama = token?.nama, userId = token?.userId, token = token?.token))
            getResult.launch(intentDetail)
        }

        binding.srlRefresh.setOnRefreshListener {
            binding.srlRefresh.isRefreshing = true;
            adapter.refresh()
            Toast.makeText(this, "List stories refreshed", Toast.LENGTH_SHORT).show()

        }

    }

    private fun getData(){
        storyViewModel.getPaging.observe(this){
            binding.srlRefresh.isRefreshing = false;
            if (it != null){
                setStory(it)

            } else {
//                AuthHelper.logOut(this, tokenViewModel)
            }
        }
    }

    private fun setStory(story: PagingData<StoryResponseItem>){

        adapter.submitData(lifecycle,story)

        adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryResponseItem) {

            }
        })
    }
}
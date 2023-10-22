package com.example.androidintermedieatesubmission.ui.adapter

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.androidintermedieatesubmission.DetailStoryActivity
import com.example.androidintermedieatesubmission.R
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem
import com.example.androidintermedieatesubmission.databinding.ItemRowStoryBinding
import com.example.androidintermedieatesubmission.helper.DetailData

class StoriesAdapter: PagingDataAdapter<StoryResponseItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var binding: ItemRowStoryBinding
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryResponseItem>() {
            override fun areItemsTheSame(oldItem: StoryResponseItem, newItem: StoryResponseItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryResponseItem, newItem: StoryResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryResponseItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class MyViewHolder(private val binding: ItemRowStoryBinding, private val gradient: GradientDrawable, private val animator: ValueAnimator) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryResponseItem){
            binding.tvUsername.text = story.name
            binding.tvDescription.text = story.description

            Glide.with(binding.root)
                .load(story.photoUrl)
                .placeholder(gradient)
                .listener(
                    object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ) : Boolean {
                            animator.end()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            animator.end()
                            return false
                        }

                    }
                )
                .into(binding.imgUser)


            binding.itemView.setOnClickListener{
                val intentDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentDetail.putExtra(DetailStoryActivity.DETAIL_INTENT_KEY, DetailData(nama = story.name!!, image = story.photoUrl!!, description = story.description!!, time = story.createdAt!! ))
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.imgUser, "imageStory"),
                        Pair(binding.tvUsername, "name"),
                        Pair(binding.tvDescription, "description"),
                    )
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
//            onItemClickCallback.onItemClicked()
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        val from = ContextCompat.getColor(binding.root.context, R.color.gray_200)
        val to = ContextCompat.getColor(binding.root.context, R.color.gray)

        binding.imgUser.setImageDrawable(
            GradientDrawable( GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    to, from
                ),
            ))

        val gradient = binding.imgUser.drawable as GradientDrawable

        val evaluator = ArgbEvaluator()
        val animator = TimeAnimator.ofFloat(0.0f, 1.0f)

        animator.duration = 1500
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener {
            val fraction = it.animatedFraction
            val newStart = evaluator.evaluate(fraction, from, to) as Int
            val newEnd = evaluator.evaluate(fraction, to, from) as Int

            gradient.colors = intArrayOf(newStart, newEnd)
        }

        animator.start()

        return MyViewHolder(binding, gradient, animator)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)

        if (story != null) {
            holder.bind(story)
        }

    }



}

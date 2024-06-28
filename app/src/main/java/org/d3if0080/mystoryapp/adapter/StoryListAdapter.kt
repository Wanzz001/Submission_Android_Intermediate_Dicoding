package org.d3if0080.mystoryapp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.d3if0080.mystoryapp.api.response.home.ListStory
import org.d3if0080.mystoryapp.databinding.ItemListBinding

class StoryListAdapter(private val context: Context): RecyclerView.Adapter<StoryListAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private val listStory = ArrayList<ListStory>()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(data: List<ListStory>) {
        val diffCallback = DiffUtilCallback(listStory, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listStory.clear()
        listStory.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fav = listStory[position]
        holder.bind(fav, onItemClickCallback)
    }

    override fun getItemCount(): Int {
        return listStory.size
    }

    class ViewHolder(private val binding: ItemListBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listStory: ListStory, clickListener: OnItemClickCallback) {
            with(binding) {
                tvTitle.text = listStory.name
                tvDesc.text = listStory.description
                Glide.with(context)
                    .load(listStory.photoUrl)
                    .into(ivImgStory)
                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        Pair(ivImgStory, "image"),
                        Pair(tvTitle, "title"),
                        Pair(tvDesc, "description")
                    )
                    clickListener.onItemClicked(listStory, optionsCompat)
                }
            }
        }
    }

    fun interface OnItemClickCallback {
        fun onItemClicked(selected: ListStory, options: ActivityOptionsCompat)
    }

    class DiffUtilCallback(
        private val oldList: List<ListStory>,
        private val newList: List<ListStory>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem == newItem
        }

        @Override
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}

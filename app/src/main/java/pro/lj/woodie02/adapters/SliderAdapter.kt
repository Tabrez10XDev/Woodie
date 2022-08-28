package pro.lj.woodie02.adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import pro.lj.woodie02.R
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.SlideItemContainerBinding

class SliderAdapter : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {



    inner class SliderViewHolder(val binding: SlideItemContainerBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Tree>(){
        override fun areItemsTheSame(oldItem: Tree, newItem: Tree): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Tree, newItem: Tree): Boolean {
            return oldItem.name == newItem.name
        }

    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = SlideItemContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun getItemCount(): Int {
    return 3
    //   return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val item = differ.currentList[0]
        with(holder){
            Glide
                .with(itemView.context)
                .load(item.imageUri?.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageSlider)
        }
        

    }


}
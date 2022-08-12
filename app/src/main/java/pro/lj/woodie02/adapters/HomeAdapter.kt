package pro.lj.woodie02.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.ItemPreviewBinding

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ItemViewHolder>() {



    private var onItemClickListener : ((Tree) -> Unit) ?= null

    fun setOnItemClickListener(listener: (Tree) -> Unit){
        onItemClickListener = listener
    }

    inner class ItemViewHolder(val binding: ItemPreviewBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Tree>(){
        override fun areItemsTheSame(oldItem: Tree, newItem: Tree): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Tree, newItem: Tree): Boolean {
            return oldItem.name == newItem.name
        }

    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
//        return 2
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = differ.currentList[position]
        //val storageRef = FirebaseStorage.getInstance()
        with(holder){
//            var visibleView: View = binding.card
//            var inVisibleView: View = binding.txt






            itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
            binding.apply {

                tvName.text = item.name
                tvScientificName.text = item.scientificName
                Glide
                        .with(itemView.context)
                        .load(item.imageUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivProduct)
            }

        }

    }

}
package vu.pham.runningappseminar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pl.droidsonroids.gif.GifImageView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.Music

class RecyclerViewMusicAdapter(private val clickMusic: ClickMusic) : RecyclerView.Adapter<RecyclerViewMusicAdapter.MusicHolder>() {

    interface ClickMusic{
        fun clickItem(music: Music)
    }
    private val differCallBack = object : DiffUtil.ItemCallback<Music>(){
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, differCallBack)
    fun submitList(list: List<Music>) = differ.submitList(list)

    inner class MusicHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtName: TextView = itemView.findViewById(R.id.textViewNameMusicItem)
        val txtAuthor: TextView = itemView.findViewById(R.id.textViewAuthorMusicItem)
        val imgGif: GifImageView = itemView.findViewById(R.id.imageViewMusicGif)
        val layout: RelativeLayout = itemView.findViewById(R.id.relativeLayoutMusic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicHolder(LayoutInflater.from(parent.context).inflate(R.layout.music_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        val music = differ.currentList[position]
        if(music.isPlaying){
            holder.imgGif.visibility = View.VISIBLE
        }else{
            holder.imgGif.visibility = View.INVISIBLE
        }
        holder.txtName.text = music.name
        holder.txtAuthor.text = music.author
        holder.layout.setOnClickListener {
            clickMusic.clickItem(music)
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }
}
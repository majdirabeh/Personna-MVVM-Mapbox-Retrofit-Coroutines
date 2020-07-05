package fr.dev.majdi.personnacoroutines.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import fr.dev.majdi.personna.model.Result
import fr.dev.majdi.personnacoroutines.R

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
class PersonnaAdapter(private val personnes: List<Result>) :
    RecyclerView.Adapter<PersonnaAdapter.ViewHolder>() {

    private var onItemClickListener: ItemClickListener? = null
    private var personnaList: List<Result> = personnes


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return personnaList.size
    }

    fun setItems(personnes: List<Result>) {
        this.personnaList = personnes
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = personnaList[position]

        holder.name.text = user.name.toString()
        holder.email.text = user.email

        Glide.with(holder.imageProfile.context)
            .load(user.picture.medium)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.photo_profile)
            .error(R.drawable.photo_profile)
            .into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(holder.itemView, position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.name)
        val email = itemView.findViewById<TextView>(R.id.email)
        val imageProfile = itemView.findViewById<ImageView>(R.id.image_profile);

        init {
            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(itemView, 0)
            }
        }
    }

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }


}
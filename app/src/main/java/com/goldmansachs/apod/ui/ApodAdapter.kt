package com.goldmansachs.apod.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goldmansachs.apod.R
import com.goldmansachs.apod.data.model.ApodEntity
import com.goldmansachs.apod.databinding.RowApodBinding

class ApodAdapter(private var actionEvents: ApodAdapter.ActionEvents, private var context: Context,
                  private var apodList: ArrayList<ApodEntity>?, private var isFavourite: Boolean) : RecyclerView.Adapter<ApodAdapter.ApodViewHolder>() {
    companion object {
        private val TAG = ApodAdapter::class.java.name
    }

    val favMap: HashMap<String, Boolean> = HashMap()// <Date, Fav Status>

    fun setApodList(apodList: ArrayList<ApodEntity>?) {
        this.apodList = apodList
    }

    fun getApodList() = apodList

    fun setIsFavourite(isFavourite: Boolean) {
        this.isFavourite = isFavourite
    }

    fun isFavourite() = isFavourite

    fun onFavouriteStatus(favStatus: Pair<Boolean, String>) {
        Log.d(TAG, "onFavouriteStatus() == favStatus: $favStatus")
        favMap[favStatus.second]?.let { status ->
            if (favStatus.first) {
                favMap.remove(favStatus.second)
            } else {
                favMap[favStatus.second] = !status
            }
        }
    }

    class ApodViewHolder(private val favMap: HashMap<String, Boolean>, private var actionEvents: ApodAdapter.ActionEvents, private var context: Context, private val rowApodBinding: RowApodBinding) : RecyclerView.ViewHolder (rowApodBinding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(apodEntity: ApodEntity?, isLastRow: Boolean) {
            Log.d(TAG, "bind() == apodEntity: $apodEntity, isLastRow: $isLastRow")
            rowApodBinding.mediaThumb.layoutParams.height = context.resources.displayMetrics.widthPixels.div(4).times(3)
            rowApodBinding.mediaThumb.scaleType = ImageView.ScaleType.FIT_XY
            rowApodBinding.mediaPlay.visibility = if (apodEntity?.media_type == "video") View.VISIBLE else View.GONE

            apodEntity?.date?.let { date -> favMap[date]}?.let { isFav ->
                rowApodBinding.apodFav.setImageDrawable(context.getDrawable(if (isFav) R.drawable.fav_on else  R.drawable.fav_off))
            } ?: run {
                rowApodBinding.apodFav.setImageDrawable(context.getDrawable(if (apodEntity?.is_favourite == 1) R.drawable.fav_on else  R.drawable.fav_off))
            }

            rowApodBinding.dateTextView.text = apodEntity?.date
            rowApodBinding.titleTextView.text = apodEntity?.title
            rowApodBinding.explanationTextView.text = apodEntity?.explanation

            Glide.with(context)
                .load(if (apodEntity?.media_type == "video") apodEntity.thumbnail_url else apodEntity?.url)
                .centerCrop()
                .placeholder(R.drawable.nasa)
                .into(rowApodBinding.mediaThumb)

            rowApodBinding.apodFav.setOnClickListener { view ->
                apodEntity?.let {
                    if (apodEntity.is_favourite == 1) { apodEntity.is_favourite = 0 } else { apodEntity.is_favourite = 1 }
                    rowApodBinding.apodFav.setImageDrawable(context.getDrawable(if (apodEntity.is_favourite == 1) R.drawable.fav_on else  R.drawable.fav_off))

                    apodEntity.date?.let { date ->
                        favMap[date] = apodEntity.is_favourite == 1
                        actionEvents.onFavClick(status = apodEntity.is_favourite == 1, date = date)
                    }
                }
            }

            rowApodBinding.card.setOnClickListener { view ->
                apodEntity?.date?.let { date -> actionEvents.onItemClick(date = date) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApodViewHolder {
        Log.d(TAG, "onCreateViewHolder()")
        val binding = RowApodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApodViewHolder(favMap = favMap, actionEvents = actionEvents, context = context, rowApodBinding = binding)
    }

    override fun onBindViewHolder(holder: ApodViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder()")
        holder.bind(apodList?.get(position), itemCount - 1 == position)
    }

    override fun getItemCount() = apodList?.size ?: 0

    interface ActionEvents {
        fun onFavClick(status: Boolean, date:String)

        fun onItemClick(date:String)
    }
}
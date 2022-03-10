package com.goldmansachs.apod.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldmansachs.apod.R
import com.goldmansachs.apod.data.api.ApodService
import com.goldmansachs.apod.data.api.ServiceProvider
import com.goldmansachs.apod.data.model.ApodEntity
import com.goldmansachs.apod.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.name
    }

    private lateinit var binding: ActivityMainBinding
    private var viewModel: MainViewModel? = null
    private var latestApodEntity: ApodEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBindings()
        initViewModels()
        initObservers()

        val date = intent.getStringExtra("date")
        Log.d(TAG, "onCreate() == date: $date")

        date?.let {
            binding.search.visibility = View.GONE
            viewModel?.getApodByDateFromDB(date = it)
        } ?: run {
            binding.search.visibility = View.VISIBLE
            viewModel?.getLatestApodFromDB()
        }
    }

    private fun initBindings() {
        Log.d(TAG, "initBindings()")
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = title
        binding.appBar.layoutParams.height = (resources.displayMetrics.heightPixels / 2)
        binding.search.setOnClickListener { view ->
            DatePickerFragment(this).show(supportFragmentManager, "DatePicker")
        }

        binding.fab.setOnClickListener { view ->
            if (binding.search.visibility == View.VISIBLE) {
                latestApodEntity?.let { apodEntity ->
                    apodEntity.date?.let { date ->
                        viewModel?.updateApodFavStatusIntoDB(isFavourite = apodEntity.is_favourite != 1, date = date)
                    }
                }
            }
        }

        binding.mediaThumb.setOnClickListener { view ->
            onApodMediaClick()
        }

        binding.mediaPlay.setOnClickListener { view ->
            onApodMediaClick()
        }
    }

    private fun initViewModels() {
        Log.d(TAG, "initViewModels()")
        if (null == viewModel) {
            viewModel = ViewModelProvider(this, ViewModelFactory(application = application,
                apodService = ServiceProvider.createService(ApodService::class.java)))[MainViewModel::class.java]
        }
    }

    private fun initObservers() {
        Log.d(TAG, "initObservers()")
        viewModel?.apodResponse?.observe(this, this::onApodResponse)
        viewModel?.saveStatus?.observe(this, this::onSaveApod)
        viewModel?.favStatus?.observe(this, this::onFavouriteApod)
        viewModel?.latestApod?.observe(this, this::onLatestApod)
        viewModel?.dateApod?.observe(this, this::onApodByDate)
    }

    private fun onApodMediaClick() {
        latestApodEntity?.let { apodEntity ->
            val url = if (apodEntity.media_type == "video") apodEntity.url else apodEntity.hdurl
            if (null != url && url.isNotEmpty()) {
                val intent = Intent(this, MediaActivity::class.java)
                intent.putExtra("Media_Type", apodEntity.media_type)
                intent.putExtra("Media_Url", url)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to open. Empty media url!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onApodResponse(apodResponse: Pair<Boolean, ApodEntity?>) {
        Log.d(TAG, "onApodResponse() == onApodResponse: $apodResponse")
        binding.contentLoader.visibility = View.GONE
        updateWithLatestApod(apodData = apodResponse.second, isNwResponse = true)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateWithLatestApod(apodData: ApodEntity?, isNwResponse: Boolean) {
        apodData?.let { apodEntity ->
            latestApodEntity = apodEntity
            viewModel?.saveApodIntoDB(apodEntity = apodEntity)

            binding.fab.visibility = View.VISIBLE

            binding.fab.setImageDrawable(getDrawable(if (latestApodEntity?.is_favourite == 1) R.drawable.fav_on else  R.drawable.fav_off))
            binding.mediaPlay.visibility = if (apodEntity.media_type == "video") View.VISIBLE else View.GONE

            binding.content.emptyApodText.visibility = View.GONE
            binding.content.titleHeaderView.visibility = View.VISIBLE
            binding.content.titleTextView.visibility = View.VISIBLE
            binding.content.explanationHeaderView.visibility = View.VISIBLE
            binding.content.explanationTextView.visibility = View.VISIBLE

            binding.content.dateTextView.text = apodEntity.date
            binding.content.titleTextView.text = apodEntity.title
            binding.content.explanationTextView.text = apodEntity.explanation

            Glide.with(this)
                .load(if (apodEntity.media_type == "video") apodEntity.thumbnail_url else apodEntity.url)
                .centerCrop()
                .placeholder(R.drawable.nasa)
                .into(binding.appBar.media_thumb)
        } ?: run {
            if (isNwResponse) {
                Toast.makeText(this, "Failed to get the New APOD information for date selected. Please try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSaveApod(isSuccess: Boolean) {
        Log.d(TAG, "onSaveApod() == isSuccess: $isSuccess")
        if (!isSuccess) Toast.makeText(this, "Failed to cache the APOD information!", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onFavouriteApod(isFavourite: Boolean) {
        Log.d(TAG, "onFavouriteApod() == isSuccess: $isFavourite")
        if (isFavourite) {
            latestApodEntity?.apply {
                is_favourite = if (is_favourite == 0) 1 else 0
            }
            binding.fab.setImageDrawable(getDrawable(if (latestApodEntity?.is_favourite == 1) R.drawable.fav_on else  R.drawable.fav_off))
        }
    }

    private fun onLatestApod(apodEntity: ApodEntity?) {
        Log.d(TAG, "onLatestApod() == apodEntity: $apodEntity")
        updateWithLatestApod(apodData = apodEntity, isNwResponse = false)
    }

    private fun onApodByDate(apodEntity: ApodEntity?) {
        Log.d(TAG, "onLatestApod() == apodEntity: $apodEntity")
        updateWithLatestApod(apodData = apodEntity, isNwResponse = false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu()")
        if (binding.search.visibility == View.VISIBLE) {
            menuInflater.inflate(R.menu.menu_scrolling, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onCreateOptionsMenu() == item: ${item.itemId}")
        return when (item.itemId) {
            R.id.action_favourites -> {
                if (null != latestApodEntity) {
                    startActivity(Intent(this, ApodActivity::class.java))
                } else {
                    Toast.makeText(this, "No APOD available yet! You can add one by hitting Search Button.", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        Log.d(TAG, "onDateSet() == Date: ${year}-${month}-${day}")
        binding.contentLoader.visibility = View.VISIBLE
        viewModel?.getApodByDateFromNW("${year}-${month}-${day}")
    }

    class DatePickerFragment(private val activity: MainActivity) : DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            return DatePickerDialog(activity, this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            activity.onDateSet(year = year, month = month, day = day)
        }
    }
}
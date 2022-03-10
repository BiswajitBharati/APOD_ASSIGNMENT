package com.goldmansachs.apod.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmansachs.apod.R
import com.goldmansachs.apod.data.api.ApodService
import com.goldmansachs.apod.data.api.ServiceProvider
import com.goldmansachs.apod.data.model.ApodEntity
import com.goldmansachs.apod.databinding.ActivityApodBinding

class ApodActivity : AppCompatActivity(), ApodAdapter.ActionEvents {
    companion object {
        private val TAG = ApodActivity::class.java.name
    }

    private lateinit var binding: ActivityApodBinding
    private var viewModel: ApodViewModel? = null
    private var apodEntityList: List<ApodEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivityApodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBindings()
        initViewModels()
        initObservers()

        viewModel?.getAllApodFromDB()
    }

    private fun initBindings() {
        Log.d(TAG, "initBindings()")
        val linearLayoutManager = LinearLayoutManager(this)
        binding.apodList.run {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
        }
    }

    private fun initViewModels() {
        Log.d(TAG, "initViewModels()")
        if (null == viewModel) {
            viewModel = ViewModelProvider(this, ViewModelFactory(application = application,
                apodService = ServiceProvider.createService(ApodService::class.java)))[ApodViewModel::class.java]
        }
    }

    private fun initObservers() {
        Log.d(TAG, "initObservers()")
        viewModel?.favStatus?.observe(this, this::onFavouriteStatus)
        viewModel?.allApod?.observe(this, this::onAllApod)
        viewModel?.favouriteApod?.observe(this, this::onFavouriteApod)
    }

    private fun onFavouriteStatus(favStatus: Pair<Boolean, String>) {
        Log.d(TAG, "onFavouriteStatus() == favStatus: $favStatus")
        binding.apodList.adapter?.let { apodAdapter ->
            (apodAdapter as ApodAdapter).onFavouriteStatus(favStatus)
            refreshApodList(apodAdapter.isFavourite())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onAllApod(apodEntityList: List<ApodEntity>?) {
        Log.d(TAG, "onAllApod()")
        this.apodEntityList = apodEntityList
        refreshApodList(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshApodList(isFavourite: Boolean) {
        Log.d(TAG, "refreshApodList() == isFavourite: $isFavourite")
        (if (isFavourite) apodEntityList?.filter { it.is_favourite == 1 } else apodEntityList)?.let { apodList ->
            if (apodList.isEmpty()) {
                Toast.makeText(this, "No ${if (isFavourite) "favourite " else ""}APOD available yet!", Toast.LENGTH_SHORT).show()
            }
            binding.apodList.adapter?.let { apodAdapter ->
                (apodAdapter as ApodAdapter).setIsFavourite(isFavourite = isFavourite)
                (apodAdapter as ApodAdapter).setApodList(apodList = ArrayList(apodList))
                apodAdapter.notifyDataSetChanged()
            } ?: run {
                with(binding.apodList) {
                    adapter = ApodAdapter(actionEvents = this@ApodActivity, context = this@ApodActivity, apodList = ArrayList(apodList), isFavourite = isFavourite)
                }
            }
        } ?: run {
            Toast.makeText(this, "No ${if (isFavourite) "favourite " else ""}APOD available yet!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onFavouriteApod(apodEntityList: List<ApodEntity>?) {
        Log.d(TAG, "onFavouriteApod()")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu()")
        menuInflater.inflate(R.menu.menu_apod, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onCreateOptionsMenu() == item: ${item.itemId}")
        return when (item.itemId) {
            R.id.action_apod -> {
                if (item.title == getString(R.string.action_all)) {
                    item.title = getString(R.string.action_fav)
                    refreshApodList(false)
                } else {
                    item.title = getString(R.string.action_all)
                    refreshApodList(true)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onFavClick(status: Boolean, date: String) {
        Log.d(TAG, "onFavClick() == status: $status date: $date")
        viewModel?.updateApodFavStatusIntoDB(isFavourite = status, date = date)
    }

    override fun onItemClick(date: String) {
        Log.d(TAG, "onItemClick() == date: $date")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("date", date)
        startActivity(intent)
    }
}
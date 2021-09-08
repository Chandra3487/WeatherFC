package com.anirudroidz.currentweatherforcast.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anirudroidz.currentweatherforcast.databinding.MainFragmentBinding
import com.anirudroidz.currentweatherforcast.ui.main.data.model.CurrentWeather
import java.io.IOException
import java.util.*


class MainFragment : Fragment(), LocationListener {

    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel
    private val currentWeatherLiveData = MutableLiveData<CurrentWeather>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(currentWeatherLiveData) as T
            }
        }).get(MainViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        Thread {
            Looper.prepare()
            context?.let { context ->
                getCityName(context)?.let {
                    viewModel.setCityName(it)
                }
            }
        }.start()
    }

    private fun getCityName(context: Context): String? {
        val locationManager = getSystemService(context, LocationManager::class.java)

        val criteria = Criteria()
        val provider = locationManager?.getBestProvider(criteria, false)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        provider?.let {
            locationManager.requestLocationUpdates(it, 400, 1.0F, this)
            val location: Location? = locationManager.getLastKnownLocation(it)
            location?.let {
                val coder = Geocoder(context, Locale.ENGLISH)
                var results: List<Address?>? = null
                try {
                    results =
                        coder.getFromLocation(location.latitude, location.longitude, 1)
                } catch (e: IOException) {
                    // nothing
                }
                return results?.firstOrNull()?.locality
            } ?: return null

        } ?: return null
    }

    override fun onLocationChanged(location: Location) {
        val coder = Geocoder(context, Locale.ENGLISH)
        var results: List<Address?>? = null
        try {
            results =
                coder.getFromLocation(location.latitude, location.longitude, 1)
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }
        Thread {
            viewModel.setCityName(results?.firstOrNull()?.locality)
        }.start()
    }

    companion object {
        fun newInstance() = MainFragment()
        private const val TAG = "MainFragment"
    }
}

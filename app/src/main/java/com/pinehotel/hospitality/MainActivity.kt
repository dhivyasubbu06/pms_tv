package com.pinehotel.hospitality

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pinehotel.hospitality.activities.*
import com.pinehotel.hospitality.adapter.MenuCardAdapter
import com.pinehotel.hospitality.databinding.ActivityMainBinding
import com.pinehotel.hospitality.model.MenuCardItem
import com.pinehotel.hospitality.model.MenuDestination
import com.pinehotel.hospitality.network.GuestInfo
import com.pinehotel.hospitality.ui.*
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import com.pinehotel.hospitality.utils.PreferenceManager
import com.pinehotel.hospitality.utils.FocusUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    
    private val viewModel: MainViewModel by viewModels()
    private val servicesViewModel: ServicesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        // Block the back button on home screen
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing: Home screen is the root
            }
        })

        initUi()
        checkSetup()
        startClock()
    }

    private fun checkSetup() {
        lifecycleScope.launch {
            preferenceManager.roomNumberFlow.collectLatest { roomNo ->
                if (roomNo.isNullOrBlank()) {
                    startActivity(Intent(this@MainActivity, SetupActivity::class.java))
                    finish()
                } else {
                    viewModel.fetchGuestInfo()
                    viewModel.fetchMyOrders()
                }
            }
        }
    }

    private fun initUi() {
        setupMenuGrid()
        setupBottomActions()
        observeViewModel()
        
        viewModel.startOrderTracking()
        servicesViewModel.fetchServices()
    }

    private fun startClock() {
        lifecycleScope.launch {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
            while (true) {
                val now = Calendar.getInstance().time
                binding.tvTime.text = timeFormat.format(now)
                binding.tvDate.text = dateFormat.format(now)
                delay(1000)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchGuestInfo()
        viewModel.fetchMyOrders()
    }

    private fun observeViewModel() {
        // Observe Guest Information
        lifecycleScope.launch {
            viewModel.guestInfo.collectLatest { info ->
                info?.let { bindGuestContext(it) }
            }
        }

        // Observe My Orders
        lifecycleScope.launch {
            viewModel.myOrders.collectLatest { 
                // Count removed as per UI request
            }
        }

        // Observe errors and notify user
        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindGuestContext(info: GuestInfo) {
        val welcomePrefix = "Welcome, "
        val name = info.guestName
        val spannableWelcome = SpannableString("$welcomePrefix$name")
        val goldColor = resources.getColor(R.color.gold_accent, null)
        val whiteColor = Color.WHITE
        
        // "Welcome, " in white
        spannableWelcome.setSpan(
            ForegroundColorSpan(whiteColor),
            0,
            welcomePrefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        // Name in gold and italic
        spannableWelcome.setSpan(
            ForegroundColorSpan(goldColor),
            welcomePrefix.length,
            spannableWelcome.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableWelcome.setSpan(
            StyleSpan(Typeface.ITALIC),
            welcomePrefix.length,
            spannableWelcome.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvWelcomeGuest.text = spannableWelcome
        
        binding.tvRoomNumber.text = "Room ${info.roomNo}"
        
        val roomStr = info.roomNo.toString()
        val footerText = "Guest: ${info.guestName}   |   Room: $roomStr"
        val spannableFooter = SpannableString(footerText)
        
        // Style guest name in gold
        val guestIndex = footerText.indexOf(info.guestName)
        if (guestIndex != -1) {
            spannableFooter.setSpan(
                ForegroundColorSpan(goldColor),
                guestIndex,
                guestIndex + info.guestName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        // Style room number in gold
        val roomIndex = footerText.indexOf(roomStr)
        if (roomIndex != -1) {
            spannableFooter.setSpan(
                ForegroundColorSpan(goldColor),
                roomIndex,
                roomIndex + roomStr.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        
        binding.footerGuestInfo.text = spannableFooter
    }

    private fun setupMenuGrid() {
        val menuItems = listOf(
            MenuCardItem(MenuDestination.LIVE_TV, "LIVE TV", "Watch Live Channels", R.drawable.ic_live_tv),
            MenuCardItem(MenuDestination.MOVIES, "MOVIES", "Watch Movies & Shows", R.drawable.ic_movies),
            MenuCardItem(MenuDestination.ROOM_SERVICE, "SERVICES", "Room Service & Housekeeping", R.drawable.ic_room_service),
            MenuCardItem(MenuDestination.HOTEL_INFO, "HOTEL INFORMATION", "About Our Hotel", R.drawable.ic_hotel_info),
            MenuCardItem(MenuDestination.WIFI, "WIFI", "Connect to Internet", R.drawable.ic_wifi),
            MenuCardItem(MenuDestination.GALLERY, "GALLERY", "Explore Hotel Photos", R.drawable.ic_gallery),
            MenuCardItem(MenuDestination.MESSAGES, "MESSAGES", "Messages from Hotel", R.drawable.ic_messages),
            MenuCardItem(MenuDestination.SETTINGS, "SETTINGS", "System Settings", R.drawable.ic_settings)
        )

        val adapter = MenuCardAdapter(
            items = menuItems,
            onCardClicked = { item -> navigateTo(item.id) },
            onCardLongClicked = { item ->
                if (item.id == MenuDestination.SETTINGS) {
                    startActivity(Intent(this, SetupActivity::class.java))
                }
            }
        )

        // 8 items grid: 4 columns, 2 rows
        val gridLayoutManager = GridLayoutManager(this, 4)

        binding.menuGridRecyclerView.apply {
            layoutManager = gridLayoutManager
            this.adapter = adapter
            setHasFixedSize(true)
        }

        binding.menuGridRecyclerView.post {
            val firstItem = binding.menuGridRecyclerView.findViewHolderForAdapterPosition(0)?.itemView
            firstItem?.requestFocus() ?: binding.menuGridRecyclerView.requestFocus()
        }
    }

    private fun setupBottomActions() {
        binding.btnLiveTv.apply {
            setOnClickListener { navigateTo(MenuDestination.LIVE_TV) }
            setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }
        binding.btnMyBookings.apply {
            setOnClickListener { navigateTo(MenuDestination.BOOKINGS) }
            setOnFocusChangeListener { view, hasFocus ->
                FocusUtils.applyScaleAnimation(view, hasFocus)
            }
        }
    }

    private fun navigateTo(destination: MenuDestination) {
        val activityClass = when (destination) {
            MenuDestination.LIVE_TV -> LiveTvActivity::class.java
            MenuDestination.MOVIES -> MoviesActivity::class.java
            MenuDestination.ROOM_SERVICE -> ServicesActivity::class.java
            MenuDestination.HOUSEKEEPING -> ServicesActivity::class.java
            MenuDestination.HOTEL_INFO -> HotelInfoActivity::class.java
            MenuDestination.WIFI -> WifiActivity::class.java
            MenuDestination.GALLERY -> GalleryActivity::class.java
            MenuDestination.MESSAGES -> MessagesActivity::class.java
            MenuDestination.SETTINGS -> SettingsActivity::class.java
            MenuDestination.BOOKINGS -> BookingActivity::class.java
            else -> LiveTvActivity::class.java
        }
        startActivity(Intent(this, activityClass))
    }
}

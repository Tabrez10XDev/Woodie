package pro.lj.woodie02.ui.app

import android.Manifest
import android.R.attr.radius
import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem
import pro.lj.woodie02.R
import pro.lj.woodie02.databinding.ActivityMainBinding
import pro.lj.woodie02.repositories.MainRepository
import pro.lj.woodie02.viewmodels.MainViewModel
import pro.lj.woodie02.viewmodels.MainViewModelProviderFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var layout: View
    lateinit var viewModel: MainViewModel

    var locationPromt = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MainRepository()
        val viewModelProviderFactory = MainViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setTheme(R.style.Theme_Woodie02)
        setContentView(view)

        val menuItems = arrayOf(
            CbnMenuItem(
                R.drawable.trial_location, // the icon
                R.drawable.trial_location_avd, // the AVD that will be shown in FAB
                R.id.searchFragment // optional if you use Jetpack Navigation
            ),
            CbnMenuItem(
                R.drawable.trial_home, // the icon
                R.drawable.trial_home_avd, // the AVD that will be shown in FAB
                R.id.home // optional if you use Jetpack Navigation
            ),
            CbnMenuItem(
                R.drawable.trial_home, // the icon
                R.drawable.trial_home_avd, // the AVD that will be shown in FAB
                R.id.qrFragment // optional if you use Jetpack Navigation
            ),
            CbnMenuItem(
                R.drawable.trial_home, // the icon
                R.drawable.trial_home_avd, // the AVD that will be shown in FAB
                R.id.locationFragment
                    // optional if you use Jetpack Navigation
            )
        )
        binding.navView.setMenuItems(menuItems, 1)

        binding.navView.setupWithNavController(findNavController(R.id.fragment))

    }










}




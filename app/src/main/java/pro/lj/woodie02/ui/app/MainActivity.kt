package pro.lj.woodie02.ui.app

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import pro.lj.woodie02.R
import pro.lj.woodie02.databinding.ActivityMainBinding
import pro.lj.woodie02.repositories.MainRepository
import pro.lj.woodie02.viewmodels.MainViewModel
import pro.lj.woodie02.viewmodels.MainViewModelProviderFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var layout: View
    lateinit var viewModel: MainViewModel


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
        binding.bottomNavBar.setupWithNavController(findNavController(R.id.fragment))

        binding.bottomNavBar.itemIconTintList = null
//
//        if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                &&
//                ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            askForLocationPermissions();
//        } else {
//            //do your work
//        }
    }

    private fun askForLocationPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this)
                    .setTitle("Location permessions needed")
                    .setMessage("you need to allow this permission!")
                    .setPositiveButton("Sure", DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                100)
                    })
                    .setNegativeButton("Not now", DialogInterface.OnClickListener { dialog, which ->
                        //                                        //Do nothing
                    })
                    .show()

        } else {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100)
        }
    }

    private val requestPermissionLauncher =
            registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.i("Permission: ", "Granted")
                } else {
                    Log.i("Permission: ", "Denied")
                }
            }

//    fun onClickRequestPermission(view: View) {
//        when {
//            ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                layout.showSnackbar(
//                        view,
//                        getString(R.string.permission_granted),
//                        Snackbar.LENGTH_INDEFINITE,
//                        null
//                ) {}
//            }
//
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.CAMERA
//            ) -> {
//                layout.showSnackbar(
//                        view,
//                        getString(R.string.permission_required),
//                        Snackbar.LENGTH_INDEFINITE,
//                        getString(R.string.ok)
//                ) {
//                    requestPermissionLauncher.launch(
//                            Manifest.permission.CAMERA
//                    )
//                }
//            }
//
//            else -> {
//                requestPermissionLauncher.launch(
//                        Manifest.permission.CAMERA
//                )
//            }
//        }
//    }
//
//    private fun isLocationPermissionGranted(): Boolean {
//        return if (ActivityCompat.checkSelfPermission(
//                        this,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(
//                            android.Manifest.permission.ACCESS_FINE_LOCATION,
//                            android.Manifest.permission.ACCESS_COARSE_LOCATION
//                    ),
//                    143
//            )
//            false
//        } else {
//            true
//        }
//    }

}



fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
) {
    val snackbar = Snackbar.make(view, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    } else {
        snackbar.show()
    }
}
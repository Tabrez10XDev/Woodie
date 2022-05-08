package pro.lj.woodie02.ui.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import pro.lj.woodie02.R
import pro.lj.woodie02.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var layout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setTheme(R.style.Theme_Woodie02)
        setContentView(view)
        binding.bottomNavBar.setupWithNavController(findNavController(R.id.fragment))

        binding.bottomNavBar.itemIconTintList = null
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

    fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                layout.showSnackbar(
                        view,
                        getString(R.string.permission_granted),
                        Snackbar.LENGTH_INDEFINITE,
                        null
                ) {}
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
            ) -> {
                layout.showSnackbar(
                        view,
                        getString(R.string.permission_required),
                        Snackbar.LENGTH_INDEFINITE,
                        getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(
                            Manifest.permission.CAMERA
                    )
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                )
            }
        }
    }


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
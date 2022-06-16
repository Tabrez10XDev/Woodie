package pro.lj.woodie02.ui.app

import android.Manifest
import android.R.attr.radius
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.shape.CornerFamily
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
        binding.fab.imageTintList = null
        binding.bottomNavBar.setupWithNavController(findNavController(R.id.fragment))
        binding.bottomNavBar.menu.getItem(1).isEnabled = false
        binding.bottomNavBar.background = null
        binding.bottomNavBar.itemIconTintList = null

        binding.fab.setOnClickListener {
            findNavController(R.id.fragment).navigate(R.id.home)
        }
//        val bottomBarBG = binding.bottomAppBar.background
//        val shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
//            .setAllCorners(CornerFamily.ROUNDED, 30f)
//            .build()
//        binding.bottomAppBar.background = shapeAppearanceModel
//        ViewCompat.setBackground(binding.bottomAppBar, MaterialShapeDrawable(shapeAppearanceModel))


    }





}




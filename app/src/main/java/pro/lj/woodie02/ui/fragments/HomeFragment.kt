package pro.lj.woodie02.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import pro.lj.woodie02.R
import pro.lj.woodie02.adapters.HomeAdapter
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.FragmentHomeBinding
import pro.lj.woodie02.ui.app.AR
import pro.lj.woodie02.utils.Status
import pro.lj.woodie02.viewmodels.MainViewModel

class HomeFragment : Fragment() {
    //private lateinit var codeScanner: CodeScanner

    private val viewModel : MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var homeAdapter: HomeAdapter
    lateinit var locationManager: LocationManager
    var locationByNetwork : Location ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        fireStore = FirebaseFirestore.getInstance()
        setupHomeRecyclerView()
        checkForPermission()

        fetchLocation()
        binding.btnQR.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_QRFragment)
        }
        startLoading()
        fireStore.collection("trees")
                .get()
                .addOnSuccessListener { result ->
                    Log.d("TABY",  result.toString())

                    val itemList : MutableList<Tree> = arrayListOf()
                    for (document in result) {
                        Log.d("TABY",  document.toString())

                        itemList.add(
                                document.toObject(Tree::class.java)
                        )

                        homeAdapter.differ.submitList(itemList)
                    }
                    stopLoading()
                }
                .addOnFailureListener { exception ->
                    Log.w("TABY", "Error getting documents.", exception)
                    Toast.makeText(activity,exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    stopLoading()

                }

        updateWeather()

        homeAdapter.setOnItemClickListener { tree ->
            val bundle = Bundle().apply {
                putSerializable("item", tree)
            }
            val intent = Intent(requireActivity(), AR::class.java)
            intent.putExtra("item", bundle)
            startActivity(intent)
        }


    }

    private fun updateWeather(){
        viewModel.liveWeather.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    val weather = it.data?.weather?.get(0)
                    ((it.data?.main?.temp?.minus(273.15))?.toFloat().toString() + "˚C").also { binding.tvTemp.text = it }
                    when {
                        weather?.id.toString().contains("800") -> {
                            binding.ivWeather.setImageResource(R.drawable.ic_clear)
                        }
                        weather?.id.toString().startsWith("80") -> {
                            binding.ivWeather.setImageResource(R.drawable.ic_clouds)
                        }
                        weather?.id.toString().startsWith("2") -> {
                            binding.ivWeather.setImageResource(R.drawable.ic_thunderstorms)
                        }
                        weather?.id.toString().startsWith("3") -> {
                            binding.ivWeather.setImageResource(R.drawable.ic_drizzle)
                        }
                        weather?.id.toString().startsWith("5") -> {
                            binding.ivWeather.setImageResource(R.drawable.ic_rain)
                        }
                        weather?.id.toString().startsWith("6") -> {
                            binding.ivWeather.setImageResource(R.drawable.ic_snow)
                        }
                        weather?.id.toString().startsWith("7") -> {
                            binding.ivWeather.setImageResource(R.drawable.partly_cloudy_day)
                        }
                    }
                }
                Status.ERROR -> {}
            }
        })
    }


    private fun startLoading(){
        binding.animationView.visibility = View.VISIBLE
        binding.animationView.playAnimation()

    }

    private fun stopLoading(){
        binding.animationView.pauseAnimation()
        binding.animationView.visibility = View.INVISIBLE
    }

    private fun setupHomeRecyclerView(){

        val spannedGridLayoutManager = SpannedGridLayoutManager(
                orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                spans = 3,
        )
        spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup{ position ->
            if(position % 6 == 0) {
                SpanSize(2, 2)
            }else{
                SpanSize(1,1)
            }
        }
        homeAdapter =
                HomeAdapter()
        binding.homeRV.apply {
            adapter = homeAdapter
            layoutManager = spannedGridLayoutManager

        }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(){
        locationManager = requireActivity().getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                viewModel.getWeather(location.latitude.toString(), location.longitude.toString())
                locationByNetwork= location
                return
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                Toast.makeText(requireContext(), "Check your connection",Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
            )
        }
    }

    private fun checkForPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            askForPermissions()
        } else {
            //do your work
        }
    }

    private fun askForPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Location permissions needed")
                .setMessage("you need to allow this permission!")
                .setPositiveButton("Sure", DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        100)
                })
                .setNegativeButton("Not now", DialogInterface.OnClickListener { dialog, which ->
                    //                                        //Do nothing
                })
                .show()

        } else {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100)
        }

    }

}


package pro.lj.woodie02.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import pro.lj.woodie02.R
import pro.lj.woodie02.adapters.HomeAdapter
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.FragmentHomeBinding
import pro.lj.woodie02.ui.app.AR
import pro.lj.woodie02.ui.app.MainActivity
import pro.lj.woodie02.utils.Status
import pro.lj.woodie02.viewmodels.MainViewModel


class HomeFragment : Fragment() {
    //private lateinit var codeScanner: CodeScanner
    var doubleBackToExitPressedOnce: Boolean ?= null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // in here you can do logic when backPress is clicked
                if(doubleBackToExitPressedOnce == true){ activity?.finish() }

                doubleBackToExitPressedOnce = true
                Toast.makeText(requireContext(), "Press again to exit", Toast.LENGTH_SHORT).show()

                Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        fireStore = FirebaseFirestore.getInstance()
        setupHomeRecyclerView()
        checkForPermission()
        fetchLocation()
//        binding.btnQR.setOnClickListener {
//            findNavController().navigate(R.id.action_home_to_QRFragment)
//        }
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

                    }
                    homeAdapter.differ.submitList(itemList)

                    viewModel.firebaseData = MutableLiveData(itemList)
                    stopLoading()
                }
                .addOnFailureListener { exception ->
                    Log.w("TABY", "Error getting documents.", exception)
                    Toast.makeText(activity,exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    stopLoading()

                }



        updateWeather()

        Log.d("AnimationCheck",binding.animationView.visibility.toString())
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
                    ((it.data?.main?.temp?.minus(273.15))?.toFloat()?.toInt().toString() + "˚C").also { binding.tvTemp.text = it }
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
                Status.ERROR -> {
                    Log.d("Weathers",it.message.toString())
                    Toast.makeText(requireContext(),"Unknown error occured",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun startLoading(){

        binding.animationView.visibility = View.VISIBLE
        binding.animationView.alpha = 1f
        binding.animationView.playAnimation()

    }

    private fun stopLoading(){
        binding.animationView.pauseAnimation()
        binding.animationView.alpha = 0f
        binding.animationView.visibility = View.INVISIBLE
    }



    private fun setupHomeRecyclerView(){
        val gridLayoutManager = GridLayoutManager(
            requireContext(),2
        )
        homeAdapter =
                HomeAdapter()
        binding.homeRV.apply {
            adapter = homeAdapter
            layoutManager = gridLayoutManager
        }
    }


    @SuppressLint("MissingPermission")
    fun fetchLocation(){
        locationManager = requireActivity().getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                viewModel.getWeather(location.latitude.toString(), location.longitude.toString())
                locationByNetwork= location
                Log.d("Weathers",locationByNetwork.toString())
                return
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            }
            override fun onProviderEnabled(provider: String) {
                val location = locationManager.getLastKnownLocation(provider)
                Log.d("Weathers",location?.latitude.toString() + location?.longitude + "hii")
                viewModel.getWeather(location?.latitude.toString(), location?.longitude.toString())

            }
            override fun onProviderDisabled(provider: String) {
                if(!(activity as MainActivity).locationPromt) {
                    Toast.makeText(
                        requireContext(),
                        "Turn on location services for live weather updates",
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as MainActivity).locationPromt = true
                }
                return
            }
        }

//        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
            )
//        }
    }

    private fun checkForPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ||
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


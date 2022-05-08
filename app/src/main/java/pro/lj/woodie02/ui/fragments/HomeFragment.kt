package pro.lj.woodie02.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import pro.lj.woodie02.R
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    //private lateinit var codeScanner: CodeScanner

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var homeAdapter: HomeAdapter


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
       // val scannerView = binding.scannerView
        val activity = requireActivity()
        fireStore = FirebaseFirestore.getInstance()
        setupHomeRecyclerView()
        binding.btnQR.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_QRFragment)
        }
        fireStore.collection("trees")
                .get()
                .addOnSuccessListener { result ->
                    val itemList : MutableList<Tree> = arrayListOf()
                    for (document in result) {

                        itemList.add(
                                document.toObject(Tree::class.java)
                        )

                        homeAdapter.differ.submitList(itemList)
                        Log.d("TABY", "${document.id} => ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TABY", "Error getting documents.", exception)
                }
//        val intent = Intent(requireActivity(), AR::class.java)
//        startActivity(intent)
//        codeScanner = CodeScanner(activity, scannerView)
//        codeScanner.decodeCallback = DecodeCallback {
//            activity.runOnUiThread {
//                Toast.makeText(activity, it.text + "ily", Toast.LENGTH_LONG).show()
//
//            }
//        }
//        codeScanner.startPreview()
//
//        scannerView.setOnClickListener {
//            codeScanner.startPreview()
//        }



    }
//
//    override fun onResume() {
//        super.onResume()
//        codeScanner.startPreview()
//    }
//
//    override fun onPause() {
//        codeScanner.releaseResources()
//        super.onPause()
//    }

    private fun setupHomeRecyclerView(){
        homeAdapter =
                HomeAdapter()
        binding.homeRV.apply {
            adapter = homeAdapter
            layoutManager = GridLayoutManager(requireActivity(), 2)
//            edgeEffectFactory =
//                    BounceEdgeEffectFactory()
        }
    }

}


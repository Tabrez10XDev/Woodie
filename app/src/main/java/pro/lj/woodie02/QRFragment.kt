package pro.lj.woodie02

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.google.firebase.firestore.FirebaseFirestore
import pro.lj.woodie02.databinding.FragmentHomeBinding
import pro.lj.woodie02.databinding.FragmentQRBinding


class QRFragment : Fragment() {

    private lateinit var fireStore : FirebaseFirestore
    private lateinit var codeScanner: CodeScanner

    private var _binding: FragmentQRBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scannerView = binding.scannerView
        val activity = requireActivity()
        fireStore = FirebaseFirestore.getInstance()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
//                Toast.makeText(activity, it.text , Toast.LENGTH_LONG).show()
                getTree(it.text)

            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        codeScanner.startPreview()



//        scannerView.setOnClickListener {
//            codeScanner.startPreview()
//        }

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun getTree(docId : String){
        var mToast : Toast ?= null

        fireStore.collection("trees")
                .document(docId)
                .get()
                .addOnSuccessListener { result ->
                    if (result.exists()){
                    val tree = result.toObject(Tree::class.java)
                        Log.d("shami", tree?.name + "\n" + docId)
                        val bundle = Bundle().apply {
                            putSerializable("item", tree)
                        }
                        val intent = Intent(requireActivity(), AR::class.java)
                        intent.putExtra("item", bundle)
                        startActivity(intent)

                }else{
                        if(mToast == null){
                            mToast = Toast.makeText(activity,"Invalid QR \n Click on the screen to scan again", Toast.LENGTH_SHORT)
                            mToast?.show()

                        }
                    }
                }
                .addOnFailureListener { exception ->

                    Toast.makeText(requireContext(), "Error getting documents.", Toast.LENGTH_SHORT).show()
                }
    }


}
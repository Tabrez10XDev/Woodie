package pro.lj.woodie02

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import pro.lj.woodie02.databinding.FragmentHomeBinding
import pro.lj.woodie02.databinding.FragmentQRBinding


class QRFragment : Fragment() {

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

        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
//                Toast.makeText(activity, it.text , Toast.LENGTH_LONG).show()
                val intent = Intent(requireActivity(), AR::class.java)
                startActivity(intent)
            }
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


}
package pro.lj.woodie02.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pro.lj.woodie02.adapters.HomeAdapter
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.FragmentSearchBinding
import pro.lj.woodie02.ui.app.AR
import pro.lj.woodie02.utils.Const.SEARCH_TIME_DELAY
import pro.lj.woodie02.viewmodels.MainViewModel


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var searchAdapter: HomeAdapter
    private var flag : Boolean ?= false


    var rvData: List<Tree> ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.firebaseData.observe(viewLifecycleOwner, Observer {
            rvData = it
            if(!flag!!){
                searchAdapter.differ.submitList(rvData)

            }

        })
        setupHomeRecyclerView()
        var job : Job?= null
        binding.etSearch.addTextChangedListener{editable->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let { editable->
                    var editableString = editable.toString().toLowerCase()
                    if(editableString.isNotEmpty()){
                        var lis = mutableListOf<Tree>()
                        rvData?.forEach { tree ->
                            if(tree.name!!.toLowerCase()!!.contains(editableString)
                                || tree.scientificName.toLowerCase()!!.contains(editableString)){
                                lis.add(tree)
                            }

                        }
                        searchAdapter.differ.submitList(lis)

                    }else{
                        searchAdapter.differ.submitList(rvData)
                    }
                }
            }
        }
        searchAdapter.setOnItemClickListener { tree ->
            val bundle = Bundle().apply {
                putSerializable("item", tree)
            }
            val intent = Intent(requireActivity(), AR::class.java)
            intent.putExtra("item", bundle)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupHomeRecyclerView(){
        val gridLayoutManager = GridLayoutManager(
            requireContext(),2
        )
        searchAdapter =
            HomeAdapter()
        binding.searchRV.apply {
            adapter = searchAdapter
            layoutManager = gridLayoutManager

        }
    }

}
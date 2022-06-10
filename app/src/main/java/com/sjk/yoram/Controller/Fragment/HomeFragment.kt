package com.sjk.yoram.Controller.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjk.yoram.MainVM
import com.sjk.yoram.model.*
import com.sjk.yoram.model.Adapter.CardAdapter
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragHomeBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel
import com.sjk.yoram.viewmodel.FragHomeViewModel
import kotlinx.coroutines.*

class HomeFragment: Fragment() {
    //private val binding by lazy { FragHomeBinding.inflate(layoutInflater) }
    private lateinit var binding: FragHomeBinding
    private val mainViewModel: MainVM by activityViewModels()
    private lateinit var viewModel: FragHomeViewModel
    private lateinit var dptFragViewModel: FragDptmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_home, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(FragHomeViewModel::class.java)
        dptFragViewModel = ViewModelProvider(requireActivity()).get(FragDptmentViewModel::class.java)

        viewModel.loadRootDpts()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = CardAdapter()
        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL


        adapter.setOnDptItemClickListener(object: CardAdapter.OnDptItemClickListener {
            override fun onItemClick(type: DptButtonType, dptCode: Int) {
                CoroutineScope(Dispatchers.Main).async {
                    mainViewModel.moveDptFrag()
                    dptFragViewModel.sortAndExpandDepartment(type, dptCode)
                }
            }
        })
        adapter.setOnSearchBarClickListener(object: CardAdapter.OnSearchBarClickListener {
            override fun onSearchBarClick() {
                // change department fragment and focus searchbar
                mainViewModel.moveDptFrag()
                dptFragViewModel.isMoved.value = true
            }
        })

        binding.vm = viewModel
        binding.fragHomeRecycler.adapter = adapter
        binding.fragHomeRecycler.layoutManager = layoutManager


        viewModel.cards.observe(viewLifecycleOwner, Observer {
            (binding.fragHomeRecycler.adapter as CardAdapter).fetchCard(it)
        })

        viewModel.rootDpts.observe(viewLifecycleOwner, Observer {
            (binding.fragHomeRecycler.adapter as CardAdapter).fetchDpts(it)
        })


        var user = mainViewModel.loginData.value ?: MyLoginData()
        Log.d("JKJK", "HomeFrag -- loginData=${user}")
        viewModel.addCard(Card(CardType.HOME_BANNER))
        viewModel.addCard(Card(CardType.HOME_DEPARTMENT))
        Log.d("JKJK", "HomeFrag -- loginState=${mainViewModel.loginState}")
        viewModel.addCard(Card(CardType.HOME_ID, userData(user)))

        mainViewModel.loginState.observe(viewLifecycleOwner, Observer {
            user = mainViewModel.loginData.value ?: MyLoginData()
            viewModel.modifyCard(2, Card(CardType.HOME_ID, userData(user)))
        })

    }

    companion object {
        fun newInstance(title:String) = HomeFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }



}
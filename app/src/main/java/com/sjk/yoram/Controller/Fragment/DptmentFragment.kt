package com.sjk.yoram.Controller.Fragment

import com.sjk.yoram.Model.DepartmentV2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjk.yoram.Model.Adapter.DepartmentCardAdapter
import com.sjk.yoram.Model.Department
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragDptmentBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel

class DptmentFragment: Fragment() {
    // private val binding by lazy { FragDptmentBinding.inflate(layoutInflater) }
    private lateinit var binding: FragDptmentBinding
    private lateinit var viewModel: FragDptmentViewModel

    private val dpts: MutableList<Department> = mutableListOf<Department>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_dptment, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(FragDptmentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel

        val sortType = mutableListOf<String>("부서별", "직급별", "이름 순")
        val spinnerAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, sortType)
        val recycleManager = LinearLayoutManager(this.context)
        recycleManager.orientation = LinearLayoutManager.VERTICAL

        var searchBar = binding.fragDptmentSearchbar
        val recyclerAdapter = DepartmentCardAdapter()
        recyclerAdapter.setOnDptSubClickListener(object : DepartmentCardAdapter.onDptSubClickListener {
            override fun onDptSubClick(dptCode: Int) {
                viewModel.expandDepartment(dptCode)
            }
        })

        binding.fragDptmentDropdown.adapter = spinnerAdapter
        binding.fragDptmentRecycler.adapter = recyclerAdapter
        binding.fragDptmentRecycler.layoutManager = recycleManager

        viewModel.dptSortType.observe(viewLifecycleOwner, Observer {
            binding.fragDptmentDropdown.setSelection(it.ordinal)
            when (it) {
                DptButtonType.DEPARTMENT -> viewModel.loadAllDepartmentsByDpt()
                DptButtonType.POSITION -> viewModel.loadAllDepartmentsByPos()
            }
        })

        viewModel.departments.observe(viewLifecycleOwner, Observer {
            (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).fetchData(it)
            if (!viewModel.dptFetched)
                (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).notifyDataSetChanged()

        })

        binding.fragDptmentDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    DptButtonType.DEPARTMENT.ordinal -> viewModel.setSortType(DptButtonType.DEPARTMENT)
                    DptButtonType.POSITION.ordinal -> viewModel.setSortType(DptButtonType.POSITION)
                    DptButtonType.NAME.ordinal -> viewModel.setSortType(DptButtonType.NAME)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

//        viewModel.loadAllDepartmentsByDpt()

    }

    companion object {
        fun newInstance(title:String) = DptmentFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }

}
package com.sjk.yoram.Controller.Fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cielyang.android.clearableedittext.ClearableEditText
import com.cielyang.android.clearableedittext.OnTextClearedListener
import com.sjk.yoram.MainVM
import com.sjk.yoram.Model.Adapter.DepartmentCardAdapter
import com.sjk.yoram.Model.DptButtonType
import com.sjk.yoram.R
import com.sjk.yoram.databinding.FragDptmentBinding
import com.sjk.yoram.viewmodel.FragDptmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DptmentFragment: Fragment() {
    // private val binding by lazy { FragDptmentBinding.inflate(layoutInflater) }
    private lateinit var binding: FragDptmentBinding
    private val mainViewModel: MainVM by activityViewModels()
    private lateinit var viewModel: FragDptmentViewModel
    private lateinit var searchBarEditText: ClearableEditText
    private lateinit var searchBarIcon: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val title = requireArguments().getString("title")
        //Log.d("jk", "${title} 오픈")
        binding = DataBindingUtil.inflate(inflater, R.layout.frag_dptment, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(FragDptmentViewModel::class.java)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sortType = mutableListOf<String>("부서별", "직급별", "이름 순")
        val spinnerAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, sortType)
        val recycleManager = LinearLayoutManager(this.context)
        recycleManager.orientation = LinearLayoutManager.VERTICAL

        this.searchBarEditText = binding.fragDptmentSearchbarEt
        this.searchBarIcon = binding.fragDptmentSearchbarIcon
        val recyclerAdapter = DepartmentCardAdapter()
        recyclerAdapter.setOnDptSubClickListener(object : DepartmentCardAdapter.onDptSubClickListener {
            override fun onDptSubClick(dptCode: Int) {
                if (viewModel.isSearch.value!!)
                    viewModel.expandSearchDepartment(dptCode)
                else
                    viewModel.expandDepartment(dptCode)
            }
            override fun onDptParentNotify(parentPos: Int) {
                recyclerAdapter.notifyItemChanged(parentPos)
            }
        })

        binding.fragDptmentDropdown.adapter = spinnerAdapter
        binding.fragDptmentRecycler.adapter = recyclerAdapter
        binding.fragDptmentRecycler.layoutManager = recycleManager

        viewModel.recycler = binding.fragDptmentRecycler

        viewModel.dptSortType.observe(viewLifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.clearData()
                when (it) {
                    DptButtonType.DEPARTMENT -> viewModel.loadAllDepartmentsByDpt()
                    DptButtonType.POSITION -> viewModel.loadAllDepartmentsByPos()
                }
                binding.fragDptmentDropdown.setSelection(it.ordinal)
            }
        })

        viewModel.departments.observe(viewLifecycleOwner, Observer {
            if (viewModel.isSearch.value!!)
                return@Observer
            (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).fetchData(it)
            if (!viewModel.dptFetched.value!!)
                (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).notifyDataSetChanged()
        })

        viewModel.searchResult.observe(viewLifecycleOwner, Observer {
            if (viewModel.isSearch.value!!) {
                (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).fetchData(it)
                (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).notifyDataSetChanged()
            }
        })

        viewModel.isSearch.observe(viewLifecycleOwner, Observer {
            if (!it && viewModel.dptFetched.value!!) {
                (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).fetchData(viewModel.departments.value!!)
                (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).notifyDataSetChanged()
            }
        })

        viewModel.dptFetched.observe(viewLifecycleOwner, Observer {
            (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).notifyDataSetChanged()
        })

        binding.fragDptmentDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                searchBarEditText.setText("")
                searchBarEditText.afterTextChanged(searchBarEditText.text)
                when (p2) {
                    DptButtonType.DEPARTMENT.ordinal -> viewModel.setSortType(DptButtonType.DEPARTMENT)
                    DptButtonType.POSITION.ordinal -> viewModel.setSortType(DptButtonType.POSITION)
                    DptButtonType.NAME.ordinal -> viewModel.setSortType(DptButtonType.NAME)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        mainViewModel.dptClickState.observe(viewLifecycleOwner, Observer {
            (binding.fragDptmentRecycler.adapter as DepartmentCardAdapter).notifyDataSetChanged()
        })

        viewModel.isMoved.observe(viewLifecycleOwner, Observer {
            if (it) {
                val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                searchBarEditText.requestFocus()
                imm.showSoftInput(searchBarEditText, 0)
            }
        })

        searchBarIcon.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(p0!!.windowToken, 0)
            }
        })

        searchBarEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.isNullOrBlank() || p0.isNullOrEmpty()) {
                    viewModel.isSearch.value = false
                    searchBarIcon.setImageResource(R.drawable.ic_baseline_search_24)
                } else {
                    viewModel.searchDptName(p0.toString())
                    searchBarIcon.setImageResource(R.drawable.ic_baseline_arrow_back_24)
                }
            }
        })
        searchBarEditText.setOnTextClearedListener(object :OnTextClearedListener {
            override fun onTextCleared() {
                viewModel.isSearch.value = false
            }
        })
    }


    companion object {
        fun newInstance(title:String) = DptmentFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
            }
        }
    }

    fun focusSearchbar() {
        searchBarEditText.requestFocus()
    }

}
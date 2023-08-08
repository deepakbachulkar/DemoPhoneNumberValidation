package com.demo.lloydstest.view.home

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.demo.lloydstest.R
import com.demo.lloydstest.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import com.demo.lloydstest.view.BaseFragment

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val vm by viewModels<HomeViewModel>()
    private var  binding:FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        binding?.vm = vm
        vm.hitCountryCodesData()
        initSpinner()
    }

    /** Country code spinner initialized **/
    private fun initSpinner() {
        binding?.spinner?.adapter = vm.countryAdapter
        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                vm.code.set(vm.countryAdapter.getItem(position).diallingCode?.let { it.substring(1, it.length) })
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

}
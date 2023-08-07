package com.demo.lloydstest.view.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.demo.lloydstest.R
import com.demo.lloydstest.databinding.FragmentHomeBinding
import com.demo.lloydstest.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import com.demo.lloydstest.BR
import com.demo.lloydstest.view.BaseFragment

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val vm by viewModels<HomeViewModel>()
    private var  binding:FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        listener()
        initSpinner()
        initObserver()
        vm.hitCountryCodesData()
    }

    /** API call observer of this screen
     * Country code list
     * Phone number validation
     * **/
    private fun initObserver() {
        vm.countryDetailsMLD.observe(viewLifecycleOwner) { item ->
            item?.let {
                binding?.lbl?.visibility = if(item.valid) View.VISIBLE else View.GONE
                binding?.validMessage?.text = vm.initPhoneValidation(item.valid)
                binding?.countryName?.text = item.countryName
                binding?.internationalFormat?.text = item.internationalFormat
                binding?.carrier?.text = if (item.carrier.isNotBlank()) "Carrier: ${item.carrier}" else ""
                binding?.location?.text = if (item.location.isNotBlank()) "Location: ${item.location}" else ""
            }
        }
        vm.countries.observe(viewLifecycleOwner) { item ->
            if(item!=null && item.isNotEmpty())
                binding?.syncCode?.visibility = View.GONE
            else
                binding?.syncCode?.visibility = View.VISIBLE
        }
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

    /**
     * All listener initialized
     * Valid button listener
     * Sync code button listener
     * Edit phone number listener
     * **/
    private fun listener(){
        binding?.valid?.setOnClickListener {
            binding?.valid?.hideKeyboard(requireContext())
            vm.onClickValid()
        }
        binding?.syncCode?.setOnClickListener{
            vm.hitCountryCodesData()
        }
        binding?.number?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                vm.isMessageValidShow.set(false)
                vm.phoneNumber.set(p0.toString())
                vm.isMessageValidShow.set(true)
                vm.isMessageValidShow.notifyPropertyChanged(BR.vm);
            }
        })
    }
}
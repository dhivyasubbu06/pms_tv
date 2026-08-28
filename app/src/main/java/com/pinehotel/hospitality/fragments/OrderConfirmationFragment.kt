package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.adapters.OrderConfirmationAdapter
import com.pinehotel.hospitality.databinding.FragmentOrderConfirmationBinding
import com.pinehotel.hospitality.viewmodels.CartViewModel

class OrderConfirmationFragment : Fragment() {

    private var _binding: FragmentOrderConfirmationBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val results = cartViewModel.submissionState.value
        binding.rvOrderConfirmations.layoutManager = LinearLayoutManager(context)
        binding.rvOrderConfirmations.adapter = OrderConfirmationAdapter(results)

        binding.btnBackHome.setOnClickListener {
            cartViewModel.clearCart()
            cartViewModel.clearSubmissionState()
            findNavController().popBackStack(R.id.servicesDashboardFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

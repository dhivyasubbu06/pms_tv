package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.pinehotel.hospitality.adapters.ServiceAdapter
import com.pinehotel.hospitality.databinding.FragmentServicesDashboardBinding
import com.pinehotel.hospitality.viewmodels.ServicesViewModel

class ServicesDashboardFragment : Fragment() {

    private var _binding: FragmentServicesDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ServiceAdapter(emptyList()) { service ->
            val destId = viewModel.getDestinationId(service.title)
            if (destId != null) {
                try {
                    findNavController().navigate(destId)
                } catch (e: Exception) {
                    Toast.makeText(context, "Feature coming soon: ${service.title}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Category '${service.title}' not mapped to a screen yet.", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.rvDashboard.layoutManager = GridLayoutManager(context, 5)
        binding.rvDashboard.adapter = adapter

        viewModel.services.observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
            
            // Request focus on the first item once data is loaded
            binding.rvDashboard.post {
                val firstItem = binding.rvDashboard.findViewHolderForAdapterPosition(0)?.itemView
                firstItem?.requestFocus() ?: binding.rvDashboard.requestFocus()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.fetchServices()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

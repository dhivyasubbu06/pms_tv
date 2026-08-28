package com.pinehotel.hospitality.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pinehotel.hospitality.adapters.MaintenanceAdapter
import com.pinehotel.hospitality.databinding.FragmentMaintenanceBinding
import com.pinehotel.hospitality.viewmodels.MainViewModel
import com.pinehotel.hospitality.viewmodels.ServicesViewModel
import com.pinehotel.hospitality.utils.NotificationUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MaintenanceFragment : Fragment() {

    private var _binding: FragmentMaintenanceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaintenanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.maintenanceIssues.observe(viewLifecycleOwner) { items ->
            binding.rvMaintenance.layoutManager = GridLayoutManager(context, 2)
            binding.rvMaintenance.adapter = MaintenanceAdapter(items) { issue ->
                val dialog = ServiceRequestDialogFragment.newInstance(
                    issue.id,
                    issue.name,
                    issue.description ?: "",
                    issue.imageUrl,
                    "maintenance"
                )
                dialog.show(childFragmentManager, "service_request")
            }
        }

        observeSubmission()
    }

    private fun observeSubmission() {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.requestSuccess.collectLatest { success ->
                success?.let {
                    if (it) {
                        NotificationUtils.showSuccessNotification(binding.root, "Maintenance request submitted successfully")
                    }
                    mainViewModel.resetRequestState()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
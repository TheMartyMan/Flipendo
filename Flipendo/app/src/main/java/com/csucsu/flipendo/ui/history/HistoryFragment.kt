package com.csucsu.flipendo.ui.history



import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.csucsu.flipendo.R
import com.csucsu.flipendo.databinding.FragmentHistoryBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        historyViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[HistoryViewModel::class.java]

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter { fileHistory ->
            val uri = fileHistory.uriString.toUri()
            val bundle = Bundle().apply {
                putParcelable("pdf_uri", uri)
                putBoolean(
                    "zoom_enabled",
                    PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getBoolean("zoom_enabled", false)
                )
            }
            findNavController().navigate(R.id.pdfViewerFragment, bundle)
        }

        // RecyclerView elválasztással
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoryFragment.adapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.history.collectLatest { list ->
                    if (list.isEmpty()) {
                        binding.textHistory.apply {
                            text = getString(R.string.history_empty)
                            isVisible = true
                        }
                        binding.recyclerViewHistory.isVisible = false
                    } else {
                        adapter.submitList(list)
                        binding.textHistory.isVisible = false
                        binding.recyclerViewHistory.isVisible = true
                    }
                }
            }
        }

        // Előzmény törlés
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.history_menu, menu)
            }
            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_clear_history -> {
                        historyViewModel.clearHistory()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


package com.csucsu.flipendo.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.csucsu.flipendo.R
import com.csucsu.flipendo.databinding.FragmentHomeBinding
import com.csucsu.flipendo.ui.history.HistoryViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by activityViewModels()
    private lateinit var openDocumentLauncher: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                // Zoom preferencia
                val zoomEnabled = PreferenceManager
                    .getDefaultSharedPreferences(requireContext())
                    .getBoolean("zoom_enabled", false)

                // URI + Zoom flag
                val bundle = Bundle().apply {
                    putParcelable("pdf_uri", it)
                    putBoolean("zoom_enabled", zoomEnabled)
                }

                // PdfViewerFragmentre navigálás
                findNavController().navigate(R.id.pdfViewerFragment, bundle)

                // Hozzáadjuk ezt a fájlt az előzményekhez
                historyViewModel.addOpenedFile(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.textHome.apply {
            homeViewModel.text.observe(viewLifecycleOwner) { txt ->
                text = txt
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Csak a HomeFragmentben adhatunk hozzá új fájlt
        binding.fab.setOnClickListener {
            openDocumentLauncher.launch(arrayOf("application/pdf"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

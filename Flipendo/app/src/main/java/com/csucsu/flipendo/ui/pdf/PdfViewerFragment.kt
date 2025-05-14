package com.csucsu.flipendo.ui.pdf

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.csucsu.flipendo.R
import com.csucsu.flipendo.ui.history.HistoryViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class PdfViewerFragment : Fragment(R.layout.fragment_pdf_viewer) {

    private val pdfVm: PdfViewerViewModel  by activityViewModels()
    private val historyVm: HistoryViewModel by activityViewModels()

    private lateinit var pdfAdapter: PdfPagerAdapter
    private var fullscreenItem: MenuItem? = null
    private lateinit var insetsController: WindowInsetsControllerCompat

    private var originalTitle: CharSequence? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalTitle = (requireActivity() as AppCompatActivity).supportActionBar?.title
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        insetsController = WindowInsetsControllerCompat(requireActivity().window, view)
        if (pdfVm.isFullScreen.value == true) hideSystemUI()


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (pdfVm.isFullScreen.value == true) {
                toggleFullScreen(false)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        val pdfUri: Uri? = arguments?.getParcelable("pdf_uri")
        val zoomEnabled = arguments?.getBoolean("zoom_enabled", false) == true

        pdfUri?.let { uri ->
            try {
                // PDF URI
                val pdfFile = copyUriToFile(uri)
                val filename = getDisplayName(uri)
                (requireActivity() as AppCompatActivity).supportActionBar?.title = filename
                val viewPager: ViewPager2 = view.findViewById(R.id.pdfViewPager)

                // Lapozás
                val orientation = PreferenceManager
                    .getDefaultSharedPreferences(requireContext())
                    .getString("page_orientation", "horizontal")
                viewPager.orientation = if (orientation == "vertical")
                    ViewPager2.ORIENTATION_VERTICAL
                else
                    ViewPager2.ORIENTATION_HORIZONTAL

                pdfAdapter = PdfPagerAdapter(pdfFile, zoomEnabled)
                viewPager.adapter = pdfAdapter


                // Lapok száma
                val pageNumberText: TextView = view.findViewById(R.id.pageNumberText)
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        pageNumberText.text = "${position + 1} / ${pdfAdapter.itemCount}"
                    }
                })

            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.open_error, Toast.LENGTH_LONG).show()
                historyVm.removeOpenedFile(uri)
                findNavController().popBackStack()
            }
        }
    }



    @Throws(IOException::class)
    private fun copyUriToFile(uri: Uri): File {
        val inputStream: InputStream = requireContext().contentResolver
            .openInputStream(uri) ?: throw SecurityException("Cannot open URI")
        val file = File(requireContext().cacheDir, "temp.pdf")
        FileOutputStream(file).use { out -> inputStream.copyTo(out) }
        return file
    }


    private fun getDisplayName(uri: Uri): String {
        var name: String? = null
        val cursor: Cursor? = requireContext().contentResolver.query(
            uri, arrayOf(OpenableColumns.DISPLAY_NAME),
            null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name ?: uri.lastPathSegment.orEmpty()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.pdf_viewer_menu, menu)
        fullscreenItem = menu.findItem(R.id.action_fullscreen)
        fullscreenItem?.isChecked = pdfVm.isFullScreen.value == true
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_fullscreen -> {
            val newState = !(pdfVm.isFullScreen.value ?: false)
            item.isChecked = newState
            pdfVm.setFullScreen(newState)
            if (newState) hideSystemUI() else showSystemUI()
            true
        }
        R.id.action_zoom -> {
            val enableZoom = !item.isChecked
            item.isChecked = enableZoom
            pdfAdapter.updateZoomState(enableZoom)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun toggleFullScreen(enable: Boolean) {
        pdfVm.setFullScreen(enable)
        if (enable) hideSystemUI() else showSystemUI()
    }

    private fun hideSystemUI() {
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    private fun showSystemUI() {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        fullscreenItem?.isChecked = false
    }

    override fun onResume() {
        super.onResume()
        if (pdfVm.isFullScreen.value == true) hideSystemUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = originalTitle
    }
}

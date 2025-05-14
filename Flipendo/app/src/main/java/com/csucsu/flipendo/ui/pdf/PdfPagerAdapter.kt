package com.csucsu.flipendo.ui.pdf

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.RecyclerView
import com.csucsu.flipendo.R
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PdfPagerAdapter(
    file: File,
    var zoomEnabled: Boolean
) : RecyclerView.Adapter<PdfPagerAdapter.PdfViewHolder>() {

    private var pdfRenderer: PdfRenderer
    private var pageCount: Int

    init {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
        pageCount = pdfRenderer.pageCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf_page, parent, false)
        return PdfViewHolder(view)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind(pdfRenderer, position)
    }

    override fun getItemCount(): Int = pageCount


    @SuppressLint("NotifyDataSetChanged")
    fun updateZoomState(enabled: Boolean) {
        zoomEnabled = enabled
        notifyDataSetChanged()
    }

    inner class PdfViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // PhotoView-t a zoom funkció támogatásához
        val photoView: PhotoView = view.findViewById(R.id.pdfPageImage)

        fun bind(renderer: PdfRenderer, pageIndex: Int) {

            CoroutineScope(Dispatchers.IO).launch {
                val page = renderer.openPage(pageIndex)
                val bmp = createBitmap(page.width, page.height).apply {
                    Canvas(this).drawColor(Color.WHITE)
                    page.render(this, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                }
                withContext(Dispatchers.Main) {
                    photoView.setImageBitmap(bmp)
                    photoView.isZoomable = zoomEnabled
                }
            }
        }
    }
}

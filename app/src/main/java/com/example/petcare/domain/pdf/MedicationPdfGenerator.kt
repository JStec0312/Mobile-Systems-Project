package com.example.petcare.domain.pdf

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationPdfGenerator
import java.io.ByteArrayOutputStream

class MedicationPdfGenerator : IMedicationPdfGenerator {

    override fun generateMedicationHistoryPdf(
        petName: String,
        medications: List<Medication>
    ): ByteArray {
        val doc = PdfDocument()
        val output = ByteArrayOutputStream()

        val pageWidth = 595  // ~A4 at 72dpi
        val pageHeight = 842
        val margin = 32

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 1f
        }

        val cols = listOf("Name", "Dose", "From", "To", "Notes")
        val colWidths = intArrayOf(160, 70, 70, 70, 161) // suma ~531 (pageWidth - 2*margin = 531)

        val rowHeight = 18
        val headerHeight = 20

        var pageNumber = 1
        var y = margin

        fun newPage(): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            return doc.startPage(pageInfo)
        }

        var page = newPage()
        var canvas = page.canvas

        // Title
        canvas.drawText("Medication history - $petName", margin.toFloat(), (y + 20).toFloat(), titlePaint)
        y += 40

        fun drawHeader() {
            var x = margin
            val top = y
            val bottom = y + headerHeight

            // outer line top
            canvas.drawLine(margin.toFloat(), top.toFloat(), (pageWidth - margin).toFloat(), top.toFloat(), linePaint)

            cols.forEachIndexed { i, c ->
                val left = x
                val right = x + colWidths[i]

                // vertical lines
                canvas.drawLine(left.toFloat(), top.toFloat(), left.toFloat(), bottom.toFloat(), linePaint)

                // header text
                canvas.drawText(c, (left + 4).toFloat(), (top + 14).toFloat(), headerPaint)

                x = right
            }

            // last vertical + bottom line
            canvas.drawLine((pageWidth - margin).toFloat(), top.toFloat(), (pageWidth - margin).toFloat(), bottom.toFloat(), linePaint)
            canvas.drawLine(margin.toFloat(), bottom.toFloat(), (pageWidth - margin).toFloat(), bottom.toFloat(), linePaint)

            y += headerHeight
        }

        fun ensureSpace(rowsNeeded: Int) {
            val neededPx = headerHeight + rowsNeeded * rowHeight + margin
            if (y + neededPx > pageHeight) {
                doc.finishPage(page)
                page = newPage()
                canvas = page.canvas
                y = margin

                canvas.drawText("Medication history - $petName", margin.toFloat(), (y + 20).toFloat(), titlePaint)
                y += 40
            }
        }

        val meds = medications.sortedBy { it.from }

        drawHeader()

        meds.forEach { med ->
            ensureSpace(rowsNeeded = 1)

            val values = listOf(
                med.name,
                med.dose ?: "-",
                med.from.toString(),
                med.to?.toString() ?: "-",      // <-- nie rob !!, bo poleci
                med.notes ?: ""
            )

            var x = margin
            val top = y
            val bottom = y + rowHeight

            canvas.drawLine(margin.toFloat(), top.toFloat(), (pageWidth - margin).toFloat(), top.toFloat(), linePaint)

            values.forEachIndexed { i, v ->
                val left = x
                val right = x + colWidths[i]

                canvas.drawLine(left.toFloat(), top.toFloat(), left.toFloat(), bottom.toFloat(), linePaint)

                // proste obciecie tekstu (zeby nie wyjezdzalo)
                val clipped = clipToWidth(v, colWidths[i] - 8, textPaint)
                canvas.drawText(clipped, (left + 4).toFloat(), (top + 13).toFloat(), textPaint)

                x = right
            }

            canvas.drawLine((pageWidth - margin).toFloat(), top.toFloat(), (pageWidth - margin).toFloat(), bottom.toFloat(), linePaint)
            canvas.drawLine(margin.toFloat(), bottom.toFloat(), (pageWidth - margin).toFloat(), bottom.toFloat(), linePaint)

            y += rowHeight
        }

        doc.finishPage(page)
        doc.writeTo(output)
        doc.close()

        return output.toByteArray()
    }

    private fun clipToWidth(text: String, maxPx: Int, paint: Paint): String {
        if (paint.measureText(text) <= maxPx) return text
        val ell = "..."
        var t = text
        while (t.isNotEmpty() && paint.measureText(t + ell) > maxPx) {
            t = t.dropLast(1)
        }
        return if (t.isEmpty()) ell else t + ell
    }
}

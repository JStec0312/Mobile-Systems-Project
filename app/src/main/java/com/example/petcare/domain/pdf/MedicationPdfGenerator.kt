package com.example.petcare.domain.pdf

import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.repository.IMedicationPdfGenerator
import java.io.ByteArrayOutputStream
import com.lowagie.text.Document
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Font
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfPCell


class MedicationPdfGenerator : IMedicationPdfGenerator {

    override fun generateMedicationHistoryPdf(
        petName: String,
        medications: List<Medication>
    ): ByteArray {

        val output = ByteArrayOutputStream()
        val document = Document(PageSize.A4)
        PdfWriter.getInstance(document, output)

        document.open()

        document.add(
            Paragraph(
                "Medication history – $petName",
                Font(Font.HELVETICA, 18f, Font.BOLD)
            )
        )

        document.add(Paragraph(" "))

        val table = PdfPTable(5).apply {
            widthPercentage = 100f
            setWidths(floatArrayOf(3f, 2f, 2f, 2f, 3f))
        }

        header(table, "Name")
        header(table, "Dose")
        header(table, "From")
        header(table, "To")
        header(table, "Notes")

        medications.sortedBy { it.from }.forEach { med ->
            cell(table, med.name)
            cell(table, med.dose ?: "-")
            cell(table, med.from.toString())
            cell(table, med.to.toString())
            cell(table, med.notes ?: "")
        }

        document.add(table)
        document.close()

        return output.toByteArray()
    }

    private fun header(table: PdfPTable, text: String) {
        table.addCell(
            PdfPCell(Phrase(text))
        )
    }

    private fun cell(table: PdfPTable, text: String) {
        table.addCell(Phrase(text))
    }
}

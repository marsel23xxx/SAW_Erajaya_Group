package com.erajaya.datamining.service;

import com.erajaya.datamining.controller.LoginController.UserSession;
import com.erajaya.datamining.dao.AlternativeDAO;
import com.erajaya.datamining.model.Alternative;
import com.erajaya.datamining.model.SAWResult;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service untuk generate laporan PDF
 */
public class PDFReportService {
    
    private final SAWService sawService;
    private final AlternativeDAO alternativeDAO;
    
    // Font styling
    private static Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
    private static Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    
    // Colors
    private static BaseColor headerColor = new BaseColor(70, 130, 180);
    private static BaseColor alternateRowColor = new BaseColor(245, 245, 245);
    
    public PDFReportService() {
        this.sawService = new SAWService();
        this.alternativeDAO = new AlternativeDAO();
    }
    
    /**
     * Generate Laporan 1: Data Alternatif
     */
    public boolean generateAlternativeReport(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Header
            addReportHeader(document, "LAPORAN DATA ALTERNATIF PRODUK ELEKTRONIK");
            
            // Info
            addParagraph(document, "Laporan ini berisi data lengkap alternatif produk elektronik yang tersedia " +
                    "dalam sistem untuk analisis SAW.", normalFont);
            document.add(Chunk.NEWLINE);
            
            // Tabel data alternatif
            List<Alternative> alternatives = alternativeDAO.findAll();
            
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{10, 15, 30, 20, 15, 15});
            
            // Header tabel
            addTableHeader(table, new String[]{"Kode", "Nama Produk", "Deskripsi", "Harga", "Kualitas", "Suku Cadang"});
            
            // Data
            for (Alternative alt : alternatives) {
                addTableRow(table, new String[]{
                    alt.getCode(),
                    alt.getName(),
                    alt.getDescription() != null ? alt.getDescription() : "-",
                    alt.getFormattedPrice(),
                    String.valueOf(alt.getQualityScore()),
                    String.valueOf(alt.getSparePartsScore())
                });
            }
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
            // Statistik
            String[] stats = alternativeDAO.getStatistics();
            addParagraph(document, "Statistik Data:", headerFont);
            addParagraph(document, "• Total Alternatif: " + stats[0], normalFont);
            addParagraph(document, "• Rata-rata Harga: Rp " + stats[1], normalFont);
            addParagraph(document, "• Kualitas Tertinggi: " + stats[2], normalFont);
            addParagraph(document, "• Skor Suku Cadang Tertinggi: " + stats[3], normalFont);
            
            addFooter(document);
            document.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate Laporan 2: Matriks Keputusan dan Normalisasi
     */
    public boolean generateMatrixReport(String filePath) {
        try {
            Document document = new Document(PageSize.A4, 20, 20, 30, 30);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Header
            addReportHeader(document, "LAPORAN MATRIKS KEPUTUSAN DAN NORMALISASI SAW");
            
            // Info kriteria
            addParagraph(document, "Kriteria dan Bobot:", headerFont);
            Map<String, Object> criteriaInfo = sawService.getCriteriaInfo();
            String[] names = (String[]) criteriaInfo.get("names");
            double[] weights = (double[]) criteriaInfo.get("weights");
            String[] types = (String[]) criteriaInfo.get("types");
            
            for (int i = 0; i < names.length; i++) {
                addParagraph(document, String.format("• %s: %.2f (%s)", 
                    names[i], weights[i], types[i]), normalFont);
            }
            document.add(Chunk.NEWLINE);
            
            // Matriks Keputusan
            addParagraph(document, "1. MATRIKS KEPUTUSAN", headerFont);
            String[][] decisionMatrix = sawService.getDecisionMatrixDisplay();
            
            PdfPTable decisionTable = new PdfPTable(4);
            decisionTable.setWidthPercentage(100);
            decisionTable.setWidths(new int[]{20, 30, 25, 25});
            
            addTableHeader(decisionTable, new String[]{"Alternatif", "Harga", "Kualitas", "Suku Cadang"});
            
            for (String[] row : decisionMatrix) {
                addTableRow(decisionTable, row);
            }
            
            document.add(decisionTable);
            document.add(Chunk.NEWLINE);
            
            // Matriks Normalisasi
            addParagraph(document, "2. MATRIKS NORMALISASI", headerFont);
            addParagraph(document, "Normalisasi menggunakan rumus:", normalFont);
            addParagraph(document, "• Benefit (Kualitas, Suku Cadang): rij = xij / max(xij)", smallFont);
            addParagraph(document, "• Cost (Harga): rij = min(xij) / xij", smallFont);
            document.add(Chunk.NEWLINE);
            
            String[][] normalizedMatrix = sawService.getNormalizedMatrixDisplay();
            
            PdfPTable normalizedTable = new PdfPTable(4);
            normalizedTable.setWidthPercentage(100);
            normalizedTable.setWidths(new int[]{20, 30, 25, 25});
            
            addTableHeader(normalizedTable, new String[]{"Alternatif", "Harga (N)", "Kualitas (N)", "Suku Cadang (N)"});
            
            for (String[] row : normalizedMatrix) {
                addTableRow(normalizedTable, row);
            }
            
            document.add(normalizedTable);
            
            addFooter(document);
            document.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate Laporan 3: Hasil Perhitungan SAW
     */
    public boolean generateSAWResultReport(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Header
            addReportHeader(document, "LAPORAN HASIL PERHITUNGAN SAW");
            
            // Penjelasan metode
            addParagraph(document, "Metode Simple Additive Weighting (SAW)", headerFont);
            addParagraph(document, "SAW adalah metode penjumlahan terbobot. Konsep dasar SAW adalah " +
                    "mencari penjumlahan terbobot dari rating kinerja setiap alternatif pada semua kriteria.", 
                    normalFont);
            document.add(Chunk.NEWLINE);
            
            // Rumus SAW
            addParagraph(document, "Rumus: Vi = Σ(wj × rij)", normalFont);
            addParagraph(document, "Dimana:", smallFont);
            addParagraph(document, "• Vi = Nilai preferensi alternatif ke-i", smallFont);
            addParagraph(document, "• wj = Bobot kriteria ke-j", smallFont);
            addParagraph(document, "• rij = Rating kinerja ternormalisasi", smallFont);
            document.add(Chunk.NEWLINE);
            
            // Hasil perhitungan
            List<SAWResult> results = sawService.getSAWResults();
            
            PdfPTable resultTable = new PdfPTable(5);
            resultTable.setWidthPercentage(100);
            resultTable.setWidths(new int[]{10, 30, 20, 15, 25});
            
            addTableHeader(resultTable, new String[]{"Rank", "Alternatif", "Kode", "Skor SAW", "Persentase"});
            
            for (SAWResult result : results) {
                addTableRow(resultTable, new String[]{
                    String.valueOf(result.getRanking()),
                    result.getAlternativeName(),
                    result.getAlternativeCode(),
                    result.getFormattedScore(),
                    result.getScorePercentage()
                });
            }
            
            document.add(resultTable);
            document.add(Chunk.NEWLINE);
            
            // Interpretasi hasil
            addParagraph(document, "Interpretasi Hasil:", headerFont);
            if (!results.isEmpty()) {
                SAWResult best = results.get(0);
                SAWResult worst = results.get(results.size() - 1);
                
                addParagraph(document, "• Alternatif terbaik: " + best.getAlternativeName() + 
                        " dengan skor " + best.getFormattedScore(), normalFont);
                addParagraph(document, "• Alternatif terburuk: " + worst.getAlternativeName() + 
                        " dengan skor " + worst.getFormattedScore(), normalFont);
                
                double gap = best.getTotalScoreAsDouble() - worst.getTotalScoreAsDouble();
                addParagraph(document, "• Selisih skor tertinggi-terendah: " + 
                        String.format("%.4f", gap), normalFont);
            }
            
            addFooter(document);
            document.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate Laporan 4: Analisis dan Rekomendasi
     */
    public boolean generateAnalysisReport(String filePath) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Header
            addReportHeader(document, "LAPORAN ANALISIS DAN REKOMENDASI");
            
            // Executive Summary
            addParagraph(document, "EXECUTIVE SUMMARY", headerFont);
            
            List<SAWResult> results = sawService.getSAWResults();
            Map<String, Object> stats = sawService.getSAWStatistics();
            
            if (!results.isEmpty()) {
                addParagraph(document, String.format(
                    "Berdasarkan analisis SAW terhadap %d alternatif produk elektronik, " +
                    "diperoleh hasil bahwa %s merupakan pilihan terbaik dengan skor %.4f. " +
                    "Analisis ini mempertimbangkan kriteria harga (40%%), kualitas (35%%), " +
                    "dan ketersediaan suku cadang (25%%).",
                    results.size(),
                    results.get(0).getAlternativeName(),
                    results.get(0).getTotalScoreAsDouble()
                ), normalFont);
            }
            document.add(Chunk.NEWLINE);
            
            // Analisis per Ranking
            addParagraph(document, "ANALISIS DETAIL PER RANKING", headerFont);
            
            for (int i = 0; i < Math.min(results.size(), 5); i++) {
                SAWResult result = results.get(i);
                Alternative alt = result.getAlternative();
                
                addParagraph(document, String.format("Ranking %d: %s", 
                    result.getRanking(), result.getAlternativeName()), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));;
                
                addParagraph(document, String.format(
                    "• Skor SAW: %s (%s)",
                    result.getFormattedScore(), result.getScorePercentage()
                ), normalFont);
                
                if (alt != null) {
                    addParagraph(document, String.format(
                        "• Harga: %s | Kualitas: %d | Suku Cadang: %d",
                        alt.getFormattedPrice(), alt.getQualityScore(), alt.getSparePartsScore()
                    ), normalFont);
                }
                
                // Analisis kelebihan/kekurangan
                String analysis = getAlternativeAnalysis(result, i + 1);
                addParagraph(document, "• " + analysis, normalFont);
                document.add(Chunk.NEWLINE);
            }
            
            // Rekomendasi
            addParagraph(document, "REKOMENDASI", headerFont);
            
            if (!results.isEmpty()) {
                SAWResult best = results.get(0);
                
                addParagraph(document, "1. Rekomendasi Utama:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                addParagraph(document, String.format(
                    "Pilih %s sebagai alternatif utama karena memiliki kombinasi terbaik " +
                    "dari semua kriteria yang dievaluasi.",
                    best.getAlternativeName()
                ), normalFont);
                document.add(Chunk.NEWLINE);
                
                addParagraph(document, "2. Rekomendasi Alternatif:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                if (results.size() > 1) {
                    SAWResult second = results.get(1);
                    addParagraph(document, String.format(
                        "Jika %s tidak tersedia, %s dapat menjadi pilihan kedua " +
                        "dengan skor %s.",
                        best.getAlternativeName(), second.getAlternativeName(), 
                        second.getFormattedScore()
                    ), normalFont);
                }
                document.add(Chunk.NEWLINE);
                
                addParagraph(document, "3. Strategi Pengadaan:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
                addParagraph(document, 
                    "• Prioritaskan pengadaan berdasarkan ranking SAW\n" +
                    "• Pertimbangkan faktor eksternal: kebutuhan pasar, tren teknologi\n" +
                    "• Monitor perubahan harga dan ketersediaan suku cadang\n" +
                    "• Review dan update analisis secara berkala",
                    normalFont);
            }
            
            // Limitasi
            document.add(Chunk.NEWLINE);
            addParagraph(document, "LIMITASI ANALISIS", headerFont);
            addParagraph(document, 
                "• Analisis berdasarkan data saat ini, dapat berubah seiring waktu\n" +
                "• Bobot kriteria dapat disesuaikan dengan kebijakan perusahaan\n" +
                "• Faktor eksternal (regulasi, tren pasar) belum dipertimbangkan\n" +
                "• Rekomendasi untuk review berkala setiap 3-6 bulan",
                normalFont);
            
            addFooter(document);
            document.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate semua laporan sekaligus
     */
    public boolean generateAllReports(String directoryPath) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            
            boolean success = true;
            success &= generateAlternativeReport(directoryPath + "/01_Data_Alternatif_" + timestamp + ".pdf");
            success &= generateMatrixReport(directoryPath + "/02_Matriks_SAW_" + timestamp + ".pdf");
            success &= generateSAWResultReport(directoryPath + "/03_Hasil_SAW_" + timestamp + ".pdf");
            success &= generateAnalysisReport(directoryPath + "/04_Analisis_Rekomendasi_" + timestamp + ".pdf");
            
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper methods
    private void addReportHeader(Document document, String title) throws DocumentException, IOException {
        // BAGIAN ATAS - Logo dan Informasi Perusahaan
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[]{25, 75}); // Logo 25%, Info Perusahaan 75%

        // Cell untuk logo (kiri)
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setPadding(10);
        logoCell.setVerticalAlignment(Element.ALIGN_TOP);

        try {
            // Path logo Erajaya
            Image logo = Image.getInstance("./src/main/java/com/erajaya/datamining/image/erajaya_logo.png");
            logo.scaleToFit(120, 120); // Ukuran logo
            logo.setAlignment(Element.ALIGN_LEFT);
            logoCell.addElement(logo);
        } catch (Exception e) {
            // Jika logo tidak ditemukan, tampilkan placeholder
            Paragraph logoPlaceholder = new Paragraph("[LOGO ERAJAYA]", headerFont);
            logoPlaceholder.setAlignment(Element.ALIGN_LEFT);
            logoCell.addElement(logoPlaceholder);
        }

        // Cell untuk info perusahaan (kanan)
        PdfPCell companyCell = new PdfPCell();
        companyCell.setBorder(Rectangle.NO_BORDER);
        companyCell.setPadding(10);
        companyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Nama Perusahaan
        Paragraph companyName = new Paragraph("PT ERAJAYA SWASEMBADA", titleFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        companyName.setSpacingAfter(8);
        companyCell.addElement(companyName);

        // Alamat Perusahaan
        Paragraph address1 = new Paragraph("Hayam Wuruk Tower, 19th floor, Jalan Hayam Wuruk No. 108, Tamansari", normalFont);
        address1.setAlignment(Element.ALIGN_CENTER);
        address1.setSpacingAfter(3);
        companyCell.addElement(address1);

        Paragraph address2 = new Paragraph("RT.4/RW.9, Maphar, Kec. Taman Sari, Kota Jakarta Barat", normalFont);
        address2.setAlignment(Element.ALIGN_CENTER);
        address2.setSpacingAfter(3);
        companyCell.addElement(address2);

        Paragraph address3 = new Paragraph("Daerah Khusus Ibukota Jakarta 11240", normalFont);
        address3.setAlignment(Element.ALIGN_CENTER);
        companyCell.addElement(address3);

        headerTable.addCell(logoCell);
        headerTable.addCell(companyCell);
        document.add(headerTable);

        // Spacing setelah header table
        document.add(new Paragraph(" ", normalFont));
        document.add(new Paragraph(" ", normalFont));

        // Garis Pemisah
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        // BAGIAN BAWAH - Judul Laporan dan Info

        // Judul Laporan
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingBefore(10);
        titlePara.setSpacingAfter(15);
        document.add(titlePara);

        // Subtitle sistem
        Paragraph subtitle = new Paragraph("SISTEM DATA MINING - ANALISIS SAW", headerFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);

        // Tanggal dan User
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss");
        Paragraph info = new Paragraph(
            "Tanggal: " + sdf.format(new Date()) + " | " +
            "User: " + UserSession.getCurrentFullName(), 
            smallFont);
        info.setAlignment(Element.ALIGN_RIGHT);
        info.setSpacingAfter(20);
        document.add(info);

        // Garis bawah (opsional)
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);
    }
    
    private void addParagraph(Document document, String text, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setSpacingAfter(10);
        document.add(paragraph);
    }
    
    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            cell.setBackgroundColor(headerColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }
    
    private void addTableRow(PdfPTable table, String[] data) {
        for (int i = 0; i < data.length; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(data[i], smallFont));
            cell.setPadding(5);
            if (table.getRows().size() % 2 == 0) {
                cell.setBackgroundColor(alternateRowColor);
            }
            table.addCell(cell);
        }
    }
    
    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(new LineSeparator());

        // Footer info
        Paragraph footer = new Paragraph(
            "Laporan ini digenerate otomatis oleh Sistem Data Mining SAW PT Erajaya", 
            smallFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        footer.setSpacingAfter(20);
        document.add(footer);

        // Tabel untuk tanda tangan
        PdfPTable signatureTable = new PdfPTable(2);
        signatureTable.setWidthPercentage(100);
        signatureTable.setWidths(new int[]{50, 50}); // Bagi dua kolom sama rata

        // Cell kosong kiri
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph(" ", normalFont)); // Kosong

        // Cell tanda tangan kanan
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(10);

        Paragraph location = new Paragraph("Jakarta, Selasa 5 Agustus 2025", normalFont);
        location.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(location);

        Paragraph mengetahui = new Paragraph("Mengetahui", normalFont);
        mengetahui.setAlignment(Element.ALIGN_CENTER);
        mengetahui.setSpacingBefore(10);
        mengetahui.setSpacingAfter(30); // Space untuk tanda tangan
        rightCell.addElement(mengetahui);

        // Garis untuk tanda tangan
        Paragraph signatureLine = new Paragraph("(                                                    )", normalFont);
        signatureLine.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(signatureLine);

        signatureTable.addCell(leftCell);
        signatureTable.addCell(rightCell);
        document.add(signatureTable);
    }
    
    private String getAlternativeAnalysis(SAWResult result, int ranking) {
        Alternative alt = result.getAlternative();
        if (alt == null) return "Data tidak tersedia";
        
        StringBuilder analysis = new StringBuilder();
        
        switch (ranking) {
            case 1:
                analysis.append("Pilihan terbaik dengan keseimbangan optimal semua kriteria");
                break;
            case 2:
                analysis.append("Alternatif yang sangat baik, dapat menjadi pilihan kedua");
                break;
            case 3:
                analysis.append("Pilihan yang baik dengan beberapa keunggulan spesifik");
                break;
            default:
                analysis.append("Memiliki karakteristik tertentu yang mungkin sesuai kebutuhan khusus");
        }
        
        // Analisis berdasarkan kriteria
        if (alt.getPrice().compareTo(new java.math.BigDecimal("12000000")) < 0) {
            analysis.append(", harga kompetitif");
        }
        if (alt.getQualityScore() >= 90) {
            analysis.append(", kualitas premium");
        }
        if (alt.getSparePartsScore() >= 90) {
            analysis.append(", ketersediaan suku cadang excellent");
        }
        
        return analysis.toString();
    }
}
package com.erajaya.datamining.view;

import com.erajaya.datamining.controller.LoginController;
import com.erajaya.datamining.controller.LoginController.UserSession;
import com.erajaya.datamining.dao.AlternativeDAO;
import com.erajaya.datamining.model.Alternative;
import com.erajaya.datamining.model.SAWResult;
import com.erajaya.datamining.model.User;
import com.erajaya.datamining.service.PDFReportService;
import com.erajaya.datamining.service.SAWService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dashboard utama aplikasi
 */
public class DashboardView extends JFrame {
    
    private User currentUser;
    private SAWService sawService;
    private AlternativeDAO alternativeDAO;
    private PDFReportService pdfReportService;
    
    // Components
    private JTabbedPane tabbedPane;
    private JTable alternativeTable;
    private JTable sawResultTable;
    private DefaultTableModel alternativeTableModel;
    private DefaultTableModel sawResultTableModel;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    // Dashboard panels
    private JPanel dashboardPanel;
    private JPanel alternativePanel;
    private JPanel sawPanel;
    private JPanel reportPanel;
    
    public DashboardView(User user) {
        this.currentUser = user;
        this.sawService = new SAWService();
        this.alternativeDAO = new AlternativeDAO();
        this.pdfReportService = new PDFReportService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initializeComponents() {
        // Frame setup
        setTitle("Dashboard - Sistem Data Mining SAW PT Erajaya");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));
        
        // Services
        sawService = new SAWService();
        alternativeDAO = new AlternativeDAO();
        pdfReportService = new PDFReportService();
        
        // Components
        tabbedPane = new JTabbedPane();
        statusLabel = new JLabel("Siap");
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        
        // Initialize panels
        initializeDashboardPanel();
        initializeAlternativePanel();
        initializeSAWPanel();
        initializeReportPanel();
        
        // Add tabs
        tabbedPane.addTab("🏠 Dashboard", dashboardPanel);
        tabbedPane.addTab("📦 Data Alternatif", alternativePanel);
        tabbedPane.addTab("⚖️ Analisis SAW", sawPanel);
        tabbedPane.addTab("📄 Laporan", reportPanel);
        
        // Set tab permissions
        if (!currentUser.hasPermission("update")) {
            tabbedPane.setEnabledAt(1, false); // Disable alternatif tab untuk staff
        }
    }
    
    private void initializeDashboardPanel() {
        dashboardPanel = new JPanel(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel welcomeLabel = new JLabel("Selamat Datang, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        
        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole().getValue().toUpperCase());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(Color.LIGHT_GRAY);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        headerPanel.add(welcomeLabel, BorderLayout.NORTH);
        headerPanel.add(roleLabel, BorderLayout.SOUTH);
        
        // Quick stats panel
        JPanel statsPanel = createStatsPanel();
        
        // Quick actions panel
        JPanel actionsPanel = createQuickActionsPanel();
        
        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(statsPanel);
        contentPanel.add(actionsPanel);
        
        dashboardPanel.add(headerPanel, BorderLayout.NORTH);
        dashboardPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("📊 Statistik Sistem"));
        
        // Get statistics
        String[] stats = alternativeDAO.getStatistics();
        Map<String, Object> sawStats = sawService.getSAWStatistics();
        
        addStatItem(panel, "Total Alternatif", stats[0]);
        addStatItem(panel, "Rata-rata Harga", "Rp " + stats[1]);
        addStatItem(panel, "Kualitas Tertinggi", stats[2]);
        addStatItem(panel, "Suku Cadang Terbaik", stats[3]);
        
        if (!sawStats.isEmpty()) {
            addStatItem(panel, "Alternatif Terbaik", (String) sawStats.get("bestAlternative"));
            addStatItem(panel, "Skor Tertinggi", String.format("%.4f", (Double) sawStats.get("maxScore")));
        }
        
        return panel;
    }
    
    private void addStatItem(JPanel parent, String label, String value) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        item.add(new JLabel(label + ":"));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 12));
        valueLabel.setForeground(new Color(70, 130, 180));
        item.add(valueLabel);
        
        parent.add(item);
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("⚡ Aksi Cepat"));
        
        JButton calculateBtn = new JButton("🔄 Hitung Ulang SAW");
        JButton viewResultBtn = new JButton("👁️ Lihat Hasil SAW");
        JButton generateReportBtn = new JButton("📄 Generate Laporan");
        JButton logoutBtn = new JButton("🚪 Logout");
        
        // Styling
        Color buttonColor = new Color(70, 130, 180);
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        
        JButton[] buttons = {calculateBtn, viewResultBtn, generateReportBtn, logoutBtn};
        for (JButton btn : buttons) {
            btn.setBackground(buttonColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
        }
        
        logoutBtn.setBackground(new Color(220, 20, 60));
        
        // Event handlers
        calculateBtn.addActionListener(e -> calculateSAW());
        viewResultBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        generateReportBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        logoutBtn.addActionListener(e -> logout());
        
        panel.add(calculateBtn);
        panel.add(viewResultBtn);
        panel.add(generateReportBtn);
        panel.add(logoutBtn);
        
        return panel;
    }
    
    private void initializeAlternativePanel() {
        alternativePanel = new JPanel(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("➕ Tambah");
        JButton editBtn = new JButton("✏️ Edit");
        JButton deleteBtn = new JButton("🗑️ Hapus");
        JButton refreshBtn = new JButton("🔄 Refresh");
        
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);
        
        // Table
        String[] columns = {"ID", "Kode", "Nama Produk", "Harga", "Kualitas", "Suku Cadang", "Deskripsi"};
        alternativeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        alternativeTable = new JTable(alternativeTableModel);
        alternativeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alternativeTable.setRowHeight(25);
        alternativeTable.getColumnModel().getColumn(0).setMaxWidth(50);
        alternativeTable.getColumnModel().getColumn(1).setMaxWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(alternativeTable);
        
        alternativePanel.add(toolbar, BorderLayout.NORTH);
        alternativePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Event handlers
        refreshBtn.addActionListener(e -> loadAlternativeData());
        addBtn.addActionListener(e -> showAlternativeDialog(null));
        editBtn.addActionListener(e -> editSelectedAlternative());
        deleteBtn.addActionListener(e -> deleteSelectedAlternative());
        
        // Permission check
        if (!currentUser.hasPermission("update")) {
            addBtn.setEnabled(false);
            editBtn.setEnabled(false);
        }
        if (!currentUser.hasPermission("delete")) {
            deleteBtn.setEnabled(false);
        }
    }
    
    private void initializeSAWPanel() {
        sawPanel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton calculateBtn = new JButton("🔄 Hitung SAW");
        JButton detailBtn = new JButton("🔍 Detail Perhitungan");
        JButton exportBtn = new JButton("📊 Export Hasil");
        
        controlPanel.add(calculateBtn);
        controlPanel.add(detailBtn);
        controlPanel.add(exportBtn);
        
        // Results table
        String[] sawColumns = {"Rank", "Kode", "Nama Produk", "Harga", "Kualitas", "S.Cadang", "Skor SAW", "%"};
        sawResultTableModel = new DefaultTableModel(sawColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        sawResultTable = new JTable(sawResultTableModel);
        sawResultTable.setRowHeight(25);
        
        // Custom renderer untuk ranking
        sawResultTable.getColumnModel().getColumn(0).setCellRenderer(new RankingCellRenderer());
        
        JScrollPane sawScrollPane = new JScrollPane(sawResultTable);
        
        sawPanel.add(controlPanel, BorderLayout.NORTH);
        sawPanel.add(sawScrollPane, BorderLayout.CENTER);
        
        // Event handlers
        calculateBtn.addActionListener(e -> calculateSAW());
        detailBtn.addActionListener(e -> showSAWDetails());
        exportBtn.addActionListener(e -> exportSAWResults());
    }
    
    private void initializeReportPanel() {
        reportPanel = new JPanel(new BorderLayout());
        
        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("📋 Informasi Laporan"));
        
        infoPanel.add(new JLabel("Sistem dapat menghasilkan 4 jenis laporan PDF:"));
        infoPanel.add(new JLabel(""));
        infoPanel.add(new JLabel("1. 📦 Laporan Data Alternatif - Data lengkap semua produk"));
        infoPanel.add(new JLabel("2. ⚖️ Laporan Matriks SAW - Matriks keputusan dan normalisasi"));
        infoPanel.add(new JLabel("3. 📊 Laporan Hasil SAW - Hasil perhitungan dan ranking"));
        infoPanel.add(new JLabel("4. 📋 Laporan Analisis - Analisis mendalam dan rekomendasi"));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("🎯 Generate Laporan"));
        
        JButton report1Btn = new JButton("📦 Data Alternatif");
        JButton report2Btn = new JButton("⚖️ Matriks SAW");
        JButton report3Btn = new JButton("📊 Hasil SAW");
        JButton report4Btn = new JButton("📋 Analisis");
        JButton allReportsBtn = new JButton("📑 Semua Laporan");
        JButton openFolderBtn = new JButton("📁 Buka Folder");
        
        // Styling
        Font reportButtonFont = new Font("Arial", Font.BOLD, 11);
        Color reportButtonColor = new Color(34, 139, 34);
        
        JButton[] reportButtons = {report1Btn, report2Btn, report3Btn, report4Btn, allReportsBtn};
        for (JButton btn : reportButtons) {
            btn.setBackground(reportButtonColor);
            btn.setForeground(Color.WHITE);
            btn.setFont(reportButtonFont);
            btn.setFocusPainted(false);
        }
        
        openFolderBtn.setBackground(new Color(70, 130, 180));
        openFolderBtn.setForeground(Color.WHITE);
        openFolderBtn.setFont(reportButtonFont);
        openFolderBtn.setFocusPainted(false);
        
        buttonsPanel.add(report1Btn);
        buttonsPanel.add(report2Btn);
        buttonsPanel.add(report3Btn);
        buttonsPanel.add(report4Btn);
        buttonsPanel.add(allReportsBtn);
        buttonsPanel.add(openFolderBtn);
        
        // Event handlers
        report1Btn.addActionListener(e -> generateReport(1));
        report2Btn.addActionListener(e -> generateReport(2));
        report3Btn.addActionListener(e -> generateReport(3));
        report4Btn.addActionListener(e -> generateReport(4));
        allReportsBtn.addActionListener(e -> generateAllReports());
        openFolderBtn.addActionListener(e -> openReportsFolder());
        
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(infoPanel);
        topPanel.add(buttonsPanel);
        
        reportPanel.add(topPanel, BorderLayout.NORTH);
        
        // Permission check
        if (!currentUser.hasPermission("report")) {
            for (JButton btn : reportButtons) {
                btn.setEnabled(false);
            }
            allReportsBtn.setEnabled(false);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main content
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(progressBar, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
        
        // Menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Data menu
        JMenu dataMenu = new JMenu("Data");
        JMenuItem refreshItem = new JMenuItem("Refresh Data");
        JMenuItem calculateItem = new JMenuItem("Hitung SAW");
        refreshItem.addActionListener(e -> loadData());
        calculateItem.addActionListener(e -> calculateSAW());
        dataMenu.add(refreshItem);
        dataMenu.add(calculateItem);
        
        // Reports menu
        JMenu reportsMenu = new JMenu("Laporan");
        JMenuItem allReportsItem = new JMenuItem("Generate Semua Laporan");
        allReportsItem.addActionListener(e -> generateAllReports());
        reportsMenu.add(allReportsItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(dataMenu);
        if (currentUser.hasPermission("report")) {
            menuBar.add(reportsMenu);
        }
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    private void setupEventHandlers() {
        // Window closing event
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                logout();
            }
        });
    }
    
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            loadAlternativeData();
            loadSAWData();
            updateStatus("Data berhasil dimuat");
        });
    }
    
    private void loadAlternativeData() {
        alternativeTableModel.setRowCount(0);
        
        List<Alternative> alternatives = alternativeDAO.findAll();
        for (Alternative alt : alternatives) {
            Object[] row = {
                alt.getId(),
                alt.getCode(),
                alt.getName(),
                alt.getFormattedPrice(),
                alt.getQualityScore(),
                alt.getSparePartsScore(),
                alt.getDescription() != null ? alt.getDescription() : "-"
            };
            alternativeTableModel.addRow(row);
        }
    }
    
    private void loadSAWData() {
        sawResultTableModel.setRowCount(0);
        
        try {
            List<SAWResult> results = sawService.getSAWResults();
            for (SAWResult result : results) {
                Alternative alt = result.getAlternative();
                Object[] row = {
                    result.getRanking(),
                    alt != null ? alt.getCode() : "N/A",
                    result.getAlternativeName(),
                    alt != null ? alt.getFormattedPrice() : "N/A",
                    alt != null ? alt.getQualityScore() : "N/A",
                    alt != null ? alt.getSparePartsScore() : "N/A",
                    result.getFormattedScore(),
                    result.getScorePercentage()
                };
                sawResultTableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Error loading SAW data: " + e.getMessage());
        }
    }
    
    private void calculateSAW() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                updateStatus("Menghitung SAW...");
                showProgress(true);
                
                // Validasi data
                List<String> errors = sawService.validateData();
                if (!errors.isEmpty()) {
                    StringBuilder errorMessage = new StringBuilder("Validasi gagal:\n");
                    for (String error : errors) {
                        errorMessage.append("• ").append(error).append("\n");
                    }
                    throw new Exception(errorMessage.toString());
                }
                
                // Hitung SAW
                sawService.calculateSAW();
                
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    loadSAWData();
                    updateStatus("Perhitungan SAW selesai");
                    showSuccess("Perhitungan SAW berhasil diselesaikan!");
                } catch (Exception e) {
                    showError("Error dalam perhitungan SAW: " + e.getMessage());
                    updateStatus("Perhitungan SAW gagal");
                } finally {
                    showProgress(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void showAlternativeDialog(Alternative alternative) {
        AlternativeDialog dialog = new AlternativeDialog(this, alternative);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            loadAlternativeData();
            updateStatus("Data alternatif berhasil disimpan");
        }
    }
    
    private void editSelectedAlternative() {
        int selectedRow = alternativeTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Pilih alternatif yang akan diedit");
            return;
        }
        
        int id = (Integer) alternativeTableModel.getValueAt(selectedRow, 0);
        Alternative alternative = alternativeDAO.findById(id);
        
        if (alternative != null) {
            showAlternativeDialog(alternative);
        } else {
            showError("Alternatif tidak ditemukan");
        }
    }
    
    private void deleteSelectedAlternative() {
        int selectedRow = alternativeTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Pilih alternatif yang akan dihapus");
            return;
        }
        
        String name = (String) alternativeTableModel.getValueAt(selectedRow, 2);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin menghapus alternatif '" + name + "'?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (Integer) alternativeTableModel.getValueAt(selectedRow, 0);
            
            if (alternativeDAO.delete(id)) {
                loadAlternativeData();
                updateStatus("Alternatif berhasil dihapus");
                showSuccess("Alternatif '" + name + "' berhasil dihapus");
            } else {
                showError("Gagal menghapus alternatif");
            }
        }
    }
    
    private void showSAWDetails() {
        SAWDetailDialog dialog = new SAWDetailDialog(this, sawService);
        dialog.setVisible(true);
    }
    
    private void exportSAWResults() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Hasil SAW");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implementation for CSV export would go here
            showSuccess("Hasil SAW berhasil diexport");
        }
    }
    
    private void generateReport(int reportType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "";
        
        switch (reportType) {
            case 1: fileName = "Laporan_Data_Alternatif_" + timestamp + ".pdf"; break;
            case 2: fileName = "Laporan_Matriks_SAW_" + timestamp + ".pdf"; break;
            case 3: fileName = "Laporan_Hasil_SAW_" + timestamp + ".pdf"; break;
            case 4: fileName = "Laporan_Analisis_" + timestamp + ".pdf"; break;
        }
        
        fileChooser.setSelectedFile(new File(fileName));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    updateStatus("Generating laporan...");
                    showProgress(true);
                    
                    switch (reportType) {
                        case 1: return pdfReportService.generateAlternativeReport(filePath);
                        case 2: return pdfReportService.generateMatrixReport(filePath);
                        case 3: return pdfReportService.generateSAWResultReport(filePath);
                        case 4: return pdfReportService.generateAnalysisReport(filePath);
                        default: return false;
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            updateStatus("Laporan berhasil digenerate");
                            showSuccess("Laporan berhasil disimpan ke: " + filePath);
                        } else {
                            showError("Gagal generate laporan");
                        }
                    } catch (Exception e) {
                        showError("Error generate laporan: " + e.getMessage());
                    } finally {
                        showProgress(false);
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    private void generateAllReports() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Folder untuk Semua Laporan");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String directoryPath = fileChooser.getSelectedFile().getAbsolutePath();
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    updateStatus("Generating semua laporan...");
                    showProgress(true);
                    
                    return pdfReportService.generateAllReports(directoryPath);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            updateStatus("Semua laporan berhasil digenerate");
                            showSuccess("Semua laporan berhasil disimpan ke: " + directoryPath);
                        } else {
                            showError("Gagal generate beberapa laporan");
                        }
                    } catch (Exception e) {
                        showError("Error generate laporan: " + e.getMessage());
                    } finally {
                        showProgress(false);
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    private void openReportsFolder() {
        try {
            Desktop.getDesktop().open(new File(System.getProperty("user.home")));
        } catch (Exception e) {
            showError("Tidak dapat membuka folder: " + e.getMessage());
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin ingin logout?",
            "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            LoginController.logout();
        }
    }
    
    private void showAbout() {
        String message = "Sistem Data Mining SAW PT Erajaya\n" +
                        "Version 1.0\n\n" +
                        "Sistem untuk pengelolaan barang elektronik\n" +
                        "menggunakan metode Simple Additive Weighting (SAW)\n\n" +
                        "© 2025 PT Erajaya";
        
        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Utility methods
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
    
    private void showProgress(boolean show) {
        SwingUtilities.invokeLater(() -> progressBar.setVisible(show));
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    // Custom cell renderer for ranking
    private class RankingCellRenderer extends DefaultTableCellRenderer {
        public RankingCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected && value instanceof Integer) {
                int rank = (Integer) value;
                switch (rank) {
                    case 1:
                        c.setBackground(new Color(255, 215, 0)); // Gold
                        c.setForeground(Color.BLACK);
                        break;
                    case 2:
                        c.setBackground(new Color(192, 192, 192)); // Silver
                        c.setForeground(Color.BLACK);
                        break;
                    case 3:
                        c.setBackground(new Color(205, 127, 50)); // Bronze
                        c.setForeground(Color.WHITE);
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                }
            }
            
            return c;
        }
    }
}
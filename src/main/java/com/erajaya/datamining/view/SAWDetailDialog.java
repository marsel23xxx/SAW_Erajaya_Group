package com.erajaya.datamining.view;

import com.erajaya.datamining.service.SAWService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Dialog untuk menampilkan detail perhitungan SAW
 */
public class SAWDetailDialog extends JDialog {
    
    private SAWService sawService;
    private JTabbedPane tabbedPane;
    
    // Tables
    private JTable criteriaTable;
    private JTable decisionTable;
    private JTable normalizedTable;
    private JTable resultTable;
    
    // Table models
    private DefaultTableModel criteriaTableModel;
    private DefaultTableModel decisionTableModel;
    private DefaultTableModel normalizedTableModel;
    private DefaultTableModel resultTableModel;
    
    public SAWDetailDialog(Frame parent, SAWService sawService) {
        super(parent, "Detail Perhitungan SAW", true);
        this.sawService = sawService;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // Initialize tables
        initializeCriteriaTable();
        initializeDecisionTable();
        initializeNormalizedTable();
        initializeResultTable();
        
        // Add tabs
        tabbedPane.addTab("📋 Kriteria & Bobot", createScrollPane(criteriaTable));
        tabbedPane.addTab("📊 Matriks Keputusan", createScrollPane(decisionTable));
        tabbedPane.addTab("⚖️ Matriks Normalisasi", createScrollPane(normalizedTable));
        tabbedPane.addTab("🏆 Hasil Akhir", createScrollPane(resultTable));
    }
    
    private void initializeCriteriaTable() {
        String[] columns = {"No", "Kriteria", "Bobot", "Bobot (%)", "Tipe", "Deskripsi"};
        criteriaTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        criteriaTable = new JTable(criteriaTableModel);
        criteriaTable.setRowHeight(25);
        criteriaTable.getColumnModel().getColumn(0).setMaxWidth(50);
        criteriaTable.getColumnModel().getColumn(2).setMaxWidth(80);
        criteriaTable.getColumnModel().getColumn(3).setMaxWidth(80);
        criteriaTable.getColumnModel().getColumn(4).setMaxWidth(80);
        
        // Center alignment for numbers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        criteriaTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        criteriaTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        criteriaTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        criteriaTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }
    
    private void initializeDecisionTable() {
        String[] columns = {"Alternatif", "Harga", "Kualitas", "Suku Cadang"};
        decisionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        decisionTable = new JTable(decisionTableModel);
        decisionTable.setRowHeight(25);
        
        // Center alignment for numbers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < decisionTable.getColumnCount(); i++) {
            decisionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void initializeNormalizedTable() {
        String[] columns = {"Alternatif", "Harga (N)", "Kualitas (N)", "Suku Cadang (N)"};
        normalizedTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        normalizedTable = new JTable(normalizedTableModel);
        normalizedTable.setRowHeight(25);
        
        // Center alignment for numbers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < normalizedTable.getColumnCount(); i++) {
            normalizedTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void initializeResultTable() {
        String[] columns = {"Rank", "Alternatif", "Kode", "Total Skor", "Persentase", "Keterangan"};
        resultTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultTable = new JTable(resultTableModel);
        resultTable.setRowHeight(25);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(60);
        resultTable.getColumnModel().getColumn(2).setMaxWidth(80);
        resultTable.getColumnModel().getColumn(3).setMaxWidth(100);
        resultTable.getColumnModel().getColumn(4).setMaxWidth(100);
        
        // Custom renderer for ranking
        resultTable.getColumnModel().getColumn(0).setCellRenderer(new RankingCellRenderer());
        
        // Center alignment for numbers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        resultTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }
    
    private JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return scrollPane;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        
        JLabel titleLabel = new JLabel("DETAIL PERHITUNGAN SAW", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Main content
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Tutup");
        JButton printButton = new JButton("Print Detail");
        
        closeButton.addActionListener(e -> dispose());
        printButton.addActionListener(e -> printDetails());
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Escape key untuk close
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void loadData() {
        try {
            loadCriteriaData();
            loadDecisionMatrixData();
            loadNormalizedMatrixData();
            loadResultData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadCriteriaData() {
        criteriaTableModel.setRowCount(0);
        
        Map<String, Object> criteriaInfo = sawService.getCriteriaInfo();
        String[] names = (String[]) criteriaInfo.get("names");
        double[] weights = (double[]) criteriaInfo.get("weights");
        String[] types = (String[]) criteriaInfo.get("types");
        String[] descriptions = (String[]) criteriaInfo.get("descriptions");
        
        for (int i = 0; i < names.length; i++) {
            Object[] row = {
                i + 1,
                names[i],
                String.format("%.2f", weights[i]),
                String.format("%.1f%%", weights[i] * 100),
                types[i],
                descriptions[i]
            };
            criteriaTableModel.addRow(row);
        }
    }
    
    private void loadDecisionMatrixData() {
        decisionTableModel.setRowCount(0);
        
        String[][] decisionMatrix = sawService.getDecisionMatrixDisplay();
        for (String[] row : decisionMatrix) {
            decisionTableModel.addRow(row);
        }
    }
    
    private void loadNormalizedMatrixData() {
        normalizedTableModel.setRowCount(0);
        
        String[][] normalizedMatrix = sawService.getNormalizedMatrixDisplay();
        for (String[] row : normalizedMatrix) {
            normalizedTableModel.addRow(row);
        }
    }
    
    private void loadResultData() {
        resultTableModel.setRowCount(0);
        
        try {
            var results = sawService.getSAWResults();
            for (var result : results) {
                String keterangan = "";
                switch (result.getRanking()) {
                    case 1: keterangan = "Terbaik"; break;
                    case 2: keterangan = "Sangat Baik"; break;
                    case 3: keterangan = "Baik"; break;
                    default: keterangan = "Cukup"; break;
                }
                
                Object[] row = {
                    result.getRanking(),
                    result.getAlternativeName(),
                    result.getAlternativeCode(),
                    result.getFormattedScore(),
                    result.getScorePercentage(),
                    keterangan
                };
                resultTableModel.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error loading SAW results: " + e.getMessage());
        }
    }
    
    private void printDetails() {
        try {
            // Simple print functionality
            JTextArea textArea = new JTextArea();
            textArea.setText(generateDetailReport());
            textArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error printing: " + e.getMessage(), 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateDetailReport() {
        StringBuilder report = new StringBuilder();
        report.append("DETAIL PERHITUNGAN SAW\n");
        report.append("========================\n\n");
        
        // Kriteria
        report.append("KRITERIA DAN BOBOT:\n");
        Map<String, Object> criteriaInfo = sawService.getCriteriaInfo();
        String[] names = (String[]) criteriaInfo.get("names");
        double[] weights = (double[]) criteriaInfo.get("weights");
        String[] types = (String[]) criteriaInfo.get("types");
        
        for (int i = 0; i < names.length; i++) {
            report.append(String.format("%d. %s: %.2f (%.1f%%) - %s\n", 
                i + 1, names[i], weights[i], weights[i] * 100, types[i]));
        }
        
        report.append("\nMATRIKS KEPUTUSAN:\n");
        String[][] decisionMatrix = sawService.getDecisionMatrixDisplay();
        for (String[] row : decisionMatrix) {
            report.append(String.join("\t", row)).append("\n");
        }
        
        report.append("\nMATRIKS NORMALISASI:\n");
        String[][] normalizedMatrix = sawService.getNormalizedMatrixDisplay();
        for (String[] row : normalizedMatrix) {
            report.append(String.join("\t", row)).append("\n");
        }
        
        return report.toString();
    }
    
    // Custom cell renderer for ranking
    private class RankingCellRenderer extends DefaultTableCellRenderer {
        public RankingCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
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
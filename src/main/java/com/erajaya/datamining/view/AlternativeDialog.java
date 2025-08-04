package com.erajaya.datamining.view;

import com.erajaya.datamining.dao.AlternativeDAO;
import com.erajaya.datamining.model.Alternative;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * Dialog untuk form tambah/edit alternatif
 */
public class AlternativeDialog extends JDialog {
    
    private Alternative alternative;
    private AlternativeDAO alternativeDAO;
    private boolean confirmed = false;
    
    // Form components
    private JTextField codeField;
    private JTextField nameField;
    private JTextField priceField;
    private JSpinner qualitySpinner;
    private JSpinner sparePartsSpinner;
    private JTextArea descriptionArea;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public AlternativeDialog(Frame parent, Alternative alternative) {
        super(parent, "Form Alternatif", true);
        this.alternative = alternative;
        this.alternativeDAO = new AlternativeDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
        
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeComponents() {
        // Text fields
        codeField = new JTextField(20);
        nameField = new JTextField(20);
        priceField = new JTextField(20);
        
        // Spinners for scores (1-100)
        qualitySpinner = new JSpinner(new SpinnerNumberModel(50, 1, 100, 1));
        sparePartsSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 100, 1));
        
        // Description area
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Buttons
        saveButton = new JButton("💾 Simpan");
        cancelButton = new JButton("❌ Batal");
        
        // Styling
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Kode
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Kode:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(codeField, gbc);
        
        // Nama
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Nama Produk:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(nameField, gbc);
        
        // Harga
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Harga (Rp):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(priceField, gbc);
        
        // Kualitas
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Skor Kualitas (1-100):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(qualitySpinner, gbc);
        
        // Suku Cadang
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Skor Suku Cadang (1-100):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(sparePartsSpinner, gbc);
        
        // Deskripsi
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Deskripsi:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(descScrollPane, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Title
        String title = alternative == null ? "Tambah Alternatif Baru" : "Edit Alternatif";
        setTitle(title);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAlternative();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter key untuk save
        getRootPane().setDefaultButton(saveButton);
        
        // Escape key untuk cancel
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void populateFields() {
        if (alternative != null) {
            codeField.setText(alternative.getCode());
            nameField.setText(alternative.getName());
            priceField.setText(alternative.getPrice().toString());
            qualitySpinner.setValue(alternative.getQualityScore());
            sparePartsSpinner.setValue(alternative.getSparePartsScore());
            
            if (alternative.getDescription() != null) {
                descriptionArea.setText(alternative.getDescription());
            }
        }
        
        // Focus pada field pertama
        SwingUtilities.invokeLater(() -> codeField.requestFocus());
    }
    
    private void saveAlternative() {
        try {
            // Validasi input
            if (!validateInput()) {
                return;
            }
            
            // Create atau update alternative
            if (alternative == null) {
                alternative = new Alternative();
            }
            
            // Set values
            alternative.setCode(codeField.getText().trim());
            alternative.setName(nameField.getText().trim());
            alternative.setPrice(new BigDecimal(priceField.getText().trim()));
            alternative.setQualityScore((Integer) qualitySpinner.getValue());
            alternative.setSparePartsScore((Integer) sparePartsSpinner.getValue());
            alternative.setDescription(descriptionArea.getText().trim());
            
            // Save to database
            boolean success;
            if (alternative.getId() == 0) {
                // New alternative
                success = alternativeDAO.save(alternative);
            } else {
                // Update existing
                success = alternativeDAO.update(alternative);
            }
            
            if (success) {
                confirmed = true;
                showSuccess("Data alternatif berhasil disimpan!");
                dispose();
            } else {
                showError("Gagal menyimpan data alternatif!");
            }
            
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        // Validasi kode
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            showError("Kode alternatif harus diisi!");
            codeField.requestFocus();
            return false;
        }
        
        // Cek kode unik
        int excludeId = alternative != null ? alternative.getId() : -1;
        if (alternativeDAO.isCodeExists(code, excludeId)) {
            showError("Kode alternatif '" + code + "' sudah digunakan!");
            codeField.requestFocus();
            return false;
        }
        
        // Validasi nama
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Nama produk harus diisi!");
            nameField.requestFocus();
            return false;
        }
        
        // Validasi harga
        String priceText = priceField.getText().trim();
        if (priceText.isEmpty()) {
            showError("Harga harus diisi!");
            priceField.requestFocus();
            return false;
        }
        
        try {
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Harga harus lebih besar dari 0!");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Format harga tidak valid! Gunakan angka saja.");
            priceField.requestFocus();
            return false;
        }
        
        // Validasi skor (sudah dikontrol oleh spinner, tapi tetap cek)
        int qualityScore = (Integer) qualitySpinner.getValue();
        int sparePartsScore = (Integer) sparePartsSpinner.getValue();
        
        if (qualityScore < 1 || qualityScore > 100) {
            showError("Skor kualitas harus antara 1-100!");
            qualitySpinner.requestFocus();
            return false;
        }
        
        if (sparePartsScore < 1 || sparePartsScore > 100) {
            showError("Skor suku cadang harus antara 1-100!");
            sparePartsSpinner.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Alternative getAlternative() {
        return alternative;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
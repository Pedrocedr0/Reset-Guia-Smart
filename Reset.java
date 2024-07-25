import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Reset {
    private JFrame frame;
    private JLabel labelInstructions;
    private JLabel labelSerie;
    private JLabel labelOs;
    private JTextField textFieldSerie;
    private JTextField textFieldOs;
    private JButton queryButton;
    private JButton resetButton;
    private JTextArea resultsTextArea;
    private JLabel logoLabel;

    private String databaseName = "dataname_base";
    private String connectionString = "jdbc:sqlserver://Ip_do_banco;databaseName=" + databaseName + ";encrypt=false";
    private String username = "User_Banco";
    private String password = "password";

    public Reset() {
        frame = new JFrame("Reset Guia");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        labelInstructions = new JLabel("Insira os valores para:");
        labelInstructions.setFont(new Font("Arial", Font.BOLD, 14));
        labelSerie = new JLabel("Serie/Os: ");
        labelOs = new JLabel("");
        textFieldSerie = new JTextField(5);
        textFieldOs = new JTextField(5);
        queryButton = new JButton("Consultar");
        resetButton = new JButton("Resetar");
        resultsTextArea = new JTextArea(10, 30);
        resultsTextArea.setEditable(false);
        ImageIcon icon = new ImageIcon("C:\\Users\\joao.almeida\\IdeaProjects1\\Projeto\\src\\ResetGuia\\src\\logo.png");
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newImage);
        logoLabel = new JLabel(newIcon);


        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 3, 0);
        panel.add(logoLabel, c);
        c.gridy++;
        panel.add(labelInstructions, c);
        c.gridy++;
        panel.add(labelSerie, c);
        c.gridx = 0;
        c.gridy = 2;
        panel.add(labelOs, c);
        c.gridx = 1;
        c.gridy = 4;
        panel.add(textFieldSerie, c);
        c.gridy++;
        panel.add(textFieldOs, c);
        c.gridy++;
        panel.add(queryButton, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        panel.add(new JScrollPane(resultsTextArea), c);

        c.gridy++;
        panel.add(resetButton, c);

        queryButton.setBackground(new Color(77, 182, 110));
        resetButton.setBackground(new Color(54, 152, 215));

        queryButton.setForeground(Color.WHITE);
        resetButton.setForeground(Color.WHITE);

        queryButton.setFocusPainted(false);
        resetButton.setFocusPainted(false);
        queryButton.setFont(new Font("Verdana", Font.PLAIN, 14));
        resetButton.setFont(new Font("Verdana", Font.PLAIN, 14));

        queryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetData();
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void executeQuery() {
        String serie = textFieldSerie.getText();
        String os = textFieldOs.getText();

        try (Connection connection = DriverManager.getConnection(connectionString, username, password)) {
            String sql = "SELECT smm_tiss_stat_aut AS Status, smm_tiss_guia_operadora AS 'Guia Operad.', smm_ctle_cnv AS 'Guia Conv.', smm_senha AS senha " +
                    "FROM smm WHERE smm_osm_serie = ? AND smm_osm = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, serie);
            stmt.setString(2, os);

            ResultSet results = stmt.executeQuery();

            StringBuilder tableBuilder = new StringBuilder();
            tableBuilder.append("Status:\tGuia Operad.:\tGuia Conv.:\tSenha\n");

            while (results.next()) {
                String status = results.getString("Status");
                String guiaOperad = results.getString("Guia Operad.");
                String guiaConv = results.getString("Guia Conv.");
                String senha = results.getString("senha");

                tableBuilder.append(status).append("\t").append(guiaOperad).append("\t").append(guiaConv).append("\t").append(senha).append("\n");
            }

            resultsTextArea.setText(tableBuilder.toString());
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("Erro ao executar a consulta: " + ex.getMessage());
            resultsTextArea.setText("Erro: " + ex.getMessage());
        }
    }

    private void resetData() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionString, username, password);
            String sql = "UPDATE smm SET smm_tiss_stat_aut = NULL, smm_tiss_guia_operadora = NULL, smm_ctle_cnv = NULL, smm_senha = NULL " +
                    "WHERE smm_osm_serie = ? AND smm_osm = ?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, textFieldSerie.getText());
            stmt.setString(2, textFieldOs.getText());

            int rowsAffected = stmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Resetado " + rowsAffected + " linhas.", "Reset Concluído", JOptionPane.INFORMATION_MESSAGE);
            stmt.close();
        } catch (SQLException ex) {
            System.out.println("Erro ao resetar os dados: " + ex.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println("Erro ao fechar a conexão: " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Reset();
            }
        });
    }
}

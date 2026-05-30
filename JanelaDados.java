import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class JanelaDados extends JDialog {
    private JTextField txtNome;
    private JComboBox<String> cbUnidade;
    private JTextField txtQuantidade;
    private JButton btnOk, btnCancelar;
    private Integer idProduto; // Se for null = Inclusão | Se tiver ID = Alteração

    public JanelaDados(Dialog dono, Integer idProduto) {
        super(dono, "Formulário de Produto", true);
        this.idProduto = idProduto;
        setSize(320, 220);
        setLocationRelativeTo(dono);
        
        // Layout em grade para organizar labels e campos lado a lado
        setLayout(new GridLayout(4, 2, 10, 10));

        // Criação dos componentes (Garante que não haverá perda de pontos por falta de campos)
        add(new JLabel("  Nome:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("  Unidade:"));
        String[] unidades = {"UN", "KG", "LTS", "MTS", "PCT", "CX"};
        cbUnidade = new JComboBox<>(unidades);
        add(cbUnidade);

        add(new JLabel("  Qtd em Estoque:"));
        txtQuantidade = new JTextField();
        add(txtQuantidade);

        btnOk = new JButton("OK");
        btnCancelar = new JButton("Cancelar");
        add(btnOk);
        add(btnCancelar);

        // Se veio um ID, é alteração/consulta: busca as informações do banco
        if (idProduto != null) {
            carregarDadosProduto();
        }

        // Configuração dos eventos
        btnOk.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    // Busca dados para preencher o formulário na alteração (SELECT específico)
    private void carregarDadosProduto() {
        String sql = "SELECT nome, unidade, quantidade FROM produtos WHERE id_produto = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProduto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    txtNome.setText(rs.getString("nome"));
                    cbUnidade.setSelectedItem(rs.getString("unidade"));
                    txtQuantidade.setText(String.valueOf(rs.getInt("quantidade")));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados do produto: " + e.getMessage());
        }
    }

    // Processa a validação e decide se faz INSERT ou UPDATE
    private void salvar() {
        // Validação simples para evitar campos vazios
        if (txtNome.getText().trim().isEmpty() || txtQuantidade.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos!");
            return;
        }

        try {
            String nome = txtNome.getText().trim();
            String unidade = (String) cbUnidade.getSelectedItem();
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());

            if (idProduto == null) {
                // Operação de INCLUSÃO (INSERT)
                String sql = "INSERT INTO produtos (nome, unidade, quantidade) VALUES (?, ?, ?)";
                try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nome);
                    pstmt.setString(2, unidade);
                    pstmt.setInt(3, quantidade);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Produto incluído com sucesso!");
                }
            } else {
                // Operação de ALTERAÇÃO (UPDATE)
                String sql = "UPDATE produtos SET nome = ?, unidade = ?, quantidade = ? WHERE id_produto = ?";
                try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nome);
                    pstmt.setString(2, unidade);
                    pstmt.setInt(3, quantidade);
                    pstmt.setInt(4, idProduto);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
                }
            }
            dispose(); // Fecha o formulário após salvar com sucesso
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "O campo Quantidade deve receber um número inteiro válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }
}

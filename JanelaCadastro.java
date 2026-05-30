import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class JanelaCadastro extends JDialog {
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public JanelaCadastro() {
        setTitle("Gerenciamento de Produtos");
        setSize(600, 400);
        setModal(true); // Bloqueia a janela de trás para foco total nesta
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Configuração da Tabela para listar dados
        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Unidade", "Qtd Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que o usuário edite direto na tabela sem abrir a janela de dados
            }
        };
        
        tabela = new JTable(modeloTabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Painel inferior de Botões
        JPanel painelBotoes = new JPanel();
        JButton btnIncluir = new JButton("Incluir");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");

        painelBotoes.add(btnIncluir);
        painelBotoes.add(btnAlterar);
        painelBotoes.add(btnExcluir);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações dos Botões
        btnIncluir.addActionListener(e -> abrirFormulario(null));
        
        btnAlterar.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada != -1) {
                int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                abrirFormulario(id);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela para alterar.");
            }
        });

        btnExcluir.addActionListener(e -> excluirProduto());

        // Carrega os dados assim que a tela abre
        atualizarTabela();
    }

    // Executa a Seleção no Banco de Dados (SELECT)
    public void atualizarTabela() {
        modeloTabela.setRowCount(0); 
        String sql = "SELECT * FROM produtos";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("id_produto"),
                        rs.getString("nome"),
                        rs.getString("unidade"),
                        rs.getInt("quantidade")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void abrirFormulario(Integer id) {
        // Abre a Janela 3 passando o ID (se for alteração) ou null (se for inclusão)
        JanelaDados form = new JanelaDados(this, id);
        form.setVisible(true);
        atualizarTabela(); // Recarrega a tabela sempre que fechar a janela 3
    }

    // Executa a Exclusão no Banco de Dados (DELETE)
    private void excluirProduto() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto para excluir.");
            return;
        }
        
        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este produto?", "Confirmação", JOptionPane.YES_NO_OPTION);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM produtos WHERE id_produto = ?";
            try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                atualizarTabela();
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage());
            }
        }
    }
}

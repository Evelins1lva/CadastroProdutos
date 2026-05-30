import javax.swing.*;
import java.awt.FlowLayout;

public class JanelaPrincipal extends JFrame {
    public JanelaPrincipal() {
        setTitle("Sistema de Estoque - Menu Principal");
        setSize(350, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40));

        JButton btnCadastro = new JButton("Abrir Cadastro de Produtos");
        btnCadastro.setFocusable(false);
        add(btnCadastro);

        // Ação do botão: Abre a Janela de Listagem (Janela 2)
        btnCadastro.addActionListener(e -> {
            JanelaCadastro janelaCad = new JanelaCadastro();
            janelaCad.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Garante a criação do banco/tabela antes de abrir a tela
        Database.criarTabela();
        
        SwingUtilities.invokeLater(() -> {
            new JanelaPrincipal().setVisible(true);
        });
    }
}

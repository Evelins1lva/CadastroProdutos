import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:produtos.db";

    public static Connection getConnection() throws Exception {

        // Carrega o driver JDBC do SQLite
        Class.forName("org.sqlite.JDBC");

        // Faz a conexão com o banco
        return DriverManager.getConnection(URL);
    }

    public static void criarTabela() {

        String sql = "CREATE TABLE IF NOT EXISTS produtos (" +
                     "id_produto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "nome TEXT NOT NULL, " +
                     "unidade TEXT NOT NULL, " +
                     "quantidade INTEGER NOT NULL)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

            System.out.println("Tabela criada com sucesso!");

        } catch (Exception e) {

            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }
}
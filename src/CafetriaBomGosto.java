import java.sql.*;
import java.time.LocalDate;

/**
 * ==============================================================
 *  SISTEMA DE CONTROLE DE VENDAS - CAFETERIA BOMGOSTO
 * ==============================================================
 *
 * Objetivo:
 * Simular um sistema de banco de dados relacional com Java e SQL
 * que responda às 5 questões propostas sobre controle de vendas.
 *
 * Linguagem: Java (JDBC)
 * Banco: PostgreSQL (localhost)
 *
 * --------------------------------------------------------------
 * Estrutura:
 * - Tabelas: Cardapio, Comanda, ItemComanda
 * - Inserção de dados fictícios
 * - Execução das 5 consultas acadêmicas solicitadas
 * --------------------------------------------------------------
 */

public class CafeteriaBomGosto {

    public static void main(String[] args) {
        // ==========================================================
        // BLOCO 1 – CONFIGURAÇÃO DE CONEXÃO COM O BANCO LOCAL
        // ==========================================================
        // Aqui definimos o endereço do banco, usuário e senha.
        // Você pode alterar para MySQL, se preferir.
        String url = "jdbc:postgresql://localhost:5432/bomgosto_db";
        String user = "postgres";
        String password = "123456";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conectado ao banco de dados localhost.\n");

            // ==========================================================
            // BLOCO 2 – CRIAÇÃO DAS TABELAS DO BANCO
            // ==========================================================
            criarTabelas(conn);

            // ==========================================================
            // BLOCO 3 – INSERÇÃO DE DADOS FICTÍCIOS
            // ==========================================================
            inserirDados(conn);

            // ==========================================================
            // BLOCO 4 – EXECUÇÃO DAS CONSULTAS (QUESTÕES 1 A 5)
            // ==========================================================
            listarCardapio(conn);                   // Questão 1
            listarComandasEItens(conn);             // Questão 2
            listarComandasComValorTotal(conn);      // Questão 3
            listarComandasMaisDeUmTipoCafe(conn);   // Questão 4
            listarFaturamentoPorData(conn);         // Questão 5

            System.out.println("\n Execução finalizada com sucesso!");

        } catch (SQLException e) {
            System.err.println(" Erro de conexão ou execução: " + e.getMessage());
        }
    }

    // ==========================================================
    // MÉTODO 1 – CRIAÇÃO DAS TABELAS
    // ==========================================================
    // Este bloco recria toda a estrutura do banco de dados,
    // garantindo que as tabelas fiquem limpas para o teste.
    private static void criarTabelas(Connection conn) throws SQLException {
        String dropAll = """
                DROP TABLE IF EXISTS ItemComanda;
                DROP TABLE IF EXISTS Comanda;
                DROP TABLE IF EXISTS Cardapio;
                """;

        String createCardapio = """
                CREATE TABLE Cardapio (
                    codigo SERIAL PRIMARY KEY,
                    nome VARCHAR(100) UNIQUE NOT NULL,
                    descricao TEXT,
                    preco_unitario DECIMAL(10,2) NOT NULL
                );
                """;

        String createComanda = """
                CREATE TABLE Comanda (
                    codigo SERIAL PRIMARY KEY,
                    data DATE NOT NULL,
                    mesa INT NOT NULL,
                    nome_cliente VARCHAR(100) NOT NULL
                );
                """;

        String createItemComanda = """
                CREATE TABLE ItemComanda (
                    codigo_comanda INT,
                    codigo_cardapio INT,
                    quantidade INT NOT NULL,
                    PRIMARY KEY (codigo_comanda, codigo_cardapio),
                    FOREIGN KEY (codigo_comanda) REFERENCES Comanda(codigo),
                    FOREIGN KEY (codigo_cardapio) REFERENCES Cardapio(codigo)
                );
                """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(dropAll);
            st.executeUpdate(createCardapio);
            st.executeUpdate(createComanda);
            st.executeUpdate(createItemComanda);
        }

        System.out.println(" Tabelas criadas com sucesso.\n");
    }

    // ==========================================================
    // MÉTODO 2 – INSERÇÃO DE DADOS FICTÍCIOS
    // ==========================================================
    // Este bloco insere cafés no cardápio, cria comandas e itens,
    // simulando vendas reais realizadas na cafeteria.
    private static void inserirDados(Connection conn) throws SQLException {
        // Cafés disponíveis no cardápio
        String insertCardapio = """
                INSERT INTO Cardapio (nome, descricao, preco_unitario)
                VALUES 
                    ('Espresso', 'Café espresso puro e intenso', 6.50),
                    ('Cappuccino', 'Café com leite vaporizado e espuma cremosa', 8.00),
                    ('Mocha', 'Café com chocolate e leite vaporizado', 9.50),
                    ('Latte', 'Café suave com leite vaporizado', 7.50);
                """;

        // Comandas abertas em datas e mesas diferentes
        String insertComandas = """
                INSERT INTO Comanda (data, mesa, nome_cliente)
                VALUES 
                    ('2025-10-24', 3, 'Carlos Almeida'),
                    ('2025-10-25', 5, 'Maria Silva'),
                    ('2025-10-25', 7, 'João Pereira');
                """;

        // Itens de cada comanda: cafés e quantidades
        String insertItens = """
                INSERT INTO ItemComanda (codigo_comanda, codigo_cardapio, quantidade)
                VALUES 
                    (1, 1, 2),  -- Comanda 1: 2 Espressos
                    (1, 2, 1),  -- Comanda 1: 1 Cappuccino
                    (2, 3, 1),  -- Comanda 2: 1 Mocha
                    (2, 4, 2),  -- Comanda 2: 2 Lattes
                    (3, 1, 1);  -- Comanda 3: 1 Espresso
                """;

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(insertCardapio);
            st.executeUpdate(insertComandas);
            st.executeUpdate(insertItens);
        }

        System.out.println("📦 Dados fictícios inseridos com sucesso.\n");
    }

    // ==========================================================
    // QUESTÃO 1 – LISTAR CARDÁPIO ORDENADO POR NOME
    // ==========================================================
    // Mostra o menu completo da cafeteria, ordenando alfabeticamente.
    private static void listarCardapio(Connection conn) throws SQLException {
        System.out.println("1️⃣  LISTAGEM DO CARDÁPIO (ordenado por nome):");

        String sql = """
                SELECT codigo, nome, descricao, preco_unitario
                FROM Cardapio
                ORDER BY nome;
                """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("Código: %d | %s - R$ %.2f | %s%n",
                        rs.getInt("codigo"),
                        rs.getString("nome"),
                        rs.getDouble("preco_unitario"),
                        rs.getString("descricao"));
            }
        }

        System.out.println("\n----------------------------------------------\n");
    }

    // ==========================================================
    // QUESTÃO 2 – COMANDAS E ITENS
    // ==========================================================
    // Mostra cada comanda (com cliente, mesa e data)
    // e seus respectivos cafés vendidos com preços e totais.
    private static void listarComandasEItens(Connection conn) throws SQLException {
        System.out.println("2️⃣  COMANDAS E ITENS (ordenadas por data, código e nome do café):");

        String sql = """
                SELECT
                    c.codigo AS codigo_comanda,
                    c.data,
                    c.mesa,
                    c.nome_cliente,
                    ca.nome AS nome_cafe,
                    ca.descricao,
                    i.quantidade,
                    ca.preco_unitario,
                    (i.quantidade * ca.preco_unitario) AS preco_total_cafe
                FROM Comanda c
                JOIN ItemComanda i ON c.codigo = i.codigo_comanda
                JOIN Cardapio ca ON i.codigo_cardapio = ca.codigo
                ORDER BY c.data, c.codigo, ca.nome;
                """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "Comanda %d | Data: %s | Mesa: %d | Cliente: %s | Café: %s | Qtd: %d | Unit: R$ %.2f | Total: R$ %.2f%n",
                        rs.getInt("codigo_comanda"),
                        rs.getDate("data"),
                        rs.getInt("mesa"),
                        rs.getString("nome_cliente"),
                        rs.getString("nome_cafe"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco_unitario"),
                        rs.getDouble("preco_total_cafe"));
            }
        }

        System.out.println("\n----------------------------------------------\n");
    }

    // ==========================================================
    // QUESTÃO 3 – COMANDAS COM VALOR TOTAL
    // ==========================================================
    // Agrupa os itens por comanda e calcula o total de cada uma.
    private static void listarComandasComValorTotal(Connection conn) throws SQLException {
        System.out.println("3️⃣  COMANDAS COM VALOR TOTAL:");

        String sql = """
                SELECT
                    c.codigo,
                    c.data,
                    c.mesa,
                    c.nome_cliente,
                    SUM(i.quantidade * ca.preco_unitario) AS valor_total_comanda
                FROM Comanda c
                JOIN ItemComanda i ON c.codigo = i.codigo_comanda
                JOIN Cardapio ca ON i.codigo_cardapio = ca.codigo
                GROUP BY c.codigo, c.data, c.mesa, c.nome_cliente
                ORDER BY c.data;
                """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "Comanda %d | Data: %s | Mesa: %d | Cliente: %s | TOTAL R$ %.2f%n",
                        rs.getInt("codigo"),
                        rs.getDate("data"),
                        rs.getInt("mesa"),
                        rs.getString("nome_cliente"),
                        rs.getDouble("valor_total_comanda"));
            }
        }

        System.out.println("\n----------------------------------------------\n");
    }

    // ==========================================================
    // QUESTÃO 4 – COMANDAS COM MAIS DE UM TIPO DE CAFÉ
    // ==========================================================
    // Exibe apenas as comandas que venderam mais de um tipo de café.
    private static void listarComandasMaisDeUmTipoCafe(Connection conn) throws SQLException {
        System.out.println("4️⃣  COMANDAS COM MAIS DE UM TIPO DE CAFÉ:");

        String sql = """
                SELECT
                    c.codigo,
                    c.data,
                    c.mesa,
                    c.nome_cliente,
                    SUM(i.quantidade * ca.preco_unitario) AS valor_total_comanda
                FROM Comanda c
                JOIN ItemComanda i ON c.codigo = i.codigo_comanda
                JOIN Cardapio ca ON i.codigo_cardapio = ca.codigo
                GROUP BY c.codigo, c.data, c.mesa, c.nome_cliente
                HAVING COUNT(i.codigo_cardapio) > 1
                ORDER BY c.data;
                """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf(
                        "Comanda %d | Data: %s | Mesa: %d | Cliente: %s | TOTAL R$ %.2f%n",
                        rs.getInt("codigo"),
                        rs.getDate("data"),
                        rs.getInt("mesa"),
                        rs.getString("nome_cliente"),
                        rs.getDouble("valor_total_comanda"));
            }
        }

        System.out.println("\n----------------------------------------------\n");
    }

    // ==========================================================
    // QUESTÃO 5 – FATURAMENTO TOTAL POR DATA
    // ==========================================================
    // Agrupa as vendas por data e soma o faturamento total do dia.
    private static void listarFaturamentoPorData(Connection conn) throws SQLException {
        System.out.println("5️⃣  FATURAMENTO TOTAL POR DATA:");

        String sql = """
                SELECT
                    c.data,
                    SUM(i.quantidade * ca.preco_unitario) AS faturamento_total
                FROM Comanda c
                JOIN ItemComanda i ON c.codigo = i.codigo_comanda
                JOIN Cardapio ca ON i.codigo_cardapio = ca.codigo
                GROUP BY c.data
                ORDER BY c.data;
                """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("Data: %s | Faturamento total: R$ %.2f%n",
                        rs.getDate("data"),
                        rs.getDouble("faturamento_total"));
            }
        }

        System.out.println("\n----------------------------------------------\n");
    }
}

# ☕ Cafeteria BomGosto – Sistema de Controle de Vendas

Este projeto é uma simulação  em **Java com JDBC** para controle de vendas de uma cafeteria fictícia chamada **BomGosto**.

O objetivo é demonstrar a execução de **consultas SQL básicas e intermediárias** a partir de um sistema Java conectado a um banco de dados relacional (PostgreSQL ou MySQL).

---

## 🎯 **Objetivo do Projeto**

A cafeteria BomGosto controla suas vendas por meio de **comandas** que registram:
- código da comanda;
- data;
- número da mesa;
- nome do cliente;
- cafés vendidos (itens de comanda);
- cardápio de cafés (nome, descrição e preço).

O sistema realiza **5 consultas SQL principais**, que respondem às seguintes questões:

1️⃣ **Listar o cardápio** ordenado por nome.  
2️⃣ **Exibir todas as comandas e seus itens**, com valores calculados.  
3️⃣ **Listar o valor total de cada comanda.**  
4️⃣ **Listar apenas as comandas que possuem mais de um tipo de café.**  
5️⃣ **Exibir o faturamento total agrupado por data.**

---

## **Tecnologias Utilizadas**

- **Java SE 17+**
- **JDBC (Java Database Connectivity)**
- **PostgreSQL 14+** (pode ser adaptado ao MySQL facilmente)
- **Driver JDBC:**
    - PostgreSQL → `postgresql-42.7.3.jar`
    - MySQL → `mysql-connector-j-8.3.0.jar`

---

## 📂 **Estrutura de Pastas Recomendada**

```
CafeteriaBomGosto/
│
├── src/
│   └── CafeteriaBomGosto.java        # Código-fonte principal
│
├── lib/
│   ├── postgresql-42.7.3.jar         # Driver JDBC do PostgreSQL
│
└── database/
    └── bomgosto_db.sql               # Script SQL para criar e popular o banco
```

---

## 🧱 **1. Criação do Banco de Dados (PostgreSQL)**

Abra o **pgAdmin** ou terminal e execute:

```sql
CREATE DATABASE bomgosto_db;
\c bomgosto_db;

-- TABELAS
CREATE TABLE Cardapio (
    codigo SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao TEXT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL
);

CREATE TABLE Comanda (
    codigo SERIAL PRIMARY KEY,
    data DATE NOT NULL,
    mesa INT NOT NULL,
    nome_cliente VARCHAR(100) NOT NULL
);

CREATE TABLE ItemComanda (
    codigo_comanda INT REFERENCES Comanda(codigo),
    codigo_cardapio INT REFERENCES Cardapio(codigo),
    quantidade INT NOT NULL,
    PRIMARY KEY (codigo_comanda, codigo_cardapio)
);

-- DADOS EXEMPLO
INSERT INTO Cardapio (nome, descricao, preco_unitario) VALUES
('Café Expresso', 'Café puro, forte e encorpado', 5.00),
('Cappuccino', 'Mistura de café, leite vaporizado e espuma', 8.50),
('Mocha', 'Café com leite e calda de chocolate', 9.00);

INSERT INTO Comanda (data, mesa, nome_cliente) VALUES
('2025-10-25', 1, 'Carlos Silva'),
('2025-10-25', 2, 'Ana Souza'),
('2025-10-26', 3, 'Rafael Costa');

INSERT INTO ItemComanda (codigo_comanda, codigo_cardapio, quantidade) VALUES
(1, 1, 2),
(1, 2, 1),
(2, 3, 1),
(3, 1, 3),
(3, 2, 2);
```

---

## ⚙️ **2. Configuração da Conexão no Código**

No arquivo `CafeteriaBomGosto.java`, atualize a URL, usuário e senha conforme seu ambiente local:

```java
String url = "jdbc:postgresql://localhost:5432/bomgosto_db";
String user = "postgres";
String password = "123456";
```

Se for MySQL, altere para:

```java
String url = "jdbc:mysql://localhost:3306/bomgosto_db";
String user = "root";
String password = "123456";
```

---

##  **3. Compilar e Executar o Programa**

Abra o terminal dentro da pasta `src` e execute:

###  Compilar:
```bash
javac -cp "../lib/postgresql-42.7.3.jar" CafeteriaBomGosto.java
```

###  Executar:
```bash
java -cp ".:../lib/postgresql-42.7.3.jar" CafeteriaBomGosto
```

*(No Windows use `;` no lugar de `:` no classpath.)*

---

##  **O que o Programa Faz**

Durante a execução, o console exibirá as respostas para as 5 questões:

```
1️⃣  LISTAGEM DO CARDÁPIO (ordenado por nome)
2️⃣  COMANDAS E ITENS (detalhadas)
3️⃣  COMANDAS COM VALOR TOTAL
4️⃣  COMANDAS COM MAIS DE UM TIPO DE CAFÉ
5️⃣  FATURAMENTO TOTAL POR DATA
```

---

## ⚠️ **Possíveis Erros e Soluções**

| Erro | Causa provável | Solução |
|------|----------------|----------|
| `org.postgresql.Driver not found` | Driver JDBC ausente | Adicione o `.jar` do driver na pasta `lib` e inclua no classpath |
| `FATAL: password authentication failed` | Senha incorreta | Verifique `user` e `password` no código |
| `relation "cardapio" does not exist` | Banco não criado | Execute o script `bomgosto_db.sql` antes |
| `ClassNotFoundException: org.postgresql.Driver` | Driver não carregado | Verifique se o `.jar` está no caminho do classpath |

---

## 📘 **Licença e Uso**

Este projeto tem fins **educacionais e acadêmicos**.  
Sinta-se à vontade para modificar, melhorar ou expandir conforme seus estudos.

---

## 👨‍💻 **Autor**

**Raphael Costa Bianco**  
Tecnólogo em Análise e Desenvolvimento de Sistemas  
Programador & Técnico em Eletrônica
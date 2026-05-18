# FloowRoom — Sistema de Agendamento e Controle de Disponibilidade de Salas

## 📋 Visão Geral

O **FloowRoom** é um sistema backend desenvolvido para gerenciar a **disponibilidade e o agendamento de salas** em instituições de ensino, empresas e organizações. Ele permite controlar quem reservou qual sala, em qual período, para qual tipo de atividade — evitando conflitos de horário de forma automatizada.

---

## 🏗️ Arquitetura e Tecnologias

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de Dados | MySQL 8 |
| Autenticação | Spring Security + JWT (HS256) |
| Documentação | Swagger UI (SpringDoc OpenAPI) |
| Build | Apache Maven |
| Containerização | Docker + Docker Compose |

---

## 🗃️ Modelo de Dados

O sistema utiliza **5 schemas separados** no MySQL, seguindo a convenção do modelo entregue:

### `seguranca.tbUsuarios` — Usuários do sistema
| Campo | Tipo | Descrição |
|---|---|---|
| usuario_id | INT (PK) | Identificador |
| nome | VARCHAR(200) | Nome completo |
| login | VARCHAR(50) | Login único |
| senha | VARCHAR(255) | Senha criptografada (bcrypt) |
| atualizado_em | TIMESTAMP | Data da última modificação |
| atualizado_por | INT (FK) | Usuário responsável |

### `cadastros.tbSalas` — Salas disponíveis
| Campo | Tipo | Descrição |
|---|---|---|
| sala_id | INT (PK) | Identificador |
| numero | INT (UNIQUE) | Número único da sala |
| atualizado_por | INT (FK) | Usuário responsável |
| atualizado_em | TIMESTAMP | Data da última modificação |

### `cadastro.tbPessoas` — Locatários / Solicitantes
| Campo | Tipo | Descrição |
|---|---|---|
| pessoa_id | INT (PK) | Identificador |
| nome | VARCHAR(200) | Nome completo |
| cpf | VARCHAR(14) | CPF único |
| nascimento | DATE | Data de nascimento |
| telefone | VARCHAR(20) | Contato |
| pessoa_tipo_id | INT (FK) | Tipo da pessoa |
| atualizado_por / em | — | Auditoria |

### `agendamento.tbAgendaSala` — Agendamentos de salas
| Campo | Tipo | Descrição |
|---|---|---|
| agenda_sala_id | INT (PK) | Identificador |
| datahora_inicio | TIMESTAMP | Início do período |
| datahora_fim | TIMESTAMP | Fim do período |
| sala_id | INT (FK) | Sala agendada |
| pessoa_id | INT (FK) | Pessoa responsável |
| evento_tipo_id | INT (FK) | Tipo do evento |
| observacao | VARCHAR(255) | Notas adicionais |
| atualizado_por / em | — | Auditoria |

### Tabelas de domínio
- **`cadastro.tbPessoaTipo`** — Tipos de pessoa: Aluno, Professor, Funcionário…
- **`dominio.tbEventoTipo`** — Tipos de evento: Reunião, Aula, Palestra, Evento…

---

## 🔐 Autenticação

O sistema utiliza **JWT (JSON Web Token)**:

1. O usuário faz `POST /api/auth/login` com `login` + `senha`
2. Recebe um token `Bearer` com validade de **24 horas**
3. Inclui o token no header `Authorization` em todas as requisições protegidas

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📡 Endpoints da API

### Autenticação
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Registrar usuário | Não |
| POST | `/api/auth/login` | Login e obter token | Não |

### Salas
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| GET | `/api/salas` | Listar todas as salas | ✅ |
| GET | `/api/salas/{id}` | Buscar sala por ID | ✅ |
| POST | `/api/salas` | Criar sala | ✅ |
| PUT | `/api/salas/{id}` | Atualizar sala | ✅ |
| DELETE | `/api/salas/{id}` | Deletar sala | ✅ |

### Pessoas (Locatários)
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| GET | `/api/pessoas` | Listar pessoas | ✅ |
| GET | `/api/pessoas/{id}` | Buscar por ID | ✅ |
| POST | `/api/pessoas` | Cadastrar pessoa | ✅ |
| PUT | `/api/pessoas/{id}` | Atualizar | ✅ |
| DELETE | `/api/pessoas/{id}` | Remover | ✅ |

### Agenda de Salas
| Método | Rota | Descrição | Auth |
|---|---|---|---|
| GET | `/api/agenda` | Listar agendamentos | ✅ |
| GET | `/api/agenda/{id}` | Buscar agendamento | ✅ |
| GET | `/api/agenda/sala/{salaId}` | Agendamentos por sala | ✅ |
| GET | `/api/agenda/disponibilidade/{salaId}?inicio=&fim=` | Horários ocupados no período | ✅ |
| POST | `/api/agenda` | Criar agendamento | ✅ |
| PUT | `/api/agenda/{id}` | Atualizar agendamento | ✅ |
| DELETE | `/api/agenda/{id}` | Cancelar agendamento | ✅ |

### Domínio
| Método | Rota | Descrição |
|---|---|---|
| GET/POST/DELETE | `/api/dominio/pessoa-tipos` | Tipos de pessoa |
| GET/POST/DELETE | `/api/dominio/evento-tipos` | Tipos de evento |

---

## ✅ Regras de Negócio

1. **Conflito de horário**: O sistema rejeita (HTTP 409) qualquer agendamento que sobreponha um já existente para a mesma sala
2. **Login único**: Não é permitido cadastrar dois usuários com o mesmo `login`
3. **CPF único**: Não é permitido cadastrar duas pessoas com o mesmo CPF
4. **Número de sala único**: Cada sala possui um número único no sistema
5. **Senhas seguras**: Todas as senhas são armazenadas com hash `bcrypt`
6. **Auditoria**: Todas as tabelas registram `atualizado_por` e `atualizado_em`

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- MySQL 8 rodando na porta 3306

### 1. Configurar banco de dados
Edite `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/floowroom?createDatabaseIfNotExist=true...
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
```

### 2. Executar localmente
```bash
mvn clean package -DskipTests
java -jar target/floowroom-backend-1.0.0.jar
```

### 3. Executar com Docker Compose
```bash
docker-compose up --build
```

### 4. Acessar a documentação interativa
```
http://localhost:8080/swagger-ui.html
```

---

## 🔄 Fluxo Típico de Uso

```
1. POST /api/auth/register       → Criar primeiro usuário
2. POST /api/auth/login           → Obter token JWT
3. POST /api/dominio/evento-tipos → Cadastrar tipos (Reunião, Aula…)
4. POST /api/dominio/pessoa-tipos → Cadastrar tipos (Professor, Aluno…)
5. POST /api/salas                → Cadastrar salas (101, 102…)
6. POST /api/pessoas              → Cadastrar locatários
7. POST /api/agenda               → Criar agendamento
8. GET  /api/agenda/disponibilidade/{id}?inicio=&fim= → Verificar disponibilidade
```

---

## 📁 Estrutura do Projeto

```
FloowRoom2/
├── src/main/java/com/floowroom/
│   ├── config/           → SecurityConfig, SwaggerConfig
│   ├── controller/       → AuthController, SalaController,
│   │                       PessoaController, AgendaSalaController,
│   │                       DominioController
│   ├── dto/              → AuthDTO, SalaDTO, PessoaDTO,
│   │                       AgendaSalaDTO, DominioDTO
│   ├── entity/           → Usuario, Sala, Pessoa, PessoaTipo,
│   │                       AgendaSala, EventoTipo
│   ├── exception/        → GlobalExceptionHandler + Exceptions
│   ├── repository/       → Interfaces JPA (6 repositórios)
│   ├── security/         → JwtUtil, JwtFilter, UsuarioDetailsServiceImpl
│   └── service/          → AuthService, SalaService, PessoaService,
│                           AgendaSalaService, DominioService
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

*FloowRoom Backend — versão 1.0.0*

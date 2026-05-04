# AuthServer - Kotlin com Spring

Projeto desenvolvido na disciplina de Desenvolvimento de Backend (PUC PR). O repositório parte de um servidor de autenticação construído ao longo das aulas e é estendido com novos módulos para a entrega final.

---

## Vídeo da apresentação

[link]

---

## Tecnologias

- Linguagem: Kotlin
- Framework: Spring
- Banco de dados: H2 (temporário em memória)
- ORM: JPA/Hibernate
- Autenticação: JWT com JJWT
- Documentação: OpenAPI/SwaggerUI
- Java versão 21

---

## O que foi feito em aula

- Servidor de autenticação com gerenciamento de usuários e roles, as entidades `User` e `Role` possuem relacionamento Many-to-Many via tabela de junção `UserRoles`. Um bootstrapper popula o banco com roles e um usuário administrador padrão na inicialização;

- A segurança é baseada em JWT com expiração de 48h para usuários comuns e 1h para administradores. O Filter `JwtTokenFilter` popula o `SecurityContext` a cada requisição, e a autorização por role é feita com `@PreAuthorize`;

- Validações de entrada usam Bean Validation (`@NotBlank`, `@Email`, `@Pattern`);

- As exceções de domínio são mapeadas para respostas HTTP com `@ResponseStatus`;

- Logging com SLF4J em todas as operações relevantes, com saída em arquivo configurada no `application.yaml`.


**Endpoints:**

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| `GET` | `/api/users` | Pública | Lista usuários (filtrável por role, ordenável) |
| `GET` | `/api/users/{id}` | Pública | Busca usuário por ID |
| `POST` | `/api/users` | Pública | Cria usuário |
| `PATCH` | `/api/users/{id}` | JWT (dono ou ADMIN) | Atualiza nome do usuário |
| `DELETE` | `/api/users/{id}` | ADMIN | Remove usuário |
| `PUT` | `/api/users/{id}/roles/{roleName}` | ADMIN | Concede role ao usuário |
| `POST` | `/api/users/login` | Pública | Autenticação — retorna JWT |
| `GET` | `/api/roles` | Pública | Lista roles |
| `POST` | `/api/roles` | Pública | Cria role |
---

## O que foi feito para a entrega

A ideia é funcionar como um Task Manager em grupo, onde um usuário pode participar de múltiplos projetos e criar tarefas pertencentes a um projeto. Cada task tem uma descrição e um status (`BACKLOG`, `IN_PROGRESS`, `DONE`).

#### Entidades

**`Project`**
- Campos: `id`, `name`, `description`, `createdAt`
- Relacionamento Many-to-Many com `User` (um projeto pode ter vários membros, um usuário pode participar de vários projetos)
- Relacionamento One-to-Many com `Task`

**`Task`**
- Campos: `id`, `title`, `description`, `status`, `createdAt`
- Relacionamento Many-to-One com `Project` (uma task pertence a um único projeto)

#### Endpoints de Project

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| `GET` | `/api/projects` | Pública | Lista projetos (filtrável por nome, ordenável) |
| `GET` | `/api/projects/{id}` | Pública | Busca projeto por ID |
| `POST` | `/api/projects` | JWT | Cria projeto |
| `PUT` | `/api/projects/{id}` | ADMIN ou membro | Atualiza projeto |
| `DELETE` | `/api/projects/{id}` | ADMIN ou membro | Remove projeto |
| `PUT` | `/api/projects/{id}/users/{userId}` | ADMIN ou membro | Adiciona membro ao projeto |
| `DELETE` | `/api/projects/{id}/users/{userId}` | ADMIN ou membro | Remove membro do projeto |

#### Endpoints de Task

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| `GET` | `/api/projects/{projectId}/tasks` | Pública | Lista tasks do projeto (filtrável por status, ordenável) |
| `GET` | `/api/projects/{projectId}/tasks/{id}` | Pública | Busca task por ID |
| `POST` | `/api/projects/{projectId}/tasks` | ADMIN ou membro | Cria task |
| `PUT` | `/api/projects/{projectId}/tasks/{id}` | ADMIN ou membro | Atualiza task |
| `PATCH` | `/api/projects/{projectId}/tasks/{id}/status` | ADMIN ou membro | Atualiza apenas o status da task |
| `DELETE` | `/api/projects/{projectId}/tasks/{id}` | ADMIN ou membro | Remove task |

#### Filtragem e ordenação (query params)

Os endpoints `GET` de listagem aceitam os parâmetros:
- `sortDir` — `ASC` ou `DESC`;
- `sortBy` — campo pelo qual ordenar (ex.: `name`, `createdAt`);
- `name` — filtro por nome do projeto (em `/api/projects`);
- `status` — filtro por status da task (em `/api/projects/{id}/tasks`).

#### Logs e exceções

- Logging de todas as operações relevantes seguindo o padrão já adotado no projeto;
- As classes Exception existentes foram reutilizadas.

---

## Executando o projeto

### 1. Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
JWT_SECRET="sua-chave-secreta"
JWT_ISSUER="AuthServer"
JWT_EXPIRE_HOURS=48
JWT_ADMIN_EXPIRE_HOURS=1
```

> O arquivo `.env` está no `.gitignore` e não deve ser commitado.

### 2. Rodar a aplicação

**Via terminal** — o `bootRun` lê o `.env` automaticamente:

```bash
# Windows
gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```

**Via IntelliJ** — é necessário apontar o `.env` manualmente nas configurações de execução:
`Run > Edit Configurations > Modify options > Environment variables` e selecionar o arquivo `.env`.

### 3. Acessar

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- H2 Console: [http://localhost:8080/api/h2-console](http://localhost:8080/api/h2-console)

Credenciais do administrador padrão criado pelo bootstrapper:

- Email:    admin@authserver.com
- Senha:    admin

---

## Estrutura de pacotes

```
br.pucpr.authserver/
├── exceptions/        # Classes de exceções
├── lib/               # Utilitários compartilhados entre módulos (ex.: SortDir)
├── security/          # JWT, filtro e configuração do Spring Security
├── users/             # Entidade User, CRUD e autenticação de usuários
├── roles/             # Entidade Role, CRUD e gerenciamento de roles
├── projects/          # (novo) Entidade Project e seus endpoints
└── tasks/             # (novo) Entidade Task e seus endpoints
```

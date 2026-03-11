# API Biblioteca

API REST desenvolvida para gerenciar um sistema de biblioteca.
O projeto permite cadastrar livros, clientes e controlar empréstimos e devoluções, incluindo regras de negócio como disponibilidade de exemplares e finalização de empréstimos.

## Tecnologias utilizadas
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok

## Arquitetura do projeto

O projeto segue uma estrutura típica de aplicações Spring Boot:
````
src
 ├─ config
 ├─ controllers
 ├─ services
 ├─ repositories
 ├─ models
 ├─ dtos
 ├─ exceptions
````

## Modelo de Dados

O sistema possui três entidades principais:

````Book```` -> Representa os livros disponíveis na biblioteca.

Campos principais:
````
id
title
author
isbn
publicationYear
totalQuantity
availableQuantity
````

Regras importantes:

- Não é possível emprestar livro sem exemplares disponíveis
- A quantidade total não pode ser menor que a quantidade emprestada

````Client```` -> Representa os clientes cadastrados.

Campos principais:

````
id
name
email
cpf
````

Relacionamento:

- Um cliente pode possuir vários empréstimos

````Loan```` -> Representa o empréstimo de um livro.

Campos principais:

````
id
book
client
loanDate
returnDate
status
fineValue
````
Status possíveis:
````
EM_ANDAMENTO
DEVOLVIDO
````
Regras:

- Um empréstimo só pode ser finalizado uma vez
- Ao devolver um livro, a quantidade disponível é atualizada automaticamente

## Regras de Negócio Aplicadas:
- Validação de quantidade total de livros no cadastro
- Validação de quantidade total mínima na atualização de estoque
- Proteção contra redução do estoque abaixo dos livros emprestados
- Verificação de disponibilidade antes de realizar empréstimo
- Decremento automático do estoque ao emprestar livro
- Incremento automático do estoque ao devolver livro
- Proteção contra devolução inválida de estoque
- Inicialização automática do status do empréstimo
- Inicialização automática da multa do empréstimo
- Restrição de finalização única do empréstimo
- Atualização de status ao finalizar empréstimo
- Registro de multa na finalização do empréstimo
- Associação obrigatória entre empréstimo e livro
- Associação obrigatória entre empréstimo e cliente
- Unicidade do email do cliente
- Unicidade do CPF do cliente
- Restrição de campos obrigatórios nas entidades

<div align="center">
<h1> Finances-API </h1>

[![en][en-shield]][en-url]
[![pt-br][pt-br-shield]][pt-br-url]
[![project_license][license-shield]][license-url]
[![last-commit][commit-shield]][commit-url]
</div>

![](https://user-images.githubusercontent.com/55067868/191626878-96f58635-f938-40e5-acd7-7692d039c29d.png#vitrinedev)

## Descrição

Finances-API é uma API REST projetada para gerenciamento financeiro pessoal. Ela permite que os usuários gerenciem suas
finanças eficientemente, fornecendo recursos como registrar renda e despesas, consultar dados financeiros e controle de
acesso usando autenticação JWT.

## Características principais

- **Operações CRUD** para registros financeiros (renda e despesas).
- Controle de acesso seguro usando **autenticação JWT**.
- Armazenamento de dados persistente com **PostgreSQL**.
- Contêinerzado para facilitar a implantação usando **Docker**.
- Documentação da API e suporte de teste com **Postman**.

## Objetivos do projeto

O projeto foi desenvolvido em sprints com duração 1 semana cada, que possuiam determinadas atividades a serem implementadas. Para uma melhor gestão das atividades, foi utilizado o trello como ferramenta.

- [Trello da Sprint 1](https://trello.com/b/ofAXrAlA/challenge-backend-semana-1)
- [Trello da Sprint 2](https://trello.com/b/tKBmD8P6/challenge-backend-semana-2)
- [Trello da Sprint 3](https://trello.com/b/qFYXUVXJ/challenge-backend-semana-3)

## Tecnologias

![java] ![spring] ![postgresql] ![docker]

## Deploy

A API Spring Boot stá rodando numa Máquina Virtual do Google Cloud, com DNS e HTTPS da Cloudflare.

O link para o acesso é:

- API Spring Boot https://marujo.site

## Rode localmente

Para rodar é necessário ter Docker instalado.

- Clone o projeto

```
git clone https://github.com/WesleyMime/Finances-API.git
```

- Enter na pasta do projeto

```
cd Finances-API
```

- Inicie os serviços

```
docker compose up
```

Depois que o aplicativo estiver em execução, você pode usar o Postman ou qualquer outro API client para interagir com a
API.

O link para acesso é:

- http://localhost

## Documentação

A documentação feita no Postman pode ser acessada por esse [link](https://documenter.getpostman.com/view/19203694/UVeGs6cv) e é o jeito mais prático de se usar esta API.

## License

Distributed under the MIT license. See `LICENSE.txt` for more information.

[en-shield]: https://img.shields.io/badge/lang-en-green.svg?style=for-the-badge

[en-url]: https://github.com/WesleyMime/Finances-API/blob/main/README.md

[pt-br-shield]: https://img.shields.io/badge/lang-pt--br-lightdarkgreen.svg?style=for-the-badge

[pt-br-url]: https://github.com/WesleyMime/Finances-API/blob/main/README.pt-br.md

[commit-shield]: https://img.shields.io/github/last-commit/wesleymime/Finances-API.svg?style=for-the-badge

[commit-url]: https://github.com/wesleymime/Finances-API/commit

[license-shield]: https://img.shields.io/github/license/wesleymime/Finances-API.svg?style=for-the-badge

[license-url]: https://github.com/wesleymime/Finances-API/blob/master/LICENSE.txt

[java]: https://img.shields.io/badge/Java-000000?logo=openjdk&logoColor=white&style=for-the-badge

[spring]: https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff&style=for-the-badge

[postgresql]: https://img.shields.io/badge/postgresql-4169E1?logo=postgresql&logoColor=white&style=for-the-badge

[docker]: https://img.shields.io/badge/docker-2496ED?logo=docker&logoColor=white&style=for-the-badge
<div align="center">
<h1> Finances-API </h1>

[![en][en-shield]][en-url]
[![pt-br][pt-br-shield]][pt-br-url]
[![project_license][license-shield]][license-url]
[![last-commit][commit-shield]][commit-url]
![workflow][workflow-shield]
[![deploy][deploy-status]][deploy-url]

</div>

![Captura de tela Front-End](https://github.com/user-attachments/assets/ff6f1904-bded-40a7-89dc-5d37dd4f9f43)

## Descrição

Finances-API é uma API REST projetada para gerenciamento financeiro pessoal. Ela permite que os usuários gerenciem suas
finanças eficientemente, fornecendo recursos como registrar renda e despesas, consultar dados financeiros e controle de
acesso usando autenticação JWT.

## Objetivos do projeto

O projeto foi desenvolvido em sprints com duração 1 semana cada, que possuiam determinadas atividades a serem implementadas. Para uma melhor gestão das atividades, foi utilizado o trello como ferramenta.

- [Trello da Sprint 1](https://trello.com/b/ofAXrAlA/challenge-backend-semana-1)
- [Trello da Sprint 2](https://trello.com/b/tKBmD8P6/challenge-backend-semana-2)
- [Trello da Sprint 3](https://trello.com/b/qFYXUVXJ/challenge-backend-semana-3)

## Características principais

- Operações CRUD para registros financeiros (renda e despesas) usando **Spring Boot**.
- Frontend desenvolvido utilizando **Angular** para uma experiência de usuário dinâmica e
  responsíva.
- Integração com IA para relatórios financeiros customizados com **Spring AI**.
- Controle de acesso seguro usando **autenticação JWT**.
- Armazenamento de dados persistente com **PostgreSQL**.
- Contêinerzado para facilitar a implantação usando **Docker**.
- Documentação da API e suporte de teste com **Postman**.

## Tecnologias

![java] ![spring] ![postgresql] ![docker] ![angular] ![typescript] ![google] ![cloudflare]

## Deploy

A aplicação Angular está rodando no GitHub Pages e a API Spring Boot está rodando numa Máquina
Virtual do Google Cloud, com DNS e HTTPS da Cloudflare.

Os links para acesso são:

- Aplicação Angular https://marujo.site
- API Spring Boot https://finances.marujo.site

![](https://user-images.githubusercontent.com/55067868/191626878-96f58635-f938-40e5-acd7-7692d039c29d.png#vitrinedev)

## Rode localmente

Para rodar é necessário ter Docker instalado.

- Clone o projeto

```
git clone https://github.com/WesleyMime/Finances-API.git
```

- Entre na pasta do projeto

```
cd Finances-API
```

- Inicie os serviços

```
docker compose up
```

## Uso

Depois que o aplicativo estiver em execução, você pode usar a aplicação Angular ou usar um
API cliente como o Postman para interagir com a API.
Para usar as funcionalidades de IA, você irá precisar de uma API Key. Você pode conseguir uma de
graça
em https://groq.com/, após obter a chave, mude o parâmetro AI_API_KEY no docker-compose para ela.

Os links para acesso são:

- Aplicação Angular http://localhost
- API Spring Boot http://localhost:8080

## Documentação

A documentação feita no Postman pode ser acessada por
esse [link](https://documenter.getpostman.com/view/19203694/UVeGs6cv).

## Licença

Distribuído sob a licença do MIT. Consulte `LICENSE.txt` para obter mais informações.

[en-shield]: https://img.shields.io/badge/lang-en-green.svg?style=for-the-badge
[en-url]: https://github.com/WesleyMime/Finances-API/blob/main/README.md
[pt-br-shield]: https://img.shields.io/badge/lang-pt--br-lightdarkgreen.svg?style=for-the-badge
[pt-br-url]: https://github.com/WesleyMime/Finances-API/blob/main/README.pt-br.md
[commit-shield]: https://img.shields.io/github/last-commit/wesleymime/Finances-API.svg?style=for-the-badge
[commit-url]: https://github.com/wesleymime/Finances-API/commit
[license-shield]: https://img.shields.io/github/license/wesleymime/Finances-API.svg?style=for-the-badge
[license-url]: https://github.com/wesleymime/Finances-API/blob/master/LICENSE.txt
[workflow-shield]: https://img.shields.io/github/actions/workflow/status/wesleymime/Finances-API/main.yml?style=for-the-badge
[workflow-url]: https://img.shields.io/github/actions/workflow/status/wesleymime/Finances-API/main.yml
[deploy-status]: http://167.234.233.130:3001/api/badge/5/status?upColor=lightdarkgreen&style=for-the-badge
[deploy-url]: https://marujo.site/

[java]: https://img.shields.io/badge/Java-000000?logo=openjdk&logoColor=white&style=for-the-badge
[spring]: https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff&style=for-the-badge
[postgresql]: https://img.shields.io/badge/postgresql-4169E1?logo=postgresql&logoColor=white&style=for-the-badge
[docker]: https://img.shields.io/badge/docker-2496ED?logo=docker&logoColor=white&style=for-the-badge

[angular]: https://img.shields.io/badge/Angular-%23DD0031.svg?logo=angular&logoColor=white&style=for-the-badge

[typescript]: https://img.shields.io/badge/TypeScript-3178C6?logo=typescript&logoColor=fff&style=for-the-badge
[google]: https://img.shields.io/badge/Google%20Cloud-%234285F4.svg?logo=google-cloud&logoColor=white&style=for-the-badge
[cloudflare]: https://img.shields.io/badge/Cloudflare-F38020?logo=Cloudflare&logoColor=white&style=for-the-badge

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

## Description

Finances-API is a REST API designed for personal financial management. It allows users to manage their finances
efficiently by providing features like recording incomes and expenses, querying financial data, and access control
using JWT authentication.

## Project objectives

The project was developed in sprints lasting 1 week each, which had certain activities to be implemented.
For better management of activities, trello was used.

- [Sprint 1 Trello](https://trello.com/b/ofAXrAlA/challenge-backend-semana-1)
- [Sprint 2 Trello](https://trello.com/b/tKBmD8P6/challenge-backend-semana-2)
- [Sprint 3 Trello](https://trello.com/b/qFYXUVXJ/challenge-backend-semana-3)

## Key Features

- CRUD operations for financial records (income and expenses) using **Spring Boot**.
- Frontend developed using **Angular** for a dynamic and responsive user experience.
- AI integration for custom financial analysis with **Spring AI**.
- Secure access control using **JWT authentication**.
- Persistent data storage with **PostgreSQL**.
- Containerized for easy deployment using **Docker**.
- API documentation and testing support with **Postman**.

## Technologies

![java] ![spring] ![postgresql] ![docker] ![angular] ![typescript] ![google] ![cloudflare]

## Deploy

The Angular app is running on GitHub Pages and the Spring Boot API is running on a Google Cloud
Virtual Machine, with DNS and HTTPS from Cloudflare.

The links to access are:

- Angular application https://marujo.site
- Spring Boot API https://finances.marujo.site

![](https://user-images.githubusercontent.com/55067868/191626878-96f58635-f938-40e5-acd7-7692d039c29d.png#vitrinedev)

## Run locally

To run locally you need to have Docker installed.

- Clone project

```
git clone https://github.com/WesleyMime/Finances-API.git
```

- Enter the project folder

```
cd Finances-API
```

- Start services

```
docker compose up
```

## Usage

Once the application is running, you can use the Angular app or use an API client like Postman
to interact with the API.
To use AI features, you will need an API Key. You can get one for free at https://groq.com/,
after obtaining the key, change the AI_API_KEY param in docker-compose to it.

The links to access are:

- Angular application http://localhost
- Spring Boot API http://localhost:8080

## Documentation

The documentation made in Postman can be accessed by
this [link](https://documenter.getpostman.com/view/19203694/UVeGs6cv).

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
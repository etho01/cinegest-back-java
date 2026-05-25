# CineGest — Backend (Spring Boot)

Migration d'un projet personnel initialement développé en PHP vers **Spring Boot**.

## Contexte

CineGest est une application de gestion de cinéma que j'ai développée en PHP. Dans le cadre de ma montée en compétences sur l'écosystème Java/Spring, j'ai entrepris de réécrire le backend from scratch en **Spring Boot 4**, en conservant la même logique métier tout en adoptant les bonnes pratiques de l'écosystème Java.

## Stack technique

- **Java 26**
- **Spring Boot 4**
- **Spring Security** + **JWT** (jjwt 0.12)
- **Spring Data JPA** + **Hibernate**
- **MariaDB**
- **Spring Mail**
- **Lombok**
- **Maven**

## Fonctionnalités implémentées

### Authentification de la partie administration (`/app/auth`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/app/auth/register` | Inscription — crée un compte et retourne un JWT |
| `POST` | `/app/auth/login` | Connexion — authentifie et retourne un JWT |
| `POST` | `/app/auth/forgot-password` | Envoie un email de réinitialisation de mot de passe |
| `POST` | `/app/auth/reset-password` | Réinitialise le mot de passe via un token |

### Sécurité

- Authentification **stateless** via JWT (Bearer token)
- Mots de passe hashés avec **BCrypt**
- Filtre JWT (`JwtAuthFilter`) sur chaque requête
- Token de réinitialisation à **usage unique**, expirant au bout d'**1 heure**
- Routes publiques : `/app/auth/**`

### Modèle utilisateur

Chaque utilisateur est identifié par son **email**, son **type** (`app`, ou autre origine) et un **originID**, permettant de distinguer les comptes issus de différentes sources — logique héritée de la version PHP.

## Configuration

Copier et adapter le fichier `src/main/resources/application.properties` :

```properties
# Base de données
spring.datasource.url=jdbc:mariadb://localhost:3308/cinegestJava
spring.datasource.username=cinegest
spring.datasource.password=cinegest

# JWT
jwt.secret=changez-ce-secret-en-production-min-32-caracteres
jwt.expiration=86400000

# Mail (SMTP Gmail)
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-application

# URL du frontend (pour les liens dans les emails)
app.frontend-url=http://localhost:4200
```

## Lancement

```bash
./mvnw spring-boot:run
```

Le serveur démarre sur le port `8080`.

## Structure du projet

```
src/main/java/com/cinegest/back/
├── app/
│   └── auth/
│       ├── controller/   # AppAuthController
│       ├── dto/          # Objets de transfert (login, register, reset...)
│       └── service/      # Logique métier
├── config/               # SecurityConfig
├── global/
│   ├── entity/           # User, PasswordResetToken
│   └── repository/       # UserRepository, PasswordResetTokenRepository
└── security/             # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
```


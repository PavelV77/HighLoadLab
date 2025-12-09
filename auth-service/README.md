# 🔐 Auth Service

Сервис авторизации и аутентификации с использованием JWT токенов.

## Возможности

- ✅ **Регистрация пользователей** - создание нового аккаунта
- ✅ **Авторизация (логин)** - получение JWT токена
- ✅ **JWT токены** - безопасная аутентификация
- ✅ **Хеширование паролей** - BCrypt
- ✅ **Интеграция с Eureka** - service discovery
- ✅ **Интеграция с Gateway** - единая точка входа

## Технологии

- Spring Boot 3.2.5
- Spring Security
- JWT (jjwt 0.12.3)
- PostgreSQL
- Flyway (миграции БД)
- Eureka Client

## API Endpoints

### Регистрация
```http
POST /auth/register
Content-Type: application/json

{
  "login": "username",
  "password": "password123",
  "email": "user@example.com"
}
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": "uuid",
  "login": "username",
  "email": "user@example.com"
}
```

### Логин
```http
POST /auth/login
Content-Type: application/json

{
  "login": "username",
  "password": "password123"
}
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": "uuid",
  "login": "username",
  "email": "user@example.com"
}
```

### Валидация токена
```http
GET /auth/validate
Authorization: Bearer <token>
```

### Получить пользователя
```http
GET /auth/user/{userId}
```

## Использование JWT токена

После получения токена, используйте его в заголовке `Authorization`:

```http
Authorization: Bearer <token>
```

## Конфигурация

### application.yml

```yaml
server:
  port: 8083

spring:
  application:
    name: auth-service
  datasource:
    url: jdbc:postgresql://localhost:5442/postgres
    username: postgres
    password: postgres

jwt:
  secret: mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm
  expiration: 86400000  # 24 hours
```

### JWT Secret

⚠️ **Важно:** В продакшене используйте безопасный секретный ключ длиной минимум 256 бит!

## База данных

Сервис создает таблицу `auth_user` в схеме `webcoursenews`:
- `id` - UUID
- `login` - уникальный логин
- `password` - хешированный пароль (BCrypt)
- `email` - email (опционально)
- `insert_at` - время создания
- `update_at` - время обновления

## Запуск

```bash
cd auth-service
mvn spring-boot:run
```

## Интеграция с Gateway

Gateway уже настроен для проксирования запросов:
- `http://localhost:8080/auth/**` → `lb://auth-service`

## Пример использования

### Регистрация
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'
```

### Логин
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "password123"
  }'
```

### Использование токена
```bash
TOKEN="your-jwt-token-here"
curl -X GET http://localhost:8080/auth/user/{userId} \
  -H "Authorization: Bearer $TOKEN"
```

## Безопасность

- ✅ Пароли хешируются с помощью BCrypt
- ✅ JWT токены с подписью (HS256)
- ✅ Токены имеют срок действия (24 часа)
- ✅ CORS настроен для работы с фронтендом

## Расширение функциональности

Можно добавить:
- Refresh tokens
- Роли и права доступа (RBAC)
- OAuth2 интеграция
- Двухфакторная аутентификация
- История входов
- Блокировка аккаунта после неудачных попыток




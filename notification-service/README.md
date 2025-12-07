# Notification Service

Сервис уведомлений на Python с использованием FastAPI и Kafka.

## Возможности

- ✅ **Автоматическое создание уведомлений из Kafka** - читает события о комментариях
- ✅ **Email уведомления** - автоматическая отправка email при создании комментария
- Создание уведомлений через REST API
- Получение списка уведомлений
- Получение уведомления по ID
- Удаление уведомлений
- Массовая отправка уведомлений
- Фильтрация по user_id

## Kafka Integration

Сервис автоматически читает сообщения из Kafka топика `comment-notifications` и создает уведомления для авторов новостей при добавлении комментариев.

## Email уведомления

При получении события о комментарии из Kafka, сервис автоматически отправляет email уведомление автору новости.

### Настройка SMTP

См. подробную инструкцию в [EMAIL_CONFIG.md](EMAIL_CONFIG.md)

Быстрая настройка для Gmail:
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=your-email@gmail.com
SMTP_USE_TLS=true
```

## Установка

```bash
# Создать виртуальное окружение
python -m venv venv

# Активировать виртуальное окружение
# Windows:
venv\Scripts\activate
# Linux/Mac:
source venv/bin/activate

# Установить зависимости
pip install -r requirements.txt
```

## Запуск

```bash
# Запуск напрямую
python main.py

# Или через uvicorn
uvicorn main:app --host 0.0.0.0 --port 8082 --reload
```

## API Endpoints

### Health Check
```
GET /health
```

### Создать уведомление
```
POST /notifications
Content-Type: application/json

{
  "user_id": "uuid-here",
  "title": "Новое уведомление",
  "message": "Текст уведомления",
  "notification_type": "info",
  "priority": "normal"
}
```

### Получить все уведомления
```
GET /notifications
GET /notifications?user_id=uuid-here
```

### Получить уведомление по ID
```
GET /notifications/{notification_id}
```

### Удалить уведомление
```
DELETE /notifications/{notification_id}
```

### Массовая отправка
```
POST /notifications/batch
Content-Type: application/json

[
  {
    "user_id": "uuid-1",
    "title": "Уведомление 1",
    "message": "Текст 1"
  },
  {
    "user_id": "uuid-2",
    "title": "Уведомление 2",
    "message": "Текст 2"
  }
]
```

## Конфигурация

### Переменные окружения

Создайте файл `.env`:

```env
PORT=8082
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_TOPIC=comment-notifications

# SMTP настройки (опционально, для email уведомлений)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=your-email@gmail.com
SMTP_USE_TLS=true
```

Или установите через переменные окружения:
```bash
export PORT=8082
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_TOPIC=comment-notifications
python main.py
```

### Kafka Consumer

Kafka Consumer запускается автоматически в фоновом потоке при старте приложения и:
- Подключается к Kafka брокеру
- Читает сообщения из топика `comment-notifications`
- Создает уведомления для авторов новостей
- **Отправляет email уведомления** (если настроен SMTP)
- Сохраняет уведомления в хранилище

## Интеграция с Gateway

Добавьте маршрут в `Gateway/src/main/resources/application.yml`:

```yaml
- id: notifications-api
  uri: http://localhost:8082
  predicates:
    - Path=/notifications/**
```

## Расширение функциональности

Сервис можно расширить для:
- ✅ Email уведомлений (SMTP) - **реализовано**
- SMS уведомлений (Twilio, AWS SNS)
- Push уведомлений (Firebase, OneSignal)
- WebSocket для real-time уведомлений
- Интеграция с базой данных (PostgreSQL, MongoDB)
- Очереди сообщений (RabbitMQ, Kafka)

## Документация API

После запуска доступна автоматическая документация:
- Swagger UI: http://localhost:8082/docs
- ReDoc: http://localhost:8082/redoc


# 🔍 Диагностика проблем с email уведомлениями

## Проверка шаг за шагом

### 1. Проверьте, что .env файл существует

```bash
cd notification-service
dir .env
```

Если файла нет, создайте его из `mail.env`:
```bash
copy mail.env .env
```

### 2. Проверьте SMTP настройки в .env

Файл должен содержать:
```env
SMTP_HOST=smtp.mail.ru
SMTP_PORT=465
SMTP_USER=vasilievpavel77@mail.ru
SMTP_PASSWORD=ka5buQT1rPwpSf6j9cs9
SMTP_FROM=vasilievpavel77@mail.ru
SMTP_USE_TLS=true
```

### 3. Проверьте логи Notification Service

При создании комментария должны появиться логи:

```
📨 Received comment notification event: {...}
   Event keys: ['commentId', 'newsId', 'newsAuthorId', 'commentAuthorId', 'commentBody', 'newsTitle', 'userEmail', 'timestamp']
📧 Email from event: {email} for user {uuid}
📤 Attempting to send email to {email}...
✅ Email sent successfully to {email}
```

### 4. Возможные проблемы

#### Проблема: "SMTP_USER or SMTP_PASSWORD not set"

**Решение:**
- Убедитесь, что файл `.env` существует в `notification-service/`
- Перезапустите Notification Service после создания `.env`

#### Проблема: "userEmail doesn't look like a valid email address"

**Причина:** В `CommentService.java` используется `newsAuthor.getLogin()` как email, но login может не быть email адресом.

**Решение:** 
- Проверьте, что в базе данных у пользователя login является валидным email
- Или добавьте отдельное поле `email` в сущность `User`

#### Проблема: "SMTP authentication failed"

**Решение:**
- Для Gmail: используйте App Password, не обычный пароль
- Для Mail.ru: убедитесь, что пароль правильный
- Проверьте, что двухфакторная аутентификация включена (для Gmail)

#### Проблема: Email не приходит, но логи показывают "Email sent successfully"

**Возможные причины:**
- Письмо попало в спам
- Неправильный email адрес получателя
- Проблемы с SMTP сервером

**Решение:**
- Проверьте папку "Спам"
- Проверьте логи SMTP сервера
- Попробуйте отправить тестовое письмо вручную

### 5. Тестовая отправка email

Создайте тестовый скрипт `test_email.py`:

```python
import asyncio
import os
from dotenv import load_dotenv
from email_service import EmailService

load_dotenv()

async def test_email():
    service = EmailService()
    result = await service.send_email(
        to_email="your-test-email@example.com",
        subject="Test Email",
        body_text="This is a test email",
        body_html="<h1>This is a test email</h1>"
    )
    print(f"Email sent: {result}")

if __name__ == "__main__":
    asyncio.run(test_email())
```

Запустите:
```bash
python test_email.py
```

### 6. Проверка Kafka событий

Убедитесь, что событие отправляется в Kafka:

**В логах KursNews должно быть:**
```
Sending comment notification event to Kafka: CommentNotificationEvent(...)
Comment notification sent successfully
```

**В логах Notification Service должно быть:**
```
📨 Received comment notification event: {...}
```

### 7. Проверка userEmail в событии

В `CommentService.java` на строке 65:
```java
String userEmail = newsAuthor.getLogin();  // Временное решение
```

Проверьте, что `login` пользователя является валидным email адресом.

Если нет, измените на:
```java
String userEmail = newsAuthor.getEmail();  // Если есть поле email
// или
String userEmail = newsAuthor.getLogin() + "@example.com";  // Временное решение
```


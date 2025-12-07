# Конфигурация Email уведомлений

## Настройка SMTP

Создайте файл `.env` в директории `notification-service`:

```env
# SMTP настройки
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=your-email@gmail.com
SMTP_USE_TLS=true
```

## Gmail настройка

### 1. Включить двухфакторную аутентификацию
1. Перейдите в настройки Google аккаунта
2. Включите двухфакторную аутентификацию

### 2. Создать пароль приложения
1. Перейдите в https://myaccount.google.com/apppasswords
2. Выберите "Почта" и "Другое устройство"
3. Введите название (например, "Notification Service")
4. Скопируйте сгенерированный пароль
5. Используйте этот пароль в `SMTP_PASSWORD`

### 3. Настройка .env
```env
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=xxxx xxxx xxxx xxxx  # Пароль приложения (16 символов без пробелов)
SMTP_FROM=your-email@gmail.com
SMTP_USE_TLS=true
```

## Другие SMTP провайдеры

### Yandex Mail
```env
SMTP_HOST=smtp.yandex.ru
SMTP_PORT=465
SMTP_USER=your-email@yandex.ru
SMTP_PASSWORD=your-password
SMTP_FROM=your-email@yandex.ru
SMTP_USE_TLS=true
```

### Mail.ru
```env
SMTP_HOST=smtp.mail.ru
SMTP_PORT=465
SMTP_USER=your-email@mail.ru
SMTP_PASSWORD=your-password
SMTP_FROM=your-email@mail.ru
SMTP_USE_TLS=true
```

### Outlook/Hotmail
```env
SMTP_HOST=smtp-mail.outlook.com
SMTP_PORT=587
SMTP_USER=your-email@outlook.com
SMTP_PASSWORD=your-password
SMTP_FROM=your-email@outlook.com
SMTP_USE_TLS=true
```

## Тестирование

### Без SMTP (режим логирования)
Если не указать `SMTP_USER` и `SMTP_PASSWORD`, email не будет отправляться, но в логах будет информация о том, что было бы отправлено.

### С SMTP
После настройки `.env`, при создании комментария:
1. Событие отправляется в Kafka
2. Notification Service читает событие
3. Email отправляется автору новости

## Проверка логов

```bash
# В логах должно быть:
INFO: Email sent successfully to user@example.com: Новый комментарий
```

## Troubleshooting

### Email не отправляется
1. Проверьте логи на наличие ошибок
2. Убедитесь, что SMTP настройки правильные
3. Для Gmail используйте пароль приложения, не обычный пароль
4. Проверьте, что порт не заблокирован файрволом

### Ошибка аутентификации
- Для Gmail: используйте пароль приложения
- Убедитесь, что `SMTP_USER` и `SMTP_PASSWORD` правильные
- Проверьте, что двухфакторная аутентификация включена (для Gmail)

### Порт заблокирован
- Попробуйте другой порт (587 для TLS, 465 для SSL)
- Проверьте настройки файрвола


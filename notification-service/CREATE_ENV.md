# Создание .env файла

## Проблема

SMTP настройки не загружаются, потому что файл `.env` не существует.

## Решение

Создайте файл `.env` в папке `notification-service`:

### Windows (PowerShell):
```powershell
cd notification-service
Copy-Item mail.env .env
```

### Windows (CMD):
```cmd
cd notification-service
copy mail.env .env
```

### Linux/Mac:
```bash
cd notification-service
cp mail.env .env
```

## Или создайте вручную

Создайте файл `notification-service/.env` со следующим содержимым:

```env
SMTP_HOST=smtp.mail.ru
SMTP_PORT=465
SMTP_USER=vasilievpavel77@mail.ru
SMTP_PASSWORD=ka5buQT1rPwpSf6j9cs9
SMTP_FROM=vasilievpavel77@mail.ru
SMTP_USE_TLS=true
```

## После создания .env

1. Перезапустите Notification Service
2. Проверьте логи - должно исчезнуть предупреждение о SMTP настройках
3. Создайте комментарий - email должен отправиться


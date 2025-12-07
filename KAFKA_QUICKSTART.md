# 🚀 Быстрый запуск Kafka

## Проблема: `NoBrokersAvailable`

Если вы видите ошибку `kafka.errors.NoBrokersAvailable`, это означает, что Kafka не запущен.

## Решение

### 1. Запустить Kafka через Docker Compose

```bash
docker-compose -f docker-compose-kafka.yml up -d
```

### 2. Проверить, что Kafka запущен

```bash
docker ps
```

Должны быть запущены контейнеры:
- `zookeeper`
- `kafka`
- `kafka-ui`

### 3. Проверить Kafka UI

Откройте в браузере: http://localhost:8089

### 4. Перезапустить Notification Service

```bash
cd notification-service
python main.py
```

## Что изменилось

✅ **Улучшена обработка ошибок:**
- Сервис теперь пытается подключиться к Kafka 5 раз с задержкой 5 секунд
- Если Kafka недоступен, сервис продолжит работу без consumer (REST API будет работать)
- Более информативные сообщения об ошибках

✅ **Создан docker-compose-kafka.yml:**
- Zookeeper (порт 2181)
- Kafka (порт 9092)
- Kafka UI (порт 8089)

## Проверка работы

После запуска Kafka и Notification Service:

1. **Создайте комментарий** через API
2. **Проверьте логи** Notification Service - должно быть:
   ```
   ✅ Kafka consumer started successfully. Listening to topic: comment-notifications
   ```
3. **Проверьте Kafka UI** - топик `comment-notifications` должен появиться автоматически

## Остановка Kafka

```bash
docker-compose -f docker-compose-kafka.yml down
```

## Troubleshooting

### Kafka не запускается
```bash
# Проверить логи
docker-compose -f docker-compose-kafka.yml logs

# Перезапустить
docker-compose -f docker-compose-kafka.yml restart
```

### Порт 9092 уже занят
Измените порт в `docker-compose-kafka.yml`:
```yaml
ports:
  - "9093:9092"  # Внешний порт:внутренний порт
```

И обновите `notification-service/.env`:
```env
KAFKA_BOOTSTRAP_SERVERS=localhost:9093
```


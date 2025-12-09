import json
from kafka import KafkaConsumer
from datetime import datetime
import logging
import asyncio
import threading
from email_service import EmailService

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

email_service = EmailService()

_notifications_db = []

class CommentNotificationConsumer:
    def __init__(self):
        # Жестко заданные настройки Kafka
        self.bootstrap_servers = "localhost:9092"
        self.topic = "comment-notifications"
        self.consumer = None
        self.loop = None
        self.max_retries = 5
        self.retry_delay = 5  # секунды
        
    def _check_kafka_connection(self):
        """Проверить доступность Kafka (только проверка порта)"""
        try:
            import socket
            
            # Проверим, доступен ли порт
            host, port = self.bootstrap_servers.split(':')
            port = int(port)
            
            # Попробуем IPv4 и IPv6
            for family in (socket.AF_INET, socket.AF_INET6):
                try:
                    sock = socket.socket(family, socket.SOCK_STREAM)
                    sock.settimeout(2)
                    result = sock.connect_ex((host, port))
                    sock.close()
                    if result == 0:
                        return True
                except Exception:
                    continue
            
            return False
        except Exception as e:
            logger.debug(f"Kafka port check failed: {e}")
            return False
    
    def start(self):
        """Запустить consumer с retry логикой"""
        # Проверить, не запущен ли уже consumer
        if self.consumer is not None:
            logger.warning("Kafka consumer already started. Skipping duplicate start.")
            return
        
        retry_count = 0
        
        # Создать event loop для async операций один раз
        if self.loop is None:
            self.loop = asyncio.new_event_loop()
            asyncio.set_event_loop(self.loop)
        
        while retry_count < self.max_retries:
            try:
                # Проверить доступность порта Kafka
                if not self._check_kafka_connection():
                    retry_count += 1
                    if retry_count < self.max_retries:
                        logger.warning(
                            f"Kafka port {self.bootstrap_servers} is not accessible. "
                            f"Retrying in {self.retry_delay} seconds... "
                            f"(Attempt {retry_count}/{self.max_retries})"
                        )
                        import time
                        time.sleep(self.retry_delay)
                        continue
                    else:
                        logger.error(
                            f"Failed to connect to Kafka after {self.max_retries} attempts. "
                            f"Please ensure Kafka is running at {self.bootstrap_servers}"
                        )
                        logger.info("Notification service will continue without Kafka consumer.")
                        return
                
                logger.info(f"Connecting to Kafka at {self.bootstrap_servers}...")
                
                # Создать consumer только если его еще нет
                if self.consumer is None:
                    try:
                        self.consumer = KafkaConsumer(
                            self.topic,
                            bootstrap_servers=self.bootstrap_servers,
                            value_deserializer=lambda m: json.loads(m.decode('utf-8')),
                            group_id='notification-service-group',
                            auto_offset_reset='earliest',
                            enable_auto_commit=True,
                            consumer_timeout_ms=10000,  # Увеличиваем таймаут
                            api_version=(0, 10, 1)  # Явно указываем версию API
                        )
                        logger.info(f"Kafka consumer created successfully. Listening to topic: {self.topic}")
                    except Exception as init_error:
                        # Если не удалось инициализировать, попробуем еще раз
                        if "NoBrokersAvailable" in str(init_error) or "Connection" in str(init_error):
                            retry_count += 1
                            if retry_count < self.max_retries:
                                logger.warning(
                                    f"Kafka connection failed: {init_error}. "
                                    f"Retrying in {self.retry_delay} seconds... "
                                    f"(Attempt {retry_count}/{self.max_retries})"
                                )
                                import time
                                time.sleep(self.retry_delay)
                                continue
                            else:
                                logger.error(f"Failed to connect to Kafka after {self.max_retries} attempts: {init_error}")
                                logger.info("Notification service will continue without Kafka consumer.")
                                return
                        else:
                            # Другие ошибки - возможно Kafka еще не готов, но порт доступен
                            logger.warning(f"Kafka initialization warning: {init_error}. Retrying...")
                            retry_count += 1
                            if retry_count < self.max_retries:
                                import time
                                time.sleep(self.retry_delay)
                                continue
                            else:
                                raise init_error
                
                # Основной цикл чтения сообщений
                logger.info("Starting message consumption loop...")
                try:
                    for message in self.consumer:
                        try:
                            logger.debug(f"Received message from topic {message.topic}, partition {message.partition}, offset {message.offset}")
                            self.process_message(message.value)
                        except Exception as e:
                            logger.error(f"Error processing message: {e}", exc_info=True)
                except Exception as loop_error:
                    # Если consumer закрыт или произошла ошибка в цикле
                    if "closed" in str(loop_error).lower() or "KafkaConsumer is closed" in str(loop_error):
                        logger.info("Kafka consumer was closed. Exiting consumption loop.")
                        return
                    else:
                        logger.error(f"Error in consumption loop: {loop_error}", exc_info=True)
                        # Попробуем переподключиться
                        if self.consumer:
                            try:
                                self.consumer.close()
                            except:
                                pass
                        self.consumer = None
                        retry_count += 1
                        if retry_count < self.max_retries:
                            logger.warning(f"Retrying connection... (Attempt {retry_count}/{self.max_retries})")
                            import time
                            time.sleep(self.retry_delay)
                            continue
                        else:
                            logger.error("Max retries reached. Stopping Kafka consumer.")
                            return
                        
            except Exception as e:
                retry_count += 1
                error_msg = str(e)
                
                if "NoBrokersAvailable" in error_msg or "Connection" in error_msg:
                    if retry_count < self.max_retries:
                        logger.warning(
                            f"Kafka connection failed: {error_msg}. "
                            f"Retrying in {self.retry_delay} seconds... "
                            f"(Attempt {retry_count}/{self.max_retries})"
                        )
                        import time
                        time.sleep(self.retry_delay)
                        continue
                    else:
                        logger.error(
                            f"Failed to connect to Kafka after {self.max_retries} attempts: {error_msg}"
                        )
                        logger.info(
                            f"Please ensure Kafka is running:\n"
                            f"   docker-compose -f docker-compose-kafka.yml up -d\n"
                            f"   Or check if Kafka is available at {self.bootstrap_servers}"
                        )
                        logger.info("Notification service will continue without Kafka consumer.")
                        return
                else:
                    logger.error(f"Unexpected error in Kafka consumer: {e}", exc_info=True)
                    if retry_count < self.max_retries:
                        import time
                        time.sleep(self.retry_delay)
                        continue
                    else:
                        logger.error("Max retries reached. Stopping Kafka consumer.")
                        return
    
    def process_message(self, event_data):
        """Обработать сообщение из Kafka"""
        logger.info(f"Received comment notification event: {event_data}")
        logger.info(f"   Event keys: {list(event_data.keys())}")
        
        try:
            # Извлечь данные из события
            comment_id = event_data.get('commentId')
            news_id = event_data.get('newsId')
            news_author_id = event_data.get('newsAuthorId')  # Кому отправлять уведомление
            comment_author_id = event_data.get('commentAuthorId')
            comment_body = event_data.get('commentBody', '')
            news_title = event_data.get('newsTitle', 'Новость')
            
            # Создать уведомление для автора новости
            notification = {
                "id": f"notif_{datetime.now().timestamp()}_{comment_id}",
                "user_id": str(news_author_id),
                "title": "Новый комментарий",
                "message": f"К вашей новости '{news_title}' добавлен комментарий: {comment_body[:100]}...",
                "notification_type": "info",
                "priority": "normal",
                "created_at": datetime.now().isoformat(),
                "status": "sent",
                "comment_id": str(comment_id),
                "news_id": str(news_id)
            }
            
            # Сохранить уведомление
            _notifications_db.append(notification)
            logger.info(f"Notification created for user {news_author_id}: {notification['title']}")
            
            # Отправить email уведомление
            user_email = event_data.get('userEmail')  # Email автора новости
            logger.info(f"Email from event: {user_email} for user {news_author_id}")
            
            if user_email and user_email.strip():
                # Проверить, что это похоже на email (содержит @)
                if '@' not in user_email:
                    logger.warning(
                        f"userEmail '{user_email}' doesn't look like a valid email address. "
                        f"Email notification skipped. (Login is used as email, but it's not an email format)"
                    )
                else:
                    # Запустить async функцию в отдельном потоке с улучшенным логированием
                    def run_async_email():
                        try:
                            logger.info(f"Email thread started for {user_email}")
                            # Всегда создаем новый event loop для email потока
                            # Это проще и надежнее, чем использовать существующий loop
                            logger.info("Creating new event loop for email sending")
                            new_loop = asyncio.new_event_loop()
                            asyncio.set_event_loop(new_loop)
                            try:
                                logger.info(f"Running email sending coroutine for {user_email}")
                                result = new_loop.run_until_complete(
                                    self.send_email_notification(
                                        user_email,
                                        notification['title'],
                                        notification['message'],
                                        news_title,
                                        comment_body
                                    )
                                )
                                if result:
                                    logger.info(f"Email sending completed successfully for {user_email}")
                                else:
                                    logger.warning(f"Email sending returned False for {user_email}")
                            except Exception as e:
                                logger.error(f"Exception in email sending for {user_email}: {e}", exc_info=True)
                            finally:
                                logger.info(f"Closing event loop for {user_email}")
                                new_loop.close()
                        except Exception as e:
                            logger.error(f"Error in email thread for {user_email}: {e}", exc_info=True)
                    
                    # Запустить в отдельном потоке
                    logger.info(f"Starting email thread for {user_email}...")
                    email_thread = threading.Thread(target=run_async_email, daemon=True, name=f"EmailThread-{user_email}")
                    email_thread.start()
                    logger.debug(f"Email thread started: {email_thread.name}")
            else:
                logger.warning(
                    f"Email not provided or empty for user {news_author_id}. "
                    f"Email notification skipped. userEmail value: '{user_email}'"
                )
            
        except Exception as e:
            logger.error(f"Error processing notification event: {e}", exc_info=True)
    
    async def send_email_notification(
        self, 
        user_email: str, 
        title: str, 
        message: str,
        news_title: str,
        comment_body: str
    ):
        """Отправить email уведомление"""
        logger.info(f"Starting email sending process for {user_email}")
        try:
            # Создать HTML версию письма
            logger.debug(f"Creating HTML email body for {user_email}")
            html_body = email_service.create_comment_notification_html(
                news_title, 
                comment_body
            )
            
            # Отправить email
            logger.info(f"Calling email_service.send_email for {user_email}")
            success = await email_service.send_email(
                to_email=user_email,
                subject=title,
                body_text=message,
                body_html=html_body
            )
            
            if success:
                logger.info(f"Email notification sent successfully to {user_email}")
            else:
                logger.warning(f"Email service returned False for {user_email} - email may not have been sent")
                
        except Exception as e:
            logger.error(f"Error sending email notification to {user_email}: {e}", exc_info=True)
            raise
    
    def stop(self):
        """Остановить consumer"""
        if self.consumer:
            self.consumer.close()
            logger.info("Kafka consumer stopped")

def get_notifications_db():
    """Получить хранилище уведомлений (для использования в main.py)"""
    return _notifications_db


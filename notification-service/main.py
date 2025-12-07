from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import Optional, List
from contextlib import asynccontextmanager
import uvicorn
from datetime import datetime
import threading
import logging
from kafka_consumer import CommentNotificationConsumer, get_notifications_db

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Запуск Kafka Consumer в фоновом потоке
kafka_consumer = None
consumer_thread = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Управление жизненным циклом приложения"""
    # Startup
    global kafka_consumer, consumer_thread
    try:
        logger.info("🚀 Starting Kafka consumer...")
        kafka_consumer = CommentNotificationConsumer()
        consumer_thread = threading.Thread(target=kafka_consumer.start, daemon=True)
        consumer_thread.start()
        logger.info("✅ Kafka consumer thread started")
    except Exception as e:
        logger.error(f"❌ Failed to start Kafka consumer thread: {e}")
        logger.info("💡 Notification service will continue without Kafka consumer")
    
    yield
    
    # Shutdown
    if kafka_consumer:
        kafka_consumer.stop()
        logger.info("Kafka consumer stopped")

app = FastAPI(title="Notification Service", version="1.0.0", lifespan=lifespan)

# CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Models
class NotificationRequest(BaseModel):
    user_id: str
    title: str
    message: str
    notification_type: Optional[str] = "info"  # info, warning, error, success
    priority: Optional[str] = "normal"  # low, normal, high, urgent

class NotificationResponse(BaseModel):
    id: str
    user_id: str
    title: str
    message: str
    notification_type: str
    priority: str
    created_at: str
    status: str  # sent, pending, failed

class NotificationStatus(BaseModel):
    status: str
    message: str

# Используем хранилище из kafka_consumer
notifications_db = get_notifications_db()

@app.get("/")
async def root():
    return {
        "service": "notification-service",
        "status": "running",
        "version": "1.0.0"
    }

@app.get("/health")
async def health():
    return {"status": "healthy", "timestamp": datetime.now().isoformat()}

@app.post("/notifications", response_model=NotificationResponse)
async def create_notification(notification: NotificationRequest):
    """Создать и отправить уведомление"""
    notification_id = f"notif_{datetime.now().timestamp()}"
    
    new_notification = NotificationResponse(
        id=notification_id,
        user_id=notification.user_id,
        title=notification.title,
        message=notification.message,
        notification_type=notification.notification_type,
        priority=notification.priority,
        created_at=datetime.now().isoformat(),
        status="sent"
    )
    
    notifications_db.append(new_notification.dict())
    
    return new_notification

@app.get("/notifications", response_model=List[NotificationResponse])
async def get_notifications(user_id: Optional[str] = None):
    """Получить список уведомлений"""
    if user_id:
        user_notifications = [
            n for n in notifications_db 
            if n.get("user_id") == user_id
        ]
        return user_notifications
    return notifications_db

@app.get("/notifications/{notification_id}", response_model=NotificationResponse)
async def get_notification(notification_id: str):
    """Получить уведомление по ID"""
    notification = next(
        (n for n in notifications_db if n.get("id") == notification_id),
        None
    )
    if not notification:
        raise HTTPException(status_code=404, detail="Notification not found")
    return notification

@app.delete("/notifications/{notification_id}", response_model=NotificationStatus)
async def delete_notification(notification_id: str):
    """Удалить уведомление"""
    global notifications_db
    notifications_db = [
        n for n in notifications_db if n.get("id") != notification_id
    ]
    return NotificationStatus(status="success", message="Notification deleted")

@app.post("/notifications/batch", response_model=List[NotificationResponse])
async def create_batch_notifications(notifications: List[NotificationRequest]):
    """Создать несколько уведомлений"""
    created_notifications = []
    for notification in notifications:
        notification_id = f"notif_{datetime.now().timestamp()}_{len(created_notifications)}"
        new_notification = NotificationResponse(
            id=notification_id,
            user_id=notification.user_id,
            title=notification.title,
            message=notification.message,
            notification_type=notification.notification_type,
            priority=notification.priority,
            created_at=datetime.now().isoformat(),
            status="sent"
        )
        notifications_db.append(new_notification.dict())
        created_notifications.append(new_notification)
    return created_notifications

if __name__ == "__main__":
    # Жестко заданный порт
    port = 8082
    uvicorn.run(app, host="0.0.0.0", port=port)


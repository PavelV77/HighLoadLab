import smtplib
import ssl
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import logging
from typing import Optional

logger = logging.getLogger(__name__)

class EmailService:
    def __init__(self):
        # Жестко заданные настройки SMTP (из mail.env)
        self.smtp_host = "smtp.mail.ru"
        self.smtp_port = 465
        self.smtp_user = "vasilievpavel77@mail.ru"
        self.smtp_password = "ka5buQT1rPwpSf6j9cs9"
        self.smtp_from = "vasilievpavel77@mail.ru"
        self.smtp_use_tls = True
        
        logger.info(f"📧 Email service initialized with SMTP: {self.smtp_host}:{self.smtp_port}")
    
    async def send_email(
        self, 
        to_email: str, 
        subject: str, 
        body_text: str, 
        body_html: Optional[str] = None
    ) -> bool:
        """
        Отправить email уведомление
        
        Args:
            to_email: Email получателя
            subject: Тема письма
            body_text: Текстовая версия письма
            body_html: HTML версия письма (опционально)
        
        Returns:
            True если письмо отправлено успешно, False в противном случае
        """
        # Настройки жестко заданы, проверка не нужна
        
        # Создать сообщение
        message = MIMEMultipart("alternative")
        message["Subject"] = subject
        message["From"] = self.smtp_from
        message["To"] = to_email
        
        # Добавить текстовую версию
        text_part = MIMEText(body_text, "plain", "utf-8")
        message.attach(text_part)
        
        # Добавить HTML версию, если предоставлена
        if body_html:
            html_part = MIMEText(body_html, "html", "utf-8")
            message.attach(html_part)
        
        try:
            logger.info(f"🔌 Connecting to SMTP server {self.smtp_host}:{self.smtp_port}...")
            
            # Для порта 465 используется SMTP_SSL, для 587 - SMTP с starttls
            if self.smtp_port == 465:
                # Порт 465 требует SSL с самого начала
                logger.info("Using SMTP_SSL for port 465")
                context = ssl.create_default_context()
                with smtplib.SMTP_SSL(self.smtp_host, self.smtp_port, context=context) as server:
                    logger.info(f"🔐 Logging in as {self.smtp_user}...")
                    server.login(self.smtp_user, self.smtp_password)
                    logger.info(f"📤 Sending email to {to_email}...")
                    server.sendmail(self.smtp_from, to_email, message.as_string())
                    logger.info(f"✅ Email sent successfully to {to_email} (subject: {subject})")
            else:
                # Порт 587 использует TLS
                logger.info("Using SMTP with STARTTLS for port 587")
                context = ssl.create_default_context()
                with smtplib.SMTP(self.smtp_host, self.smtp_port) as server:
                    if self.smtp_use_tls:
                        logger.info("Starting TLS...")
                        server.starttls(context=context)
                    logger.info(f"🔐 Logging in as {self.smtp_user}...")
                    server.login(self.smtp_user, self.smtp_password)
                    logger.info(f"📤 Sending email to {to_email}...")
                    server.sendmail(self.smtp_from, to_email, message.as_string())
                    logger.info(f"✅ Email sent successfully to {to_email} (subject: {subject})")
            
            return True
            
        except smtplib.SMTPAuthenticationError as e:
            logger.error(
                f"❌ SMTP authentication failed for {to_email}: {e}\n"
                f"   Check SMTP_USER and SMTP_PASSWORD settings.\n"
                f"   For Gmail, use App Password, not regular password."
            )
            return False
        except smtplib.SMTPException as e:
            logger.error(
                f"❌ SMTP error sending email to {to_email}: {e}\n"
                f"   SMTP_HOST: {self.smtp_host}\n"
                f"   SMTP_PORT: {self.smtp_port}"
            )
            return False
        except Exception as e:
            logger.error(
                f"❌ Failed to send email to {to_email}: {e}\n"
                f"   Check SMTP settings and network connection.",
                exc_info=True
            )
            return False
    
    def create_comment_notification_html(self, news_title: str, comment_body: str) -> str:
        """
        Создать HTML версию письма для уведомления о комментарии
        
        Args:
            news_title: Заголовок новости
            comment_body: Текст комментария
        
        Returns:
            HTML строка письма
        """
        return f"""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body {{
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #333;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                }}
                .header {{
                    background-color: #4CAF50;
                    color: white;
                    padding: 20px;
                    text-align: center;
                    border-radius: 5px 5px 0 0;
                }}
                .content {{
                    background-color: #f9f9f9;
                    padding: 20px;
                    border: 1px solid #ddd;
                    border-top: none;
                }}
                .news-title {{
                    font-size: 18px;
                    font-weight: bold;
                    color: #2c3e50;
                    margin-bottom: 15px;
                }}
                .comment {{
                    background-color: white;
                    padding: 15px;
                    border-left: 4px solid #4CAF50;
                    margin: 15px 0;
                    font-style: italic;
                }}
                .footer {{
                    text-align: center;
                    margin-top: 20px;
                    color: #666;
                    font-size: 12px;
                }}
            </style>
        </head>
        <body>
            <div class="header">
                <h2>Новый комментарий к вашей новости!</h2>
            </div>
            <div class="content">
                <p>К вашей новости добавлен новый комментарий:</p>
                <div class="news-title">"{news_title}"</div>
                <div class="comment">
                    {comment_body}
                </div>
                <p>Перейдите в приложение, чтобы просмотреть комментарий и ответить.</p>
            </div>
            <div class="footer">
                <p>С уважением,<br>Команда News App</p>
            </div>
        </body>
        </html>
        """


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
        
        logger.info(f"Email service initialized with SMTP: {self.smtp_host}:{self.smtp_port}")
    
    async def send_email(
        self, 
        to_email: str, 
        subject: str, 
        body_text: str, 
        body_html: Optional[str] = None
    ) -> bool:
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
            logger.info(f"Connecting to SMTP server {self.smtp_host}:{self.smtp_port}...")
            logger.info("Using SMTP_SSL for port 465")
            context = ssl.create_default_context()
            with smtplib.SMTP_SSL(self.smtp_host, self.smtp_port, context=context) as server:
                logger.info(f"Logging in as {self.smtp_user}...")
                server.login(self.smtp_user, self.smtp_password)
                logger.info(f"Sending email to {to_email}...")
                server.sendmail(self.smtp_from, to_email, message.as_string())
                logger.info(f"Email sent successfully to {to_email} (subject: {subject})")
            return True
            
        except Exception as e:
            logger.error(
                f"Failed to send email to {to_email}: {e}\n"
                f"Check SMTP settings and network connection.",
                exc_info=True
            )
            return False
    
    def create_comment_notification_html(self, news_title: str, comment_body: str) -> str:
        return f"""
        <!DOCTYPE html>
        <html>
        <body>
            <div>
                <h2>Новый комментарий к вашей новости!</h2>
            </div>
            <div class="content">
                <p>К вашей новости добавлен новый комментарий:</p>
                <div>"{news_title}"</div>
                <div>
                    {comment_body}
                </div>
                <p>Перейдите в приложение, чтобы просмотреть комментарий и ответить.</p>
            </div>
            <div>
                <p>С уважением,<br>Команда News App</p>
            </div>
        </body>
        </html>
        """


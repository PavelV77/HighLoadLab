-- Создание таблицы для пользователей авторизации
CREATE TABLE IF NOT EXISTS webcoursenews.auth_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    insert_at BIGINT NOT NULL,
    update_at BIGINT NOT NULL
);

-- Создание индекса для быстрого поиска по login
CREATE INDEX IF NOT EXISTS idx_auth_user_login ON webcoursenews.auth_user(login);

-- Создание индекса для быстрого поиска по email
CREATE INDEX IF NOT EXISTS idx_auth_user_email ON webcoursenews.auth_user(email);




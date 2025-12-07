-- Создание схемы
CREATE SCHEMA IF NOT EXISTS webcoursenews;

-- Функция для автоматического обновления insert_at и update_at
CREATE OR REPLACE FUNCTION webcoursenews.update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        NEW.insert_at := EXTRACT(EPOCH FROM NOW()) * 1000;
        NEW.update_at := EXTRACT(EPOCH FROM NOW()) * 1000;
    ELSIF TG_OP = 'UPDATE' THEN
        NEW.update_at := EXTRACT(EPOCH FROM NOW()) * 1000;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание таблицы customer (User)
CREATE TABLE IF NOT EXISTS webcoursenews.customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_login VARCHAR(255) NOT NULL UNIQUE,
    insert_at BIGINT NOT NULL,
    update_at BIGINT NOT NULL
);

-- Создание таблицы news
CREATE TABLE IF NOT EXISTS webcoursenews.news (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    head VARCHAR(255),
    body TEXT,
    customer_id UUID NOT NULL,
    insert_at BIGINT NOT NULL,
    update_at BIGINT NOT NULL,
    CONSTRAINT fk_news_customer FOREIGN KEY (customer_id) 
        REFERENCES webcoursenews.customer(id) ON DELETE CASCADE
);

-- Создание таблицы activity (Like)
CREATE TABLE IF NOT EXISTS webcoursenews.activity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type_of_activity INTEGER NOT NULL,
    customer_id UUID NOT NULL,
    news_id UUID NOT NULL,
    insert_at BIGINT NOT NULL,
    update_at BIGINT NOT NULL,
    CONSTRAINT fk_activity_customer FOREIGN KEY (customer_id) 
        REFERENCES webcoursenews.customer(id) ON DELETE CASCADE,
    CONSTRAINT fk_activity_news FOREIGN KEY (news_id) 
        REFERENCES webcoursenews.news(id) ON DELETE CASCADE
);

-- Создание таблицы comment
CREATE TABLE IF NOT EXISTS webcoursenews.comment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    body TEXT,
    customer_id UUID,
    news_id UUID,
    insert_at BIGINT NOT NULL,
    update_at BIGINT NOT NULL,
    CONSTRAINT fk_comment_customer FOREIGN KEY (customer_id) 
        REFERENCES webcoursenews.customer(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_news FOREIGN KEY (news_id) 
        REFERENCES webcoursenews.news(id) ON DELETE CASCADE
);

-- Создание триггеров для автоматического обновления timestamp
CREATE TRIGGER customer_update_timestamp
    BEFORE INSERT OR UPDATE ON webcoursenews.customer
    FOR EACH ROW
    EXECUTE FUNCTION webcoursenews.update_timestamp();

CREATE TRIGGER news_update_timestamp
    BEFORE INSERT OR UPDATE ON webcoursenews.news
    FOR EACH ROW
    EXECUTE FUNCTION webcoursenews.update_timestamp();

CREATE TRIGGER activity_update_timestamp
    BEFORE INSERT OR UPDATE ON webcoursenews.activity
    FOR EACH ROW
    EXECUTE FUNCTION webcoursenews.update_timestamp();

CREATE TRIGGER comment_update_timestamp
    BEFORE INSERT OR UPDATE ON webcoursenews.comment
    FOR EACH ROW
    EXECUTE FUNCTION webcoursenews.update_timestamp();

-- Создание индексов для улучшения производительности
CREATE INDEX IF NOT EXISTS idx_news_customer_id ON webcoursenews.news(customer_id);
CREATE INDEX IF NOT EXISTS idx_activity_customer_id ON webcoursenews.activity(customer_id);
CREATE INDEX IF NOT EXISTS idx_activity_news_id ON webcoursenews.activity(news_id);
CREATE INDEX IF NOT EXISTS idx_activity_type ON webcoursenews.activity(type_of_activity);
CREATE INDEX IF NOT EXISTS idx_comment_customer_id ON webcoursenews.comment(customer_id);
CREATE INDEX IF NOT EXISTS idx_comment_news_id ON webcoursenews.comment(news_id);


-- 创建会话表
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL
);

-- 创建会话消息表
CREATE TABLE IF NOT EXISTS conversation_messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender VARCHAR(32) NOT NULL,
    content VARCHAR(4000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_conversation
        FOREIGN KEY (conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE
);

-- 创建FAQ表
CREATE TABLE IF NOT EXISTS faq_entries (
    id BIGSERIAL PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    answer VARCHAR(3000) NOT NULL,
    category VARCHAR(100)
);

-- 创建FAQ标签表（用于ElementCollection）
CREATE TABLE IF NOT EXISTS faq_tags (
    faq_id BIGINT NOT NULL,
    tag VARCHAR(255),
    CONSTRAINT fk_faq_tags
        FOREIGN KEY (faq_id)
        REFERENCES faq_entries(id)
        ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_customer_email ON conversations(customer_email);
CREATE INDEX IF NOT EXISTS idx_status ON conversations(status);
CREATE INDEX IF NOT EXISTS idx_started_at ON conversations(started_at);
CREATE INDEX IF NOT EXISTS idx_conversation_messages_conversation_id ON conversation_messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_conversation_messages_created_at ON conversation_messages(created_at);
CREATE INDEX IF NOT EXISTS idx_faq_entries_category ON faq_entries(category);
CREATE INDEX IF NOT EXISTS idx_faq_tags_faq_id ON faq_tags(faq_id);

-- 插入示例FAQ数据
INSERT INTO faq_entries (question, answer, category) VALUES
('如何重置密码？', '您可以通过点击登录页面的"忘记密码"链接来重置密码。系统会向您的注册邮箱发送重置链接。', '账户管理'),
('支持哪些支付方式？', '我们支持信用卡、借记卡、支付宝和微信支付。所有支付都通过安全加密通道处理。', '支付问题'),
('如何联系客服？', '您可以通过以下方式联系我们：1) 在线聊天（工作时间9:00-18:00）2) 发送邮件至support@example.com 3) 拨打客服热线400-123-4567', '客户服务'),
('订单多久能发货？', '一般情况下，订单会在24小时内处理并发货。节假日可能会有延迟，具体以实际情况为准。', '订单配送'),
('可以退货吗？', '商品自收到之日起7天内，如未使用且包装完好，可以申请退货。部分特殊商品除外，详见退货政策。', '退换货政策')
ON CONFLICT DO NOTHING;

-- 插入示例FAQ标签数据
INSERT INTO faq_tags (faq_id, tag) VALUES
(1, '密码'),
(1, '账户'),
(2, '支付'),
(2, '付款方式'),
(3, '客服'),
(3, '联系方式'),
(4, '配送'),
(4, '物流'),
(5, '退货'),
(5, '售后')
ON CONFLICT DO NOTHING;

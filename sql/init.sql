-- =====================================================
-- 校园二手物品智能匹配平台 数据库初始化脚本
-- 数据库：campus_secondhand
-- MySQL 8.0+
-- 字符集：utf8mb4
-- 引擎：InnoDB
-- =====================================================

-- 创建数据库
DROP DATABASE IF EXISTS `campus_secondhand`;
CREATE DATABASE `campus_secondhand`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `campus_secondhand`;

-- =====================================================
-- 1. 用户表
-- =====================================================
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '用户名，用于登录',
    `password`      VARCHAR(255)    NOT NULL                 COMMENT '密码（后续改为BCrypt加密存储）',
    `nickname`      VARCHAR(50)     DEFAULT NULL             COMMENT '昵称',
    `avatar`        VARCHAR(500)    DEFAULT NULL             COMMENT '头像URL',
    `phone`         VARCHAR(20)     DEFAULT NULL             COMMENT '手机号',
    `email`         VARCHAR(100)    DEFAULT NULL             COMMENT '邮箱',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-正常',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`),
    UNIQUE KEY `uk_user_phone`     (`phone`),
    UNIQUE KEY `uk_user_email`     (`email`),
    KEY `idx_user_status`          (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 管理员表
-- =====================================================
CREATE TABLE `admin` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '管理员ID',
    `username`      VARCHAR(50)     NOT NULL                 COMMENT '管理员用户名',
    `password`      VARCHAR(255)    NOT NULL                 COMMENT '密码（后续改为BCrypt加密存储）',
    `nickname`      VARCHAR(50)     DEFAULT NULL             COMMENT '管理员昵称',
    `role`          VARCHAR(20)     NOT NULL DEFAULT 'admin' COMMENT '角色：admin-超级管理员，editor-内容编辑',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-正常',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_username` (`username`),
    KEY `idx_admin_role`           (`role`),
    KEY `idx_admin_status`         (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- =====================================================
-- 3. 商品分类表
-- =====================================================
CREATE TABLE `category` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    `name`          VARCHAR(50)     NOT NULL                 COMMENT '分类名称',
    `parent_id`     BIGINT          NOT NULL DEFAULT 0       COMMENT '父分类ID，0表示顶级分类',
    `icon`          VARCHAR(255)    DEFAULT NULL             COMMENT '分类图标URL',
    `sort`          INT             NOT NULL DEFAULT 0       COMMENT '排序值，越小越靠前',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：0-禁用，1-启用',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_parent_id` (`parent_id`),
    KEY `idx_category_sort`      (`sort`),
    KEY `idx_category_status`    (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- =====================================================
-- 4. 商品表
-- =====================================================
CREATE TABLE `goods` (
    `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '商品ID',
    `user_id`         BIGINT          NOT NULL                 COMMENT '发布用户ID',
    `category_id`     BIGINT          NOT NULL                 COMMENT '所属分类ID',
    `title`           VARCHAR(200)    NOT NULL                 COMMENT '商品标题',
    `description`     TEXT            DEFAULT NULL             COMMENT '商品描述',
    `price`           DECIMAL(10,2)   NOT NULL                 COMMENT '商品价格（元）',
    `condition_level` VARCHAR(20)     NOT NULL DEFAULT 'moderate' COMMENT '新旧程度：brand_new-全新，like_new-几乎全新，good-良好，moderate-一般，worn-老旧',
    `trade_location`  VARCHAR(200)    DEFAULT NULL             COMMENT '交易地点（如：XX校区XX楼）',
    `contact_info`    VARCHAR(200)    DEFAULT NULL             COMMENT '联系方式（QQ/微信/手机号）',
    `negotiable`      TINYINT         NOT NULL DEFAULT 0       COMMENT '是否可议价：0-否，1-是',
    `view_count`      INT             NOT NULL DEFAULT 0       COMMENT '浏览次数',
    `favorite_count`  INT             NOT NULL DEFAULT 0       COMMENT '收藏次数',
    `status`          VARCHAR(20)     NOT NULL DEFAULT 'approved' COMMENT '状态：pending-待审核，approved-审核通过，rejected-审核不通过，offline-已下架，sold-已交易',
    `create_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_goods_user_id`      (`user_id`),
    KEY `idx_goods_category_id`  (`category_id`),
    KEY `idx_goods_status`       (`status`),
    KEY `idx_goods_price`        (`price`),
    KEY `idx_goods_condition`    (`condition_level`),
    KEY `idx_goods_create_time`  (`create_time`),
    FULLTEXT KEY `ft_goods_title_desc` (`title`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =====================================================
-- 5. 商品图片表
-- =====================================================
CREATE TABLE `goods_image` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '图片ID',
    `goods_id`      BIGINT          NOT NULL                 COMMENT '所属商品ID',
    `image_url`     VARCHAR(500)    NOT NULL                 COMMENT '图片URL',
    `sort`          INT             NOT NULL DEFAULT 0       COMMENT '排序值，越小越靠前，第一张为主图',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_goods_image_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- =====================================================
-- 6. 求购需求表
-- =====================================================
CREATE TABLE `demand` (
    `id`                  BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '需求ID',
    `user_id`             BIGINT          NOT NULL                 COMMENT '发布用户ID',
    `category_id`         BIGINT          NOT NULL                 COMMENT '期望分类ID',
    `title`               VARCHAR(200)    NOT NULL                 COMMENT '需求标题',
    `description`         TEXT            DEFAULT NULL             COMMENT '需求描述',
    `min_price`           DECIMAL(10,2)   DEFAULT NULL             COMMENT '最低接受价格（元）',
    `max_price`           DECIMAL(10,2)   DEFAULT NULL             COMMENT '最高接受价格（元）',
    `expected_condition`  VARCHAR(20)     DEFAULT NULL             COMMENT '期望新旧程度：brand_new-全新，like_new-几乎全新，good-良好，moderate-一般，worn-老旧',
    `expected_location`   VARCHAR(200)    DEFAULT NULL             COMMENT '期望交易地点',
    `keywords`            VARCHAR(500)    DEFAULT NULL             COMMENT '关键词，逗号分隔，用于匹配计算',
    `status`              VARCHAR(20)     NOT NULL DEFAULT 'approved' COMMENT '状态：pending-待审核，approved-审核通过，rejected-审核不通过，closed-已关闭',
    `create_time`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_demand_user_id`      (`user_id`),
    KEY `idx_demand_category_id`  (`category_id`),
    KEY `idx_demand_status`       (`status`),
    KEY `idx_demand_price`        (`min_price`, `max_price`),
    KEY `idx_demand_create_time`  (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='求购需求表';

-- =====================================================
-- 7. 收藏表
-- =====================================================
CREATE TABLE `favorite` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `goods_id`      BIGINT          NOT NULL                 COMMENT '商品ID',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_favorite_user_goods` (`user_id`, `goods_id`),
    KEY `idx_favorite_user_id`         (`user_id`),
    KEY `idx_favorite_goods_id`        (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =====================================================
-- 8. 交易意向表
-- =====================================================
CREATE TABLE `trade_order` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '交易意向ID',
    `buyer_id`      BIGINT          NOT NULL                 COMMENT '买家用户ID（发起交易意向方）',
    `seller_id`     BIGINT          NOT NULL                 COMMENT '卖家用户ID（商品发布方）',
    `goods_id`      BIGINT          NOT NULL                 COMMENT '商品ID',
    `message`       VARCHAR(500)    DEFAULT NULL             COMMENT '买家留言',
    `status`        VARCHAR(20)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待确认，accepted-已接受，rejected-已拒绝，completed-已完成，cancelled-已取消',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_trade_order_buyer_id`   (`buyer_id`),
    KEY `idx_trade_order_seller_id`  (`seller_id`),
    KEY `idx_trade_order_goods_id`   (`goods_id`),
    KEY `idx_trade_order_status`     (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易意向表';

-- =====================================================
-- 9. 浏览记录表
-- =====================================================
CREATE TABLE `browse_history` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `goods_id`      BIGINT          NOT NULL                 COMMENT '商品ID',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `idx_browse_history_user_id`  (`user_id`),
    KEY `idx_browse_history_goods_id` (`goods_id`),
    KEY `idx_browse_history_time`     (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='浏览记录表';

-- =====================================================
-- 10. 搜索记录表
-- =====================================================
CREATE TABLE `search_history` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `keyword`       VARCHAR(200)    NOT NULL                 COMMENT '搜索关键词',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
    PRIMARY KEY (`id`),
    KEY `idx_search_history_user_id`  (`user_id`),
    KEY `idx_search_history_keyword`  (`keyword`),
    KEY `idx_search_history_time`     (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索记录表';

-- =====================================================
-- 11. 推荐记录表
-- =====================================================
CREATE TABLE `recommend_record` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '用户ID',
    `demand_id`     BIGINT          NOT NULL                 COMMENT '求购需求ID',
    `goods_id`      BIGINT          NOT NULL                 COMMENT '匹配商品ID',
    `match_score`   DECIMAL(5,2)    NOT NULL DEFAULT 0.00    COMMENT '匹配分数（0.00-100.00）',
    `match_level`   VARCHAR(20)     NOT NULL DEFAULT 'low'   COMMENT '匹配等级：high-高匹配(>=70)，medium-中匹配(40-69)，low-低匹配(<40)',
    `match_reason`  VARCHAR(1000)   DEFAULT NULL             COMMENT '匹配原因说明（JSON或文本描述各维度匹配情况）',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '推荐时间',
    PRIMARY KEY (`id`),
    KEY `idx_recommend_user_id`     (`user_id`),
    KEY `idx_recommend_demand_id`   (`demand_id`),
    KEY `idx_recommend_goods_id`    (`goods_id`),
    KEY `idx_recommend_score`       (`match_score`),
    KEY `idx_recommend_level`       (`match_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐记录表';

-- =====================================================
-- 12. 举报表
-- =====================================================
CREATE TABLE `report` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '举报ID',
    `user_id`       BIGINT          NOT NULL                 COMMENT '举报人用户ID',
    `target_type`   VARCHAR(20)     NOT NULL                 COMMENT '举报目标类型：goods-商品，user-用户',
    `target_id`     BIGINT          NOT NULL                 COMMENT '举报目标ID',
    `reason`        VARCHAR(500)    NOT NULL                 COMMENT '举报原因',
    `status`        VARCHAR(20)     NOT NULL DEFAULT 'pending' COMMENT '处理状态：pending-待处理，resolved-已处理，dismissed-已驳回',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
    PRIMARY KEY (`id`),
    KEY `idx_report_user_id`       (`user_id`),
    KEY `idx_report_target`        (`target_type`, `target_id`),
    KEY `idx_report_status`        (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报表';

-- =====================================================
-- 13. 公告表
-- =====================================================
CREATE TABLE `notice` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '公告ID',
    `title`         VARCHAR(200)    NOT NULL                 COMMENT '公告标题',
    `content`       TEXT            NOT NULL                 COMMENT '公告内容',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：0-下架，1-发布',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_notice_status`      (`status`),
    KEY `idx_notice_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- =====================================================
-- ==================== 插入测试数据 ====================
-- =====================================================

-- -----------------------------------------------------
-- 管理员数据
-- -----------------------------------------------------
-- BCrypt 加密密码: 123456
INSERT INTO `admin` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', 'admin', 1);

-- -----------------------------------------------------
-- 普通用户数据
-- -----------------------------------------------------
-- BCrypt 加密密码: 123456
INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `phone`, `email`, `status`) VALUES
('student001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '小明', '/upload/avatar/default.png', '13800000001', 'student001@campus.edu', 1),
('student002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '小红', '/upload/avatar/default.png', '13800000002', 'student002@campus.edu', 1);

-- -----------------------------------------------------
-- 商品分类数据
-- -----------------------------------------------------
INSERT INTO `category` (`id`, `name`, `parent_id`, `icon`, `sort`, `status`) VALUES
(1,  '数码电子', 0, '/upload/category/digital.png',   1, 1),
(2,  '图书教材', 0, '/upload/category/book.png',      2, 1),
(3,  '生活用品', 0, '/upload/category/life.png',      3, 1),
(4,  '运动户外', 0, '/upload/category/sport.png',     4, 1),
(5,  '服饰鞋包', 0, '/upload/category/fashion.png',   5, 1),
(6,  '学习用品', 0, '/upload/category/study.png',     6, 1);

-- 数码电子子分类
INSERT INTO `category` (`id`, `name`, `parent_id`, `icon`, `sort`, `status`) VALUES
(7,  '手机平板', 1, NULL, 1, 1),
(8,  '电脑配件', 1, NULL, 2, 1),
(9,  '耳机音箱', 1, NULL, 3, 1),
(10, '其他数码', 1, NULL, 99, 1);

-- -----------------------------------------------------
-- 商品数据（用于后续智能匹配测试）
-- -----------------------------------------------------
INSERT INTO `goods` (`id`, `user_id`, `category_id`, `title`, `description`, `price`, `condition_level`, `trade_location`, `contact_info`, `negotiable`, `view_count`, `favorite_count`, `status`) VALUES
(1, 1, 7, 'iPad Air 5 64G 深空灰 99新',
    '2024年购入，使用不到半年，无磕碰无划痕，电池健康度98%，包装配件齐全。因换Pro出售。',
    3200.00, 'like_new', '东校区图书馆一楼', 'QQ: 123456789', 1, 156, 12, 'approved'),

(2, 2, 7, '华为平板MatePad 11 2023款',
    '去年双11购入，8+128G版本，屏幕完美，日常看网课做笔记用的，考研结束出掉。',
    1800.00, 'good', '西校区食堂门口', '微信: hong_student', 1, 89, 8, 'approved'),

(3, 1, 2, '《高等数学》第七版 同济大学',
    '大一时候买的，书况良好，仅前两章有少量笔记，其他章节几乎全新。',
    25.00, 'good', '东校区3号教学楼', 'QQ: 123456789', 0, 203, 15, 'approved'),

(4, 2, 2, '《Java编程思想》第四版 中文版',
    '经典Java入门书籍，适合计算机专业同学。有少量划线标注，不影响阅读。',
    35.00, 'moderate', '西校区图书馆', '微信: hong_student', 1, 67, 5, 'approved'),

(5, 1, 4, '捷安特ATX 860 山地自行车 27速',
    '骑了一年，车况良好，变速顺畅，刹车灵敏。送车锁和水壶架。校内交易方便。',
    850.00, 'good', '东校区体育馆旁', 'QQ: 123456789', 1, 312, 22, 'approved'),

(6, 2, 3, '宿舍用台灯 LED护眼灯 可调光',
    '三档调光调色温，USB充电，夹子式不占桌面空间。用了三个月，功能完好。',
    45.00, 'like_new', '西校区宿舍楼5栋', '微信: hong_student', 0, 45, 3, 'approved'),

(7, 1, 6, '考研英语二真题 2025版 张剑黄皮书',
    '全新未拆封，买多了出一套。2025年最新版，英语二专用。',
    40.00, 'brand_new', '东校区图书馆', 'QQ: 123456789', 0, 78, 6, 'approved'),

(8, 2, 5, 'Nike 双肩包 灰色 大容量',
    '八成新，无破损，拉链顺滑，容量大，上课装书装电脑都合适。',
    80.00, 'good', '西校区教学楼', '微信: hong_student', 1, 134, 9, 'approved');

-- -----------------------------------------------------
-- 商品图片数据
-- -----------------------------------------------------
INSERT INTO `goods_image` (`goods_id`, `image_url`, `sort`) VALUES
(1, '/upload/goods/ipad_air_01.jpg', 0),
(1, '/upload/goods/ipad_air_02.jpg', 1),
(2, '/upload/goods/huawei_pad_01.jpg', 0),
(3, '/upload/goods/math_book_01.jpg', 0),
(4, '/upload/goods/java_book_01.jpg', 0),
(5, '/upload/goods/bike_01.jpg', 0),
(5, '/upload/goods/bike_02.jpg', 1),
(6, '/upload/goods/lamp_01.jpg', 0),
(7, '/upload/goods/english_book_01.jpg', 0),
(8, '/upload/goods/bag_01.jpg', 0);

-- -----------------------------------------------------
-- 求购需求数据（与商品数据形成匹配关系，用于后续智能匹配测试）
-- -----------------------------------------------------
INSERT INTO `demand` (`id`, `user_id`, `category_id`, `title`, `description`, `min_price`, `max_price`, `expected_condition`, `expected_location`, `keywords`, `status`) VALUES
(1, 2, 7, '求购iPad平板，用于考研学习',
    '求购一台iPad，主要用于看网课、做笔记，考研备考用。Air或Pro系列均可，64G以上。',
    2500.00, 4000.00, 'like_new', '东校区或西校区', 'iPad,平板,考研,学习', 'approved'),

(2, 1, 7, '求购一台平板电脑',
    '预算有限，求购一台Android平板，主要用于看PDF和视频。',
    1000.00, 2000.00, 'good', '东校区', '平板,安卓,华为,小米', 'approved'),

(3, 1, 2, '求购高等数学教材',
    '需要同济大学《高等数学》第七版，上下册都可以。',
    15.00, 40.00, 'good', '东校区', '高数,高等数学,同济,教材', 'approved'),

(4, 2, 4, '求购二手自行车',
    '求购一辆二手自行车，用于校内代步，山地车或普通车都可以。',
    500.00, 1000.00, 'good', '校内', '自行车,山地车,代步', 'approved'),

(5, 1, 2, '求购Java编程书籍',
    '计算机专业学生，求购Java相关编程书籍，入门或进阶均可。',
    20.00, 50.00, 'moderate', '东校区', 'Java,编程,书籍,计算机', 'approved');

-- -----------------------------------------------------
-- 收藏数据
-- -----------------------------------------------------
INSERT INTO `favorite` (`user_id`, `goods_id`) VALUES
(1, 2), (1, 6), (1, 8),
(2, 1), (2, 3), (2, 5);

-- -----------------------------------------------------
-- 浏览记录数据
-- -----------------------------------------------------
INSERT INTO `browse_history` (`user_id`, `goods_id`, `create_time`) VALUES
(1, 2, '2025-06-15 10:30:00'),
(1, 4, '2025-06-15 10:35:00'),
(1, 6, '2025-06-16 14:20:00'),
(1, 8, '2025-06-16 14:25:00'),
(2, 1, '2025-06-15 09:00:00'),
(2, 3, '2025-06-15 09:10:00'),
(2, 5, '2025-06-16 16:00:00'),
(2, 7, '2025-06-16 16:15:00');

-- -----------------------------------------------------
-- 搜索记录数据
-- -----------------------------------------------------
INSERT INTO `search_history` (`user_id`, `keyword`, `create_time`) VALUES
(1, '平板',     '2025-06-15 08:00:00'),
(1, '高数',     '2025-06-15 08:05:00'),
(1, 'Java',     '2025-06-16 12:00:00'),
(2, 'iPad',     '2025-06-15 07:30:00'),
(2, '自行车',   '2025-06-16 15:00:00'),
(2, '背包',     '2025-06-16 15:10:00');

-- -----------------------------------------------------
-- 推荐记录数据（模拟匹配结果，用于后续算法测试）
-- -----------------------------------------------------
INSERT INTO `recommend_record` (`user_id`, `demand_id`, `goods_id`, `match_score`, `match_level`, `match_reason`) VALUES
(2, 1, 1, 92.50, 'high',
    '{"category_match": 100, "keyword_match": 85, "price_match": 90, "condition_match": 95, "location_match": 80, "summary": "分类完全匹配，价格在预算范围内，成色满足期望，交易地点接近"}'),

(1, 2, 2, 85.30, 'high',
    '{"category_match": 100, "keyword_match": 80, "price_match": 95, "condition_match": 85, "location_match": 70, "summary": "分类匹配，价格完全在预算范围内，成色良好"}'),

(1, 3, 3, 96.00, 'high',
    '{"category_match": 100, "keyword_match": 100, "price_match": 100, "condition_match": 90, "location_match": 100, "summary": "分类、关键词、价格、地点均完全匹配，高度推荐"}'),

(2, 4, 5, 88.00, 'high',
    '{"category_match": 100, "keyword_match": 90, "price_match": 95, "condition_match": 85, "location_match": 80, "summary": "分类匹配，价格在预算范围内，车况良好"}'),

(1, 5, 4, 78.50, 'high',
    '{"category_match": 100, "keyword_match": 85, "price_match": 90, "condition_match": 70, "location_match": 75, "summary": "分类匹配，价格合适，成色一般但可接受"}'),

-- 一些中低匹配记录用于测试
(2, 1, 2, 55.00, 'medium',
    '{"category_match": 100, "keyword_match": 60, "price_match": 80, "condition_match": 50, "location_match": 50, "summary": "分类匹配但品牌不符，成色略低于期望"}'),

(1, 4, 8, 15.00, 'low',
    '{"category_match": 0, "keyword_match": 10, "price_match": 30, "condition_match": 20, "location_match": 50, "summary": "分类不匹配，非自行车类商品"}');

-- -----------------------------------------------------
-- 公告数据
-- -----------------------------------------------------
INSERT INTO `notice` (`title`, `content`, `status`) VALUES
('欢迎使用校园二手交易平台',
    '欢迎来到校园二手物品智能匹配平台！本平台旨在为同学们提供便捷、安全的二手物品交易服务。发布商品或求购需求后，系统将自动为您匹配最合适的交易对象。请遵守平台规则，诚信交易。',
    1),
('平台交易安全提醒',
    '请同学们注意交易安全：1. 尽量选择校内公共场所交易；2. 验货后再付款；3. 保留交易凭证；4. 如遇纠纷请联系平台客服。祝大家交易愉快！',
    1);
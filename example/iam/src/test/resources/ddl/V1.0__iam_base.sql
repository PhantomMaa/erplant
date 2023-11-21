CREATE TABLE IF NOT EXISTS `idn_user` (
    `id` int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `email` varchar(50) NOT NULL,
    `passwd` varchar(50) NOT NULL,
    `phone` varchar(50),
    `profile` json,
    `oauth` json,
    `saml` json,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `idn_role` (
    `id` int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(50) NOT NULL,
    `biz_type` varchar(50) NOT NULL,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_name` (`biz_type`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `idn_permission` (
    `id` int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `name` varchar(50) NOT NULL,
    `biz_type` varchar(50) NOT NULL,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_name` (`biz_type`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `idn_user_role_rel` (
    `id` int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `uid` int unsigned NOT NULL,
    `role_id` int unsigned NOT NULL,
    `biz_type` varchar(50) NOT NULL,
    `biz_id` varchar(100) NOT NULL,
    `extension` json,
    `attribute` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_bind` (`biz_type`, `biz_id`, `role_id`, `uid`),
    INDEX `idx_user` (`uid`, `biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `idn_role_permission_rel` (
    `id` int unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `role_id` int unsigned NOT NULL,
    `permission_id` int unsigned NOT NULL,
    `biz_type` varchar(50) NOT NULL,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_bind` (`biz_type`, `role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

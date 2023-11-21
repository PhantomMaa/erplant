CREATE TABLE IF NOT EXISTS `kv_group` (
    `id` bigint unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `group_name` varchar(50) NOT NULL,
    `app_name` varchar(50) NOT NULL,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_app_group` (`app_name`, `group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kv_config_item` (
    `id` bigint unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `group_id` int unsigned NOT NULL,
    `item_key` varchar(100) NOT NULL,
    `item_value` text,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_group_itemkey` (`group_id`, `item_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kv_cache_item` (
    `id` bigint unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `group_id` int unsigned NOT NULL,
    `item_key` varchar(100) NOT NULL,
    `item_value` blob NOT NULL,
    `ttl` bigint unsigned,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_group_itemkey` (`group_id`, `item_key`),
    INDEX `idx_kv_cache_item_ttl` (`ttl`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kv_config_item_history` (
    `id` bigint unsigned NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `item_id` int unsigned NOT NULL,
    `item_key` varchar(100),
    `item_value` text,
    `item_version` smallint unsigned NOT NULL DEFAULT 0,
    `extension` json,
    `version` smallint unsigned NOT NULL DEFAULT 0,
    UNIQUE KEY `uk_item_version` (`item_id`, `item_version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN','SELLER') NOT NULL,
    `username` VARCHAR(255) NOT NULL,
    `about_business` VARCHAR(255) DEFAULT NULL,
    `address` VARCHAR(255) DEFAULT NULL,
    `business_name` VARCHAR(255) DEFAULT NULL,
    `city` VARCHAR(255) DEFAULT NULL,
    `gst_number` VARCHAR(255) DEFAULT NULL,
    `owner_name` VARCHAR(255) DEFAULT NULL,
    `pan_number` VARCHAR(255) DEFAULT NULL,
    `phone` VARCHAR(255) DEFAULT NULL,
    `profile_image_url` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
    UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
    UNIQUE KEY `UKq1t7ptahowasiknl2r6y0f859` (`business_name`),
    UNIQUE KEY `UKdu5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `order_entity` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `created_at` DATETIME(6) DEFAULT NULL,
    `customer_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) DEFAULT NULL,
    `phone` VARCHAR(255) NOT NULL,
    `status` ENUM('DELIVERED','RECEIVED') DEFAULT NULL,
    `total_amount` DECIMAL(38,2) DEFAULT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK1aty8556l6ubkgne265npb41f` (`user_id`),
    CONSTRAINT `FK1aty8556l6ubkgne265npb41f`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `garment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `price_per_item` DECIMAL(38,2) DEFAULT NULL,
    `quantity` INT NOT NULL,
    `type` ENUM('BLAZER','COAT_PANT','JEANS','LEHENGA','PANT','SAREE','SHIRT') DEFAULT NULL,
    `order_entity_id` INT DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK4rmgcv5p2hpnxnqm3ri36ti94` (`order_entity_id`),
    CONSTRAINT `FK4rmgcv5p2hpnxnqm3ri36ti94`
        FOREIGN KEY (`order_entity_id`) REFERENCES `order_entity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pricing` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `garment_type` ENUM('BLAZER','COAT_PANT','JEANS','LEHENGA','PANT','SAREE','SHIRT') NOT NULL,
    `price` DECIMAL(38,2) NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKnuuqter5dri6u48s3ohmsn06x` (`user_id`,`garment_type`),
    CONSTRAINT `FKsg4x1one94dtdhm9kw3cppwwf`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `password_reset_token` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `expiry_date` DATETIME(6) NOT NULL,
    `token` VARCHAR(255) NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKg0guo4k8krgpwuagos61oc06j` (`token`),
    UNIQUE KEY `UKf90ivichjaokvmovxpnlm5nin` (`user_id`),
    CONSTRAINT `FK83nsrttkwkb6ym0anu051mtxn`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `refresh_token` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `expiry_date` DATETIME(6) NOT NULL,
    `token` VARCHAR(255) NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKr4k4edos30bx9neoq81mdvwph` (`token`),
    UNIQUE KEY `UKf95ixxe7pa48ryn1awmh2evt7` (`user_id`),
    CONSTRAINT `FKjtx87i0jvq2svedphegvdwcuy`
        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

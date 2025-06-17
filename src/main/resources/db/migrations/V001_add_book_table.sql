CREATE TABLE `books`
(
    `id`          int          NOT NULL AUTO_INCREMENT,
    `title`       varchar(255) NOT NULL,
    `author`      varchar(255) NULL,
    `description` text,
    `created_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);
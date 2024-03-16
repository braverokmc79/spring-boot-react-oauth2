create database shop CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
create user `shop`@`localhost` identified by '1111';
grant all privileges on shop.* to `shop`@`localhost` ;




create user `shopJpaGradle2`@`%` identified by 'shopJpaGradle123';
create database shopJpaGradle2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
grant all privileges on shopJpaGradle2.* to `shopJpaGradle2`@`%` ;





create table item (
                      price integer not null,
                      stock_number integer not null,
                      item_id bigint not null,
                      reg_time datetime(6),
                      update_time datetime(6),
                      item_nm varchar(50) not null,
                      item_detail tinytext not null,
                      item_sell_status enum ('SELL','SOLD_OUT'),
                      primary key (item_id)
) engine=InnoDB;



ALTER TABLE orders MODIFY COLUMN order_status enum('ORDER','CANCEL') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ORDER';



-- 더미 데이터 생성쿼리
DELIMITER $$
DROP PROCEDURE IF EXISTS loopInsert$$

CREATE PROCEDURE loopInsert()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 500 DO

            INSERT INTO item
            (reg_time, update_time, created_by, modified_by, item_detail, item_nm, item_sell_status, price, stock_number)
            VALUES(now(), now(), 'braverokmc79@gamil.com', 'braverokmc79@gamil.com', concat('제목',i), concat('내용',i),  'SELL', 10000, 1000);

            SET i = i + 1;
        END WHILE;
    END$$
DELIMITER $$


CALL loopInsert;




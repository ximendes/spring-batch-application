CREATE TABLE IF NOT EXISTS orders
(
    id         bigserial        NOT NULL ,
    order_ref  VARCHAR(100)   NOT NULL,
    amount     DECIMAL(19, 2) NULL DEFAULT NULL,
    order_date timestamp       NOT NULL,
    note      VARCHAR(1000)  NULL DEFAULT NULL,
    constraint orders_pk primary key (id)
);
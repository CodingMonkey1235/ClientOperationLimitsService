--liquibase formatted sql

--changeset update tables:5
--comment create_table_clients_limits
create table day_limits (
    id bigserial primary key,
    day_limit decimal(10, 2)
);

insert into day_limits (id, day_limit)
values (1, 100000), (2, 50000), (3, 10), (4, 100000), (5, 50000), (6, 10);

create table pending_operations (
    id UUID primary key,
    day_limit_id bigserial references day_limits (id),
    operation_amount decimal(10, 2),
    date_created TIMESTAMP
);
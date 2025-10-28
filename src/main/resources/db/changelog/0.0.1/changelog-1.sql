--liquibase formatted sql

--changeset create_table_clients_limits:1
--comment create_table_clients_limits
create table clients_limits (
    client_id bigserial primary key,
    client_limit decimal
);

insert into clients_limits (client_id, client_limit)
values (1, 100000), (2, 50000), (3, 10);
--rollback truncate table clients_limits;
--liquibase formatted sql

--changeset add_data_table_clients_limits:2
--comment add_data_table_clients_limits

insert into clients_limits (client_id, client_limit)
values (4, 100000), (5, 50000), (6, 10), (7, 100000), (8, 50000), (9, 10);
--rollback truncate table clients_limits;
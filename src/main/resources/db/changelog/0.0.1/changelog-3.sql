--liquibase formatted sql

--changeset add_need_daily_reset:3
--comment add_need_daily_reset

alter table clients_limits
add column if not exists need_daily_reset boolean;

--rollback truncate table clients_limits;
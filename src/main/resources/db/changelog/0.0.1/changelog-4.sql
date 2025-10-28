--liquibase formatted sql

--changeset add_pending_clients_limits:4
--comment pending_clients_limits

create table pending_clients_limits (
    operation_id UUID primary key,
    client_id bigserial,
    pending_limit decimal,
    need_daily_reset boolean

)

--rollback truncate table pending_clients_limits;
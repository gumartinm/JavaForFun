create schema if not exists conciliation;

create table conciliation.conciliation
(
    code varchar(255) not null,
    layer_id integer not null,
    name varchar(255) not null
);


comment on table  conciliation.conciliation         			 is 'Locations conciliation';
comment on column conciliation.conciliation.code                 is 'Location code';
comment on column conciliation.conciliation.layer_id             is 'Layer id';
comment on column conciliation.conciliation.name                 is 'Location name';

alter table conciliation.conciliation add constraint conciliation_conciliation_pk primary key (code, layer_id);

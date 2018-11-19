create schema if not exists conciliation;

create table conciliation.conciliation
(
    code varchar(80) not null,

    layer_id integer not null,

    name varchar(512) not null,

    conciliation_time timestamp NOT NULL
);


comment on table  conciliation.conciliation         			 is 'Locations conciliation';
comment on column conciliation.conciliation.code                 is 'Location code';
comment on column conciliation.conciliation.layer_id             is 'Layer id';
comment on column conciliation.conciliation.name                 is 'Location name';
comment on column conciliation.conciliation.conciliation_time    is 'Last conciliation time';

alter table conciliation.conciliation add constraint conciliation_pk primary key (code, layer_id);

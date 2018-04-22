create table location
(
    id bigint not null,
    parent_id bigint not null,
    description varchar(255) not null,
    gps bytea not null
);

comment on table  location                          is 'Locations';
comment on column location.id                       is 'Location id';
comment on column location.parent_id                is 'Location parent id';
comment on column location.description              is 'Location  name';
comment on column location.gps                      is 'Location geography type';

alter table location add constraint location_pk primary key ( id );
alter table location add constraint location_fk_01 foreign key ( parent_id ) references location ( id ) on delete no action on update no action;
alter table location alter column gps type geography(point,4326) using geography(ST_SetSRID(geometry(gps),4326));

create sequence seq_location_id  start with 1 increment by 1 cache 1;




create table location_hierarchy
(
    id bigint not null,
    parent_id bigint not null,
    child_id bigint not null,
    level_id smallint not null
);

comment on table  location_hierarchy                is 'Locations hierarchy';
comment on column location_hierarchy.id             is 'Location hierarchy id';
comment on column location_hierarchy.parent_id      is 'Location hierarchy parent id';
comment on column location_hierarchy.child_id       is 'Location hierarchy child id';
comment on column location_hierarchy.level_id       is 'Location hierarchy level id';


alter table location_hierarchy add constraint location_hierarchy_pk primary key ( id );
alter table location_hierarchy add constraint location_hierarchy_fk_01 foreign key ( parent_id ) references location ( id ) on delete no action on update no action;
alter table location_hierarchy add constraint location_hierarchy_fk_02 foreign key ( child_id ) references location ( id ) on delete no action on update no action;

create sequence seq_location_hierarchy_id  start with 1 increment by 1 cache 1;

insert INTO location_type (id, name) values
(nextval('seq_location_type_id'), 'ROOT')
,(nextval('seq_location_type_id'), 'WORLD')
,(nextval('seq_location_type_id'), 'CONTINENT')
,(nextval('seq_location_type_id'), 'COUNTRY')
,(nextval('seq_location_type_id'), 'REGION')
,(nextval('seq_location_type_id'), 'CITY')
,(nextval('seq_location_type_id'), 'NEIGHBOURDHOOD')
,(nextval('seq_location_type_id'), 'PEDANIA')
,(nextval('seq_location_type_id'), 'HEATMAP');



insert INTO location (id, description, gps, parent_id, location_type_id) values
(nextval('seq_location_id'), 'ROOT', ST_GeographyFromText('SRID=4326;POINT(0.0 0.0)'), 1, 1)
,(nextval('seq_location_id'), 'World', ST_GeographyFromText('SRID=4326;POINT(0.0 0.0)'), 1, 2)
,(nextval('seq_location_id'), 'Europe', ST_GeographyFromText('SRID=4326;POINT(0.0 0.0)'), 2, 3)
,(nextval('seq_location_id'), 'Spain', ST_GeographyFromText('SRID=4326;POINT(-3.7492 40.4637)'), 3, 4)
,(nextval('seq_location_id'), 'Barcelona', ST_GeographyFromText('SRID=4326;POINT(2.1734 41.3851)'), 4, 5)
,(nextval('seq_location_id'), 'Avila', ST_GeographyFromText('SRID=4326;POINT(-4.6812 40.6567)'), 4, 5)
,(nextval('seq_location_id'), 'Toledo', ST_GeographyFromText('SRID=4326;POINT(-4.0273 39.8628)'), 4, 5)
,(nextval('seq_location_id'), 'Sant Cugat', ST_GeographyFromText('SRID=4326;POINT(-2.0763 41.4526)'), 5, 6)
,(nextval('seq_location_id'), 'Alcorcon', ST_GeographyFromText('SRID=4326;POINT(-3.8278 40.3468)'), 6, 6)
,(nextval('seq_location_id'), 'Candeleda', ST_GeographyFromText('SRID=4326;POINT(-5.2436 40.1559)'), 6, 6)
,(nextval('seq_location_id'), 'Arenas de San Pedro', ST_GeographyFromText('SRID=4326;POINT(-5.0833 40.2103)'), 6, 6)
,(nextval('seq_location_id'), 'Portugal', ST_GeographyFromText('SRID=4326;POINT(-8.2245 39.3999)'), 3, 4)
,(nextval('seq_location_id'), 'Oporto', ST_GeographyFromText('SRID=4326;POINT(-8.6291 41.1579)'), 12, 6)
,(nextval('seq_location_id'), 'Lisboa', ST_GeographyFromText('SRID=4326;POINT(-9.1393 38.7223)'), 12, 6)
,(nextval('seq_location_id'), 'Moreira', ST_GeographyFromText('SRID=4326;POINT(-8.654604 41.242565)'), 14, 6)
,(nextval('seq_location_id'), 'Murcia', ST_GeographyFromText('SRID=4326;POINT(-1.1307 37.9922)'), 4, 5)
,(nextval('seq_location_id'), 'Lorca', ST_GeographyFromText('SRID=4326;POINT(-1.6968 37.6736)'), 16, 6)
,(nextval('seq_location_id'), 'Aguaderas', ST_GeographyFromText('SRID=4326;POINT(-1.6968 37.6736)'), 17, 8)
,(nextval('seq_location_id'), 'Barrio de Aguaderas', ST_GeographyFromText('SRID=4326;POINT(-1.6968 37.6736)'), 18, 7)
,(nextval('seq_location_id'), 'Barrio Sant Cugat', ST_GeographyFromText('SRID=4326;POINT(-2.0763 41.4526)'), 8, 7)
,(nextval('seq_location_id'), 'Heat Map Mordor', ST_GeographyFromText('SRID=4326;POINT(-2.0763 41.4526)'), 1, 9);

package de.spring.webservices.rest.persistence.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgis.PGgeometry;

import de.spring.webservices.domain.Location.Point;

public class PointTypeHandler implements TypeHandler<Point> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int paramNumber, Point paramValue, JdbcType jdbcType)
            throws SQLException {
        preparedStatement.setObject(paramNumber, convert(paramValue));
    }

    @Override
    public Point getResult(ResultSet resultSet, String columnName) throws SQLException {
        PGgeometry pGgeometry = (PGgeometry) resultSet.getObject(columnName);
        return convert(pGgeometry);
    }

    @Override
    public Point getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        PGgeometry pGgeometry = (PGgeometry) resultSet.getObject(columnIndex);
        return convert(pGgeometry);
    }

    @Override
    public Point getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        Object object = callableStatement.getObject(columnIndex);
        PGgeometry pGgeometry = (PGgeometry) object;
        return convert(pGgeometry);
    }

    private de.spring.webservices.domain.Location.Point convert(final PGgeometry pGgeometry) {
        if (pGgeometry == null) {
            return null;
        }

        org.postgis.Point point = (org.postgis.Point) pGgeometry.getGeometry();
        return new de.spring.webservices.domain.Location.Point(point.getX(), point.getY());
    }

    private PGgeometry convert(final Point point) {
        if (point == null) {
            return null;
        }

        PGgeometry geometry = new PGgeometry();
        geometry.setGeometry(new org.postgis.Point(point.getLongitude(), point.getLatitude()));
        return geometry;
    }
}

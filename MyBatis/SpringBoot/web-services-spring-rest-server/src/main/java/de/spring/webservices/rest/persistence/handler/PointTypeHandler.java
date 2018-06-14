package de.spring.webservices.rest.persistence.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgis.Geometry;
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
        String point = (String) resultSet.getObject(columnName);
        return convert(point);
    }

    @Override
    public Point getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String pGobject = (String) resultSet.getObject(columnIndex);
        return convert(pGobject);
    }

    @Override
    public Point getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String pGobject = (String) callableStatement.getObject(columnIndex);
        return convert(pGobject);
    }

    private de.spring.webservices.domain.Location.Point convert(final String point) throws SQLException {
        if (point == null) {
            return null;
        }

        Geometry geometry = PGgeometry.geomFromString(point);
        org.postgis.Point postgisPoint = (org.postgis.Point) geometry;

        return new de.spring.webservices.domain.Location.Point(postgisPoint.getX(), postgisPoint.getY());
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

package de.spring.webservices.rest.persistence.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;

import de.spring.webservices.domain.Location.Point;

public class PointTypeHandler implements TypeHandler<Point> {

    @Override
    public void setParameter(PreparedStatement preparedStatement, int paramNumber, Point paramValue, JdbcType jdbcType)
            throws SQLException {
        preparedStatement.setObject(paramNumber, convert(paramValue));
    }

    @Override
    public Point getResult(ResultSet resultSet, String columnName) throws SQLException {
        PGobject pGobject = (PGobject) resultSet.getObject(columnName);
        return convert(pGobject);
    }

    @Override
    public Point getResult(ResultSet resultSet, int columnIndex) throws SQLException {
        PGobject pGobject = (PGobject) resultSet.getObject(columnIndex);
        return convert(pGobject);
    }

    @Override
    public Point getResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        PGobject pGobject = (PGobject) callableStatement.getObject(columnIndex);
        return convert(pGobject);
    }

    private de.spring.webservices.domain.Location.Point convert(final PGobject pGobject) throws SQLException {
        if (pGobject == null) {
            return null;
        }

        String value = pGobject.getValue();
        Geometry geometry = PGgeometry.geomFromString(value);
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

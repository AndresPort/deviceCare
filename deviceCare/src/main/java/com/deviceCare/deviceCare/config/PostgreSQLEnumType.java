package com.deviceCare.deviceCare.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PostgreSQLEnumType<T extends Enum<T>> implements UserType<T> {

    private final Class<T> enumClass;

    public PostgreSQLEnumType(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    public PostgreSQLEnumType() {
        this.enumClass = null;
    }

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(T x, T y) {
        return x == y;
    }

    @Override
    public int hashCode(T x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public T nullSafeGet(ResultSet rs, int position,
                         SharedSessionContractImplementor session,
                         Object owner) throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull() || value == null || enumClass == null) return null;
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, T value, int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T value) {
        return value == null ? null : value.name();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T assemble(Serializable cached, Object owner) {
        if (cached == null || enumClass == null) return null;
        return Enum.valueOf(enumClass, (String) cached);
    }
}
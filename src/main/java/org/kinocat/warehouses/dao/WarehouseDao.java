package org.kinocat.warehouses.dao;

import org.kinocat.warehouses.dto.WarehouseFilter;
import org.kinocat.warehouses.entity.Warehouse;
import org.kinocat.warehouses.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {
    private static final String TABLE_CREATE_SQL = """
            create table if not exists warehouse
             (
                 id                 INT          auto_increment,
                 name               VARCHAR(50)  NOT NULL,
                 address_line_1     VARCHAR(100) NOT NULL,
                 address_line_2     VARCHAR(100),
                 city               VARCHAR(50)  NOT NULL,
                 state              VARCHAR(50)  NOT NULL,
                 country            VARCHAR(50)  NOT NULL,
                 inventory_quantity INT          NOT NULL DEFAULT 0,
                 PRIMARY KEY (id)
             )
            """;
    private static final String SAVE_SQL = """
            INSERT INTO warehouse
            (name, address_line_1, address_line_2, city, state, country, inventory_quantity)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String DELETE_SQL = "delete from warehouse where id = ?";

    private static final String UPDATE_SQL = """
            update warehouse
            set name = ?,
                address_line_1 = ?,
                address_line_2 = ?,
                city = ?,
                state = ?,
                country = ?,
                inventory_quantity = ?
            where id = ?
            """;

    private static final String FIND_ALL_SQL = """
            select id, name, address_line_1, address_line_2, city, state, country, inventory_quantity
            from warehouse
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "where id = ?";

    private static Warehouse buildWarehouse(ResultSet rs) throws SQLException {
        return new Warehouse(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address_line_1"),
                rs.getString("address_line_2"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("country"),
                rs.getInt("inventory_quantity")
        );
    }

    private int buildStatement(Warehouse warehouse, PreparedStatement st) throws SQLException {
        st.setString(1, warehouse.getName());
        st.setString(2, warehouse.getAddressLine1());
        st.setString(3, warehouse.getAddressLine2());
        st.setString(4, warehouse.getCity());
        st.setString(5, warehouse.getState());
        st.setString(6, warehouse.getCountry());
        st.setInt(7, warehouse.getInventoryQuantity());
        return 7;
    }

    public void createTable() {
        try (Connection con = ConnectionManager.open(); Statement statement = con.createStatement()) {
            statement.execute(TABLE_CREATE_SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Warehouse> findAll() {
        return findAll(null);
    }

    public List<Warehouse> findAll(WarehouseFilter filter) {
        try (Connection con = ConnectionManager.open();
             PreparedStatement st = filter == null ? con.prepareStatement(FIND_ALL_SQL) : buildFilteredStatement(con, filter)) {
            List<Warehouse> list = new ArrayList<>();
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                list.add(buildWarehouse(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement buildFilteredStatement(Connection con, WarehouseFilter filter) throws SQLException {
        List<Object> parameters = new ArrayList<>();
        String subQuery = filter.buildSubQuery(parameters);
        PreparedStatement st = con.prepareStatement(FIND_ALL_SQL + subQuery);

        for (int i = 0; i < parameters.size(); i++) {
            st.setObject(i + 1, parameters.get(i));
        }

        return st;
    }

    public Warehouse findById(int id) {
        try (Connection con = ConnectionManager.open(); PreparedStatement st = con.prepareStatement(FIND_BY_ID_SQL)) {
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            return rs.next() ? buildWarehouse(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Warehouse save(Warehouse warehouse) {
        try (Connection con = ConnectionManager.open(); PreparedStatement st = con.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            buildStatement(warehouse, st);
            st.executeUpdate();

            ResultSet keys = st.getGeneratedKeys();

            if (keys.next()) {
                warehouse.setId(keys.getInt(1));
            }
            return warehouse;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Warehouse warehouse) {
        try (Connection con = ConnectionManager.open(); PreparedStatement st = con.prepareStatement(UPDATE_SQL)) {
            int index = buildStatement(warehouse, st);
            st.setInt(index + 1, warehouse.getId());

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        try (Connection con = ConnectionManager.open(); PreparedStatement st = con.prepareStatement(DELETE_SQL)) {
            st.setLong(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

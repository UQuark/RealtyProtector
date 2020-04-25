package me.uquark.realtyprotector.data;

import me.uquark.quarkcore.data.DatabaseProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RegionManager {
    private final Connection connection = DatabaseProvider.getConnection("realtyprotector", "rp_user", "");

    public RegionManager() throws SQLException {
    }

    private boolean isMember(int regionId, String playerUUID) throws SQLException {
        final String QUERY = "SELECT FROM membership WHERE (\"regionId\" = ?) and (\"playerUUID\" = CAST(? as UNIQUEIDENTIFIER))";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(QUERY);
            statement.setInt(1, regionId);
            statement.setString(2, playerUUID);

            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return false;
    }

    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) throws SQLException {
        final String QUERY = "SELECT id, CAST(\"ownerUUID\" as char(36)) FROM region WHERE (? between x1 and x2) and (? between y1 and y2) and (? between z1 and z2)";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(QUERY);
            statement.setInt(1, pos.getX());
            statement.setInt(2, pos.getY());
            statement.setInt(3, pos.getZ());

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String uuid = resultSet.getString(2);
                if (player.getUuidAsString().equals(uuid))
                    return true;
                return isMember(id, player.getUuidAsString());
            } else
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return false;
    }

    private void addMembership(int regionId, List<PlayerEntity> members) {

    }

    public int addRegion(String name, BlockPos pos1, BlockPos pos2, PlayerEntity owner, List<PlayerEntity> members) throws SQLException {
        final String INSERT_QUERY = "INSERT INTO region(x1, y1, z1, x2, y2, z2, \"ownerUUID\", name) VALUES (?, ?, ?, ?, ?, ?, CAST(? as UNIQUEIDENTIFIER), ?)";
        final String SELECT_QUERY = "SELECT SCOPE_IDENTITY()";

        int x1 = Math.min(pos1.getX(), pos2.getX());
        int y1 = Math.min(pos1.getY(), pos2.getY());
        int z1 = Math.min(pos1.getZ(), pos2.getZ());
        int x2 = Math.max(pos1.getX(), pos2.getX());
        int y2 = Math.max(pos1.getY(), pos2.getY());
        int z2 = Math.max(pos1.getZ(), pos2.getZ());
        String ownerUUID = owner.getUuidAsString();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(INSERT_QUERY);
            statement.setInt(1, x1);
            statement.setInt(2, y1);
            statement.setInt(3, z1);
            statement.setInt(4, x2);
            statement.setInt(5, y2);
            statement.setInt(6, z2);
            statement.setString(7, ownerUUID);
            statement.setString(8, name);
            statement.execute();
            statement.close();

            statement = connection.prepareStatement(SELECT_QUERY);
            resultSet = statement.executeQuery();
            int regionId = -1;
            if (resultSet.next())
                regionId = resultSet.getInt(1);
            else
                return -1;
            if (members != null)
                addMembership(regionId, members);
            return regionId;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (resultSet != null) resultSet.close();
        }
        return -1;
    }
}

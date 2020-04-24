package me.uquark.realtyprotector.data;

import me.uquark.quarkcore.data.DatabaseProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RegionManager {
    private Connection connection = DatabaseProvider.getConnection();
    private ArrayList<Region> regions = new ArrayList<>();

    private class Region {
        private final Box box;

        public Region(int sx, int sy, int sz, int ex, int ey, int ez) {
            box = new Box(sx, sy, sz, ex, ey, ez);
        }

        public boolean contains(BlockPos pos) {
            return box.contains(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public RegionManager() throws SQLException {
        reloadRegions();
    }

    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        if (player.allowsPermissionLevel(4))
            return true;
        for (Region region : regions)
            if (region.contains(pos))
                return false;
        return true;
    }

    private void reloadRegions() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM realtyprotector");
            regions.clear();
            while (resultSet.next()) {
                int sx = resultSet.getInt(1);
                int sy = resultSet.getInt(2);
                int sz = resultSet.getInt(3);
                int ex = resultSet.getInt(4);
                int ey = resultSet.getInt(5);
                int ez = resultSet.getInt(6);
                regions.add(new Region(sx, sy, sz, ex, ey, ez));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }
}

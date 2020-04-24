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
import java.util.List;
import java.util.UUID;

public class RegionManager {
    private final Connection connection = DatabaseProvider.getConnection();
    private final ArrayList<Region> regions = new ArrayList<>();

    private static class Region {
        private final Box box;
        private final UUID owner;
        private final List<UUID> members;

        public Region(int sx, int sy, int sz, int ex, int ey, int ez, String owner, String members) {
            box = new Box(sx, sy, sz, ex, ey, ez);
            this.owner = UUID.fromString(owner);
            this.members = new ArrayList<>();
            String[] memberUUID = members.split(":");
            for (String member : memberUUID) {
                try {
                    UUID uuid = UUID.fromString(member);
                    this.members.add(uuid);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        public Region(BlockPos pos1, BlockPos pos2, UUID owner, List<UUID> members) {
            box = new Box(pos1, pos2);
            this.owner = owner;
            this.members = members;
        }

        private boolean contains(BlockPos pos) {
            return box.contains(pos.getX(), pos.getY(), pos.getZ());
        }
        public boolean hasPermission(PlayerEntity player, BlockPos pos) {
            if (!contains(pos))
                return true;
            if (player.getUuid().equals(owner))
                return true;
            for (UUID member : members)
                if (player.getUuid().equals(member))
                    return true;
            return false;
        }
    }

    public RegionManager() throws SQLException {
        reloadRegions();
    }

    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        if (player.allowsPermissionLevel(4))
            return true;
        for (Region region : regions)
            if (!region.hasPermission(player, pos))
                return false;
        return true;
    }

    public void addRegion(BlockPos pos1, BlockPos pos2, PlayerEntity owner) throws SQLException {
        Region region = new Region(pos1, pos2, owner.getUuid(), new ArrayList<>());
        regions.add(region);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(String.format(
                "INSERT INTO realtyprotector_regions(sx, sy, sz, ex, ey, ez, owner, members) VALUES (%d, %d, %d, %d, %d, %d, '%s', '%s')",
                pos1.getX(),
                pos1.getY(),
                pos1.getZ(),
                pos2.getX(),
                pos2.getY(),
                pos2.getZ(),
                owner.getUuidAsString(),
                ""
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
        }
    }

    private void reloadRegions() throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM realtyprotector_regions");
            regions.clear();
            while (resultSet.next()) {
                int sx = resultSet.getInt(1);
                int sy = resultSet.getInt(2);
                int sz = resultSet.getInt(3);
                int ex = resultSet.getInt(4);
                int ey = resultSet.getInt(5);
                int ez = resultSet.getInt(6);
                String ownerUUID = resultSet.getString(7);
                String membersUUID = resultSet.getString(8);
                regions.add(new Region(sx, sy, sz, ex, ey, ez, ownerUUID, membersUUID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
    }
}

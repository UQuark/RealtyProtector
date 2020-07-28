package me.uquark.realtyprotector;

import me.uquark.realtyprotector.data.RegionManager;
import net.fabricmc.api.DedicatedServerModInitializer;

import java.io.IOException;
import java.sql.SQLException;

public class RealtyProtectorServer implements DedicatedServerModInitializer {
    public static RealtyProtectorServer INSTANCE;
    public RegionManager regionManager = null;

    @Override
    public void onInitializeServer() {
        INSTANCE = this;
        try {
            regionManager = new RegionManager();
        } catch (SQLException e) {
            RealtyProtector.LOGGER.warn("Failed to establish DB connection");
            e.printStackTrace();
        } catch (IOException e) {
            RealtyProtector.LOGGER.warn("Failed to unpack DB sample");
            e.printStackTrace();
        }
    }
}

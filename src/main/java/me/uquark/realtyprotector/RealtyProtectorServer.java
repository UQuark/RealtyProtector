package me.uquark.realtyprotector;

import me.uquark.realtyprotector.data.RegionManager;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class RealtyProtectorServer implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static RealtyProtectorServer INSTANCE;

    public RegionManager regionManager = null;

    @Override
    public void onInitializeServer() {
        INSTANCE = this;
        try {
            regionManager = new RegionManager();
        } catch (SQLException e) {
            LOGGER.warn("Failed to establish SQL connection");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warn("Failed to unpack DB sample");
            e.printStackTrace();
        }
    }
}

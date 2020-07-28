package me.uquark.realtyprotector;

import me.uquark.realtyprotector.item.Items;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealtyProtector implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String modid = "realtyprotector";

    @Override
    public void onInitialize() {
        Items.PROTECTION_CURSOR.register();
        Items.DELETION_CURSOR.register();
    }
}

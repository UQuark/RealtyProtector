package me.uquark.realtyprotector;

import me.uquark.quarkcore.base.AbstractMod;
import me.uquark.realtyprotector.data.RegionManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.network.ClientPlayerInteractionManager;

public class RealtyProtector extends AbstractMod implements ModInitializer {
    public RealtyProtector() {
        super("realtyprotector");
    }

    public RegionManager regionManager = new RegionManager();

    @Override
    public void onInitialize() {
    }
}

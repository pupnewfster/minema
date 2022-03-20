package info.ata4.minecraft.minema.client.config;

import info.ata4.minecraft.minema.Minema;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler extends ModConfig {

    private final MinemaConfig config;

    public ConfigHandler(ModContainer container, MinemaConfig config) {
        super(config.getConfigType(), config.getConfigSpec(), container, Minema.MODID + "/" + config.getFileName() + ".toml");
        this.config = config;
    }

    public void clearCache() {
        config.clearCache();
    }
}

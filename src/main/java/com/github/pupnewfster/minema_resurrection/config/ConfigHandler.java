package com.github.pupnewfster.minema_resurrection.config;

import com.github.pupnewfster.minema_resurrection.MinemaResurrection;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler extends ModConfig {

    private final MinemaConfig config;

    public ConfigHandler(ModContainer container, MinemaConfig config) {
        super(config.getConfigType(), config.getConfigSpec(), container, MinemaResurrection.MODID + "/" + config.getFileName() + ".toml");
        this.config = config;
    }

    public void clearCache() {
        config.clearCache();
    }
}
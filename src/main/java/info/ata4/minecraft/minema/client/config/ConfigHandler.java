package info.ata4.minecraft.minema.client.config;

import java.nio.file.Path;
import java.util.function.Function;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import info.ata4.minecraft.minema.Minema;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class ConfigHandler extends ModConfig {

    private static final MekanismConfigFileTypeHandler MEK_TOML = new MekanismConfigFileTypeHandler();

    private final IMekanismConfig config;

    public ConfigHandler(ModContainer container, IMekanismConfig config) {
        super(config.getConfigType(), config.getConfigSpec(), container, Minema.MODID + "/" + config.getFileName() + ".toml");
        this.config = config;
    }

    @Override
    public ConfigFileTypeHandler getHandler() {
        return MEK_TOML;
    }

    public void clearCache() {
        config.clearCache();
    }

    private static class MekanismConfigFileTypeHandler extends ConfigFileTypeHandler {

        private static Path getPath(Path configBasePath) {
            //Intercept server config path reading for Mekanism configs and reroute it to the normal config directory
            if (configBasePath.endsWith("serverconfig")) {
                return FMLPaths.CONFIGDIR.get();
            }
            return configBasePath;
        }

        @Override
        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
            return super.reader(getPath(configBasePath));
        }

        @Override
        public void unload(Path configBasePath, ModConfig config) {
            super.unload(getPath(configBasePath), config);
        }
    }
}

package com.github.pupnewfster.minema_resurrection;

import com.mojang.logging.LogUtils;
import com.github.pupnewfster.minema_resurrection.config.ConfigHandler;
import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(MinemaResurrection.MODID)
public class MinemaResurrection {

    public static final String MODID = "minema_resurrection";
    public static final Logger logger = LogUtils.getLogger();
    private static final Lazy<Path> cameraDirectory = Lazy.of(() -> FMLPaths.getOrCreateGameRelativePath(FMLPaths.GAMEDIR.get().resolve(MODID), MODID));
    public static MinemaResurrection instance;
    private final MinemaConfig config;

    public MinemaResurrection() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            instance = this;
            ModContainer modContainer = ModLoadingContext.get().getActiveContainer();
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(MODID), MODID);
            modContainer.addConfig(new ConfigHandler(modContainer, config = new MinemaConfig()));
            modEventBus.addListener(this::onConfigLoad);
            modEventBus.addListener(MinemaKeyBindings::clientSetup);
            MinecraftForge.EVENT_BUS.register(EventListener.instance);
        } else {
            config = null;
        }
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static Path getCameraDirectory() {
        return cameraDirectory.get();
    }

    public MinemaConfig getConfig() {
        return config;
    }

    private void onConfigLoad(ModConfigEvent configEvent) {
        //Note: We listen to both the initial load and the reload, to make sure that we fix any accidentally
        // cached values from calls before the initial loading
        ModConfig config = configEvent.getConfig();
        //Make sure it is for the same modid as us
        if (config.getModId().equals(MODID) && config instanceof ConfigHandler handler) {
            handler.clearCache();
        }
    }
}
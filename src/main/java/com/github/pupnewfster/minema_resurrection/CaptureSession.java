package com.github.pupnewfster.minema_resurrection;

import com.github.pupnewfster.minema_resurrection.config.MinemaConfig;
import com.github.pupnewfster.minema_resurrection.event.CaptureEvent;
import com.github.pupnewfster.minema_resurrection.event.MinemaEventbus;
import com.github.pupnewfster.minema_resurrection.modules.CaptureModule;
import com.github.pupnewfster.minema_resurrection.modules.CaptureNotification;
import com.github.pupnewfster.minema_resurrection.modules.CaptureOverlay;
import com.github.pupnewfster.minema_resurrection.modules.ChunkPreloader;
import com.github.pupnewfster.minema_resurrection.modules.ShaderSync;
import com.github.pupnewfster.minema_resurrection.modules.TickSynchronizer;
import com.github.pupnewfster.minema_resurrection.modules.modifiers.DisplaySizeModifier;
import com.github.pupnewfster.minema_resurrection.modules.modifiers.GameSettingsModifier;
import com.github.pupnewfster.minema_resurrection.modules.modifiers.TimerModifier;
import com.github.pupnewfster.minema_resurrection.modules.video.VideoHandler;
import com.github.pupnewfster.minema_resurrection.util.CaptureTime;
import com.github.pupnewfster.minema_resurrection.util.Translations;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CaptureSession {

    public static final CaptureSession singleton = new CaptureSession();

    private final CaptureModule[] modules = {new GameSettingsModifier(), new ShaderSync(), new TimerModifier(), new TickSynchronizer(), new ChunkPreloader(),
                                             new DisplaySizeModifier(), new VideoHandler(), new CaptureOverlay(), new CaptureNotification()};

    private Path captureDir;
    private CaptureTime time;
    private int frameLimit;
    private boolean isEnabled;
    public boolean isPaused;

    private CaptureSession() {
    }

    public boolean startCapture() {
        if (isEnabled) {
            return false;
        }
        isEnabled = true;
        isPaused = false;

        try {
            MinemaConfig cfg = MinemaResurrection.instance.getConfig();

            frameLimit = cfg.frameLimit.get();
            captureDir = Paths.get(cfg.capturePath.get());

            if (!Files.exists(captureDir)) {
                Files.createDirectories(captureDir);
            }

            Minecraft minecraft = Minecraft.getInstance();
            if (!minecraft.hasSingleplayerServer()) {
                if (cfg.syncEngine.get()) {
                    minecraft.player.sendSystemMessage(Translations.WARN_ERROR.translateColored(ChatFormatting.RED));
                    minecraft.player.sendSystemMessage(Translations.WARN_TICK_SYNC.translateColored(ChatFormatting.RED));
                }
                if (cfg.preloadChunks.get()) {
                    minecraft.player.sendSystemMessage(Translations.WARN_WARNING.translateColored(ChatFormatting.YELLOW));
                    minecraft.player.sendSystemMessage(Translations.WARN_PRELOAD_CHUNKS.translateColored(ChatFormatting.YELLOW));
                }
            }

            for (CaptureModule m : modules) {
                m.enable();
            }

            MinecraftForge.EVENT_BUS.register(this);

            time = new CaptureTime(cfg.frameRate.get());
        } catch (Exception e) {
            printError(e, true);
        }
        return true;
    }

    public boolean stopCapture() {
        if (!isEnabled) {
            return false;
        }
        MinemaEventbus.reset();
        MinecraftForge.EVENT_BUS.unregister(this);
        for (CaptureModule m : modules) {
            if (m.isEnabled()) {
                try {
                    m.disable();
                } catch (Exception e) {
                    printError(e, false);
                }
            }
        }
        isEnabled = false;
        return true;
    }

    public Path getCaptureDir() {
        return captureDir;
    }

    public CaptureTime getTime() {
        return time;
    }

    private <X> void execFrameEvent(MinemaEventbus<X> bus, X event) {
        if (isEnabled) {
            if (frameLimit > 0 && time.getNumFrames() >= frameLimit) {
                stopCapture();
                return;
            }
            try {
                bus.throwEvent(event);
            } catch (Exception e) {
                printError(e, true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent e) {
        if (e.phase == Phase.END && !isPaused) {
            execFrameEvent(MinemaEventbus.endRenderBUS, new CaptureEvent.End(this));
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL && !isPaused) {
            execFrameEvent(MinemaEventbus.midRenderBUS, new CaptureEvent.Mid(this));
        }
    }

    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        //TODO - 1.19: Once optifine is updated test if this handles the paused stuff properly
        ShaderSync.setFrameTimeCounter();
    }

    private void printError(Throwable throwable, boolean stop) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.sendSystemMessage(Component.literal(throwable.getClass().getName()).withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal(throwable.getMessage()).withStyle(ChatFormatting.RED));
            Throwable cause = throwable.getCause();
            if (cause != null) {
                player.sendSystemMessage(Translations.ERROR_CAUSE.translateColored(ChatFormatting.RED, cause.getClass().getName(), cause.getMessage()));
            }
            MinemaResurrection.logger.error(throwable.getMessage(), throwable);
            player.sendSystemMessage(Translations.ERROR_SEE_LOG.translateColored(ChatFormatting.RED));
        }
        if (stop) {
            stopCapture();
        }
    }
}
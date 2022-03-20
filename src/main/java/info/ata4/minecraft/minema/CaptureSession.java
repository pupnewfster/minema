package info.ata4.minecraft.minema;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import info.ata4.minecraft.minema.client.event.EndRenderEvent;
import info.ata4.minecraft.minema.client.event.MidRenderEvent;
import info.ata4.minecraft.minema.client.event.MinemaEventbus;
import info.ata4.minecraft.minema.client.modules.CaptureModule;
import info.ata4.minecraft.minema.client.modules.CaptureNotification;
import info.ata4.minecraft.minema.client.modules.CaptureOverlay;
import info.ata4.minecraft.minema.client.modules.ChunkPreloader;
import info.ata4.minecraft.minema.client.modules.ShaderSync;
import info.ata4.minecraft.minema.client.modules.TickSynchronizer;
import info.ata4.minecraft.minema.client.modules.modifiers.DisplaySizeModifier;
import info.ata4.minecraft.minema.client.modules.modifiers.GameSettingsModifier;
import info.ata4.minecraft.minema.client.modules.modifiers.TimerModifier;
import info.ata4.minecraft.minema.client.modules.video.VideoHandler;
import info.ata4.minecraft.minema.client.util.CaptureTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CaptureSession {

    public static final CaptureSession singleton = new CaptureSession();

    private final CaptureModule[] modules = new CaptureModule[]{new GameSettingsModifier(), new ShaderSync(),
                                                                new TimerModifier(), new TickSynchronizer(), new ChunkPreloader(), new DisplaySizeModifier(),
                                                                new VideoHandler(), new CaptureOverlay(), new CaptureNotification()};

    private Path captureDir;
    private CaptureTime time;
    private int frameLimit;
    private boolean isEnabled;

    private CaptureSession() {
    }

    public boolean startCapture() {
		if (isEnabled) {
			return false;
		}
        isEnabled = true;

        try {
            Minecraft MC = Minecraft.getInstance();
            MinemaConfig cfg = Minema.instance.getConfig();

            frameLimit = cfg.frameLimit.get();
            captureDir = Paths.get(cfg.capturePath.get());

            if (!Files.exists(captureDir)) {
                Files.createDirectories(captureDir);
            }

            if (cfg.syncEngine.get() & !MC.hasSingleplayerServer()) {
                Utils.print("WARNING!", ChatFormatting.RED);
                Utils.print("Tick sync is NOT going to work! Record in singleplayer!", ChatFormatting.RED);
            }

            if (cfg.preloadChunks.get() & !MC.hasSingleplayerServer()) {
                Utils.print("Warning!", ChatFormatting.YELLOW);
                Utils.print("Instant chunk loading should be used in singleplayer for its full effect!",
                      ChatFormatting.YELLOW);
            }

            for (CaptureModule m : modules) {
                m.enable();
            }

            MinecraftForge.EVENT_BUS.register(this);

            time = new CaptureTime(cfg.frameRate.get());
        } catch (Exception e) {
            Utils.printError(e);
            stopCapture();
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
                    Utils.printError(e);
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
                Utils.printError(e);
                stopCapture();
            }

        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent e) {
        if (e.phase == Phase.END) {
            execFrameEvent(MinemaEventbus.endRenderBUS, new EndRenderEvent(this));
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderLevelLastEvent event) {
        singleton.execFrameEvent(MinemaEventbus.midRenderBUS, new MidRenderEvent(singleton));
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        // CHANGED: use event instead of ASM hack
        ShaderSync.setFrameTimeCounter();
    }

}

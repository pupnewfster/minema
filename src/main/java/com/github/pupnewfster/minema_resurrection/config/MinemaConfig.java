/*
 ** 2014 July 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package com.github.pupnewfster.minema_resurrection.config;

import com.github.pupnewfster.minema_resurrection.config.value.CachedBooleanValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedDoubleValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedEnumValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedFloatValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedIntValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedPrimitiveValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedResolvableConfigValue;
import com.github.pupnewfster.minema_resurrection.config.value.CachedStringValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

public class MinemaConfig {

    private static final int MAX_TEXTURE_SIZE = 1000;//Minecraft.getGLMaximumTextureSize();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final String ENCODING_CATEGORY = "encoding";
    private static final String CAPTURING_CATEGORY = "capturing";
    private static final String ENGINE_CATEGORY = "engine";

    private final List<CachedResolvableConfigValue<?, ?>> cachedConfigValues = new ArrayList<>();
    private final List<CachedPrimitiveValue<?>> cachedPrimitiveValues = new ArrayList<>();

    private final ForgeConfigSpec configSpec;

    public final CachedBooleanValue useVideoEncoder;
    public final CachedStringValue videoEncoderPath;
    public final CachedStringValue videoEncoderParams;
    private final CachedEnumValue<SnapResolution> snapResolution;

    private final CachedIntValue frameWidth;
    private final CachedIntValue frameHeight;
    public final CachedDoubleValue frameRate;
    public final CachedIntValue frameLimit;
    public final CachedStringValue capturePath;
    public final CachedBooleanValue showOverlay;
    public final CachedBooleanValue captureDepth;
    public final CachedBooleanValue recordGui;
    public final CachedBooleanValue aaFastRenderFix;

    public final CachedFloatValue engineSpeed;
    public final CachedBooleanValue syncEngine;
    public final CachedBooleanValue preloadChunks;
    public final CachedBooleanValue forcePreloadChunks;
    public final CachedBooleanValue delayStartUntilChunksLoaded;
    public final CachedBooleanValue applyFOVModifiers;
    public final CachedBooleanValue applyFOVModifiersPath;

    public MinemaConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("General Config.").push("general");

        builder.comment("Encoding Settings").translation("minema_resurrection.config.encoding").push(ENCODING_CATEGORY);
        useVideoEncoder = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.useVideoEncoder")
              .comment("If enabled, a video encoding program is used that will receive uncompressed BGR24 frames from Minema Resurrection via the standard input pipe.")
              .define("useVideoEncoder", true));
        videoEncoderPath = CachedStringValue.wrap(this, builder.translation("minema_resurrection.config.videoEncoderPath")
              .comment("Path to the video encoding executable. The encoder's working directory is the generated movie folder. Ignored if the video encoder is disabled.")
              .define("videoEncoderPath", "ffmpeg"));
        videoEncoderParams = CachedStringValue.wrap(this, builder.translation("minema_resurrection.config.videoEncoderParams")
              .comment("Arguments for the video encoding program. Placeholders: %%WIDTH%% - frame width, %%HEIGHT%% - frame height, %%FPS%% - frame rate. %%NAME%% - video file name to use, %OPTIONAL_SCALE% - adds a scale parameter if the dimensions aren't divisible by two. Ignored if the video encoder is disabled.")
              .define("videoEncoderParams",
              "-f rawvideo -pix_fmt bgr24 -s %WIDTH%x%HEIGHT% -r %FPS% -i - -vf \"vflip%OPTIONAL_SCALE%\" -c:v libx264 -preset ultrafast -tune zerolatency -qp 18 -pix_fmt yuv420p %NAME%.mp4"));
        //TODO - 1.19: Why does this not work in place of the optional scale thing
        snapResolution = CachedEnumValue.wrap(this, builder.translation("minema_resurrection.config.snapResolution")
              .comment("If necessary, snaps the recording resolution to the next lower resolution so that width and height is divisible by this modulus. FFMpeg only needs mod2, some other encoders might need more.")
              .defineEnum("snapResolution", SnapResolution.MOD2));
        builder.pop();

        builder.comment("Capturing Settings").translation("minema_resurrection.config.capturing").push(CAPTURING_CATEGORY);
        frameWidth = CachedIntValue.wrap(this, builder.translation("minema_resurrection.config.frameWidth")
              .comment("Width of every captured frame in pixels. Set to 0 to use the current window/display width. Non-zero values require framebuffer support and are bound to the maximum texture resolution of your GPU.")
              .defineInRange("frameWidth", 0, 0, MAX_TEXTURE_SIZE));
        frameHeight = CachedIntValue.wrap(this, builder.translation("minema_resurrection.config.frameHeight")
              .comment("Height of every captured frame in pixels. Set to 0 to use the current window/display height. Non-zero values require framebuffer support and are bound to the maximum texture resolution of your GPU.")
              .defineInRange("frameHeight", 0, 0, MAX_TEXTURE_SIZE));
        frameRate = CachedDoubleValue.wrap(this, builder.translation("minema_resurrection.config.frameRate")
              .comment("Recording frame rate, sets the amount of frames recorded per in-game second. Floating point values are allowed, e.g. 23.976 for 24p NTSC. Because Minecraft operates at 20 ticks per second, frame rates with multiples of 20 are recommended for best smoothness.")
              .defineInRange("frameRate", 60.0, 1.0, 240.0));
        frameLimit = CachedIntValue.wrap(this, builder.translation("minema_resurrection.config.frameLimit")
              .comment("Number of frames to capture before stopping automatically. -1 means no limit.")
              .defineInRange("frameLimit", -1, -1, Integer.MAX_VALUE));
        capturePath = CachedStringValue.wrap(this, builder.translation("minema_resurrection.config.capturePath")
              .comment("Path were the captured videos are stored. If no absolute path is used, it's relative to the Minecraft working directory.")
              .define("capturePath", "movies"));
        showOverlay = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.showOverlay")
              .comment("If enabled, show additional capturing information on the overlay (F3 menu). Note that these information are visible in the video, too.")
              .define("showOverlay", false));
        captureDepth = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.captureDepth")
              .comment("If enabled, the depth buffer is captured linearly. Warning: Certain tricks are not available for this buffer so this is quite slow!")
              .define("captureDepth", false));
        recordGui = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.recordGui")
              .comment("If disabled, the GUI (hotbar, hand, crosshair, etc) will not be recorded.")
              .define("recordGui", false));
        aaFastRenderFix = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.aaFastRenderFix")
              .comment("Enable to fix broken recordings when using Optifine's antialiasing or fast render together with a custom resolution. Resizes the whole application window as a workaround.")
              .define("aaFastRenderFix", false));
        builder.pop();

        builder.comment("Engine Settings").translation("minema_resurrection.config.engine").push(ENGINE_CATEGORY);
        engineSpeed = CachedFloatValue.wrap(this, builder.translation("minema_resurrection.config.engineSpeed")
              .comment("Speed modifier for the game clock. The default is 1.0, which equals 20 ticks per second. Lower or higher values will speed up or slow down the game time. 2.5, for example, results in two and a half of the normal speed. Useful for slow motion or time lapse effects. Ignored if the capturing is unsynchronized.")
              .defineInRange("engineSpeed", 1.0, 0.01, 100.0));
        syncEngine = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.syncEngine")
              .comment("If enabled, the local server and client runs synchronously to the video capturing frame rate. This effectively turns Minecraft into an offline renderer and allows rendering and capturing of extremely complex scenes. This ShaderSync version also synchronizes the shader mod by karyonix (compatible with Optifine, too). ONLY WORKS ON LOCAL WORLDS!")
              .define("syncEngine", true));
        preloadChunks = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.preloadChunks")
              .comment("If enabled, Minema Resurrection will heavily accelerate the chunk loading rate during recording. THIS IS ONLY TRULY EFFECTIVE ON LOCAL WORLDS!")
              .define("preloadChunks", true));
        forcePreloadChunks = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.forcePreloadChunks")
              .comment("If Preload Chunks is enabled and this is also enabled, all chunks in render distance will be preloaded. THIS IS ONLY TRULY EFFECTIVE ON LOCAL WORLDS!")
              .define("forcePreloadChunks", false));
        delayStartUntilChunksLoaded = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.delayStartUntilChunksLoaded")
              .comment("Delays starting the recording and travelling the path until all chunks in the client's view are loaded.")
              .define("delayStartUntilChunksLoaded", true));
        applyFOVModifiers = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.applyFOVModifiers")
              .comment("When Apply FOV Modifiers is disabled FOV modifiers will not be applied.")
              .define("applyFOVModifiers", false));
        applyFOVModifiersPath = CachedBooleanValue.wrap(this, builder.translation("minema_resurrection.config.applyFOVModifiersPath")
              .comment("When Apply FOV Modifiers While Pathing is enabled FOV modifiers will be applied during pathing.")
              .define("applyFOVModifiersPath", false));
        builder.pop();
        configSpec = builder.build();
    }

    public int getFrameWidth() {
        int width = frameWidth.get();

        // use display width if not set
        if (width == 0) {
            width = minecraft.getWindow().getWidth();
        }

        // snap to nearest
        if (useVideoEncoder.get()) {
            width = snapResolution.get().snap(width);
        }

        return width;
    }

    public int getFrameHeight() {
        int height = frameHeight.get();

        // use display height if not set
        if (height == 0) {
            height = minecraft.getWindow().getHeight();
        }

        // snap to nearest
        if (useVideoEncoder.get()) {
            height = snapResolution.get().snap(height);
        }

        return height;
    }

    public boolean useFrameSize() {
        return getFrameWidth() != minecraft.getWindow().getWidth() || getFrameHeight() != minecraft.getWindow().getHeight();
    }

    public String getFileName() {
        return "general";
    }

    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    public Type getConfigType() {
        return Type.CLIENT;
    }

    public void clearCache() {
        cachedConfigValues.forEach(CachedResolvableConfigValue::clearCache);
        cachedPrimitiveValues.forEach(CachedPrimitiveValue::clearCache);
    }

    public <T, R> void addCachedValue(CachedResolvableConfigValue<T, R> configValue) {
        cachedConfigValues.add(configValue);
    }

    public <T> void addCachedValue(CachedPrimitiveValue<T> configValue) {
        cachedPrimitiveValues.add(configValue);
    }
}
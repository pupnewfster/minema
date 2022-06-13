/*
 ** 2014 July 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.minecraft.minema.client.config;

import info.ata4.minecraft.minema.client.config.value.CachedBooleanValue;
import info.ata4.minecraft.minema.client.config.value.CachedDoubleValue;
import info.ata4.minecraft.minema.client.config.value.CachedEnumValue;
import info.ata4.minecraft.minema.client.config.value.CachedFloatValue;
import info.ata4.minecraft.minema.client.config.value.CachedIntValue;
import info.ata4.minecraft.minema.client.config.value.CachedPrimitiveValue;
import info.ata4.minecraft.minema.client.config.value.CachedResolvableConfigValue;
import info.ata4.minecraft.minema.client.config.value.CachedStringValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

public class MinemaConfig {

    private static final int MAX_TEXTURE_SIZE = 1000;//Minecraft.getGLMaximumTextureSize();
    private static final Minecraft MC = Minecraft.getInstance();

    private final List<CachedResolvableConfigValue<?, ?>> cachedConfigValues = new ArrayList<>();
    private final List<CachedPrimitiveValue<?>> cachedPrimitiveValues = new ArrayList<>();

    private final ForgeConfigSpec configSpec;

    private final String ENCODING_CATEGORY = "encoding";
    private final String CAPTURING_CATEGORY = "capturing";
    private final String ENGINE_CATEGORY = "engine";

    public final CachedBooleanValue useVideoEncoder;
    public final CachedStringValue videoEncoderPath;
    public final CachedStringValue videoEncoderParams;
    public final CachedEnumValue<SnapResolution> snapResolution;

    public final CachedIntValue frameWidth;
    public final CachedIntValue frameHeight;
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

    public MinemaConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("General Config.").push("general");

        builder.comment("Encoding Settings").push(ENCODING_CATEGORY);
        useVideoEncoder = CachedBooleanValue.wrap(this, builder.define("useVideoEncoder", true));
        videoEncoderPath = CachedStringValue.wrap(this, builder.define("videoEncoderPath", "ffmpeg"));
        videoEncoderParams = CachedStringValue.wrap(this, builder.define("videoEncoderParams",
              "-f rawvideo -pix_fmt bgr24 -s %WIDTH%x%HEIGHT% -r %FPS% -i - -vf \"vflip%OPTIONAL_SCALE%\" -c:v libx264 -preset ultrafast -tune zerolatency -qp 18 -pix_fmt yuv420p %NAME%.mp4"));
        snapResolution = CachedEnumValue.wrap(this, builder.defineEnum("snapResolution", SnapResolution.MOD2));
        builder.pop();

        builder.comment("Capturing Settings").push(CAPTURING_CATEGORY);
        frameWidth = CachedIntValue.wrap(this, builder.defineInRange("frameWidth", 0, 0, MAX_TEXTURE_SIZE));
        frameHeight = CachedIntValue.wrap(this, builder.defineInRange("frameHeight", 0, 0, MAX_TEXTURE_SIZE));
        frameRate = CachedDoubleValue.wrap(this, builder.defineInRange("frameRate", 60.0, 1.0, 240.0));
        frameLimit = CachedIntValue.wrap(this, builder.defineInRange("frameLimit", -1, -1, Integer.MAX_VALUE));
        capturePath = CachedStringValue.wrap(this, builder.define("capturePath", "movies"));
        showOverlay = CachedBooleanValue.wrap(this, builder.define("showOverlay", false));
        captureDepth = CachedBooleanValue.wrap(this, builder.define("captureDepth", false));
        recordGui = CachedBooleanValue.wrap(this, builder.define("recordGui", true));
        aaFastRenderFix = CachedBooleanValue.wrap(this, builder.define("aaFastRenderFix", false));
        builder.pop();

        builder.comment("Engine Settings").push(ENGINE_CATEGORY);
        engineSpeed = CachedFloatValue.wrap(this, builder.defineInRange("engineSpeed", 1.0, 0.01, 100.0));
        syncEngine = CachedBooleanValue.wrap(this, builder.define("syncEngine", true));
        preloadChunks = CachedBooleanValue.wrap(this, builder.define("preloadChunks", true));
        forcePreloadChunks = CachedBooleanValue.wrap(this, builder.define("forcePreloadChunks", false));
        builder.pop();
        configSpec = builder.build();
    }

    public int getFrameWidth() {
        int width = frameWidth.get();

        // use display width if not set
        if (width == 0) {
            width = MC.getWindow().getWidth();
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
            height = MC.getWindow().getHeight();
        }

        // snap to nearest
        if (useVideoEncoder.get()) {
            height = snapResolution.get().snap(height);
        }

        return height;
    }

    public boolean useFrameSize() {
        return getFrameWidth() != MC.getWindow().getWidth() || getFrameHeight() != MC.getWindow().getHeight();
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

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

import org.jline.utils.Display;
import info.ata4.minecraft.minema.client.config.value.CachedBooleanValue;
import info.ata4.minecraft.minema.client.config.value.CachedDoubleValue;
import info.ata4.minecraft.minema.client.config.value.CachedEnumValue;
import info.ata4.minecraft.minema.client.config.value.CachedIntValue;
import info.ata4.minecraft.minema.client.config.value.CachedStringValue;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MinemaConfig implements IMekanismConfig {

	private static final int MAX_TEXTURE_SIZE = Minecraft.getGLMaximumTextureSize();

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

	public final CachedDoubleValue engineSpeed;
	public final CachedBooleanValue syncEngine;
	public final CachedBooleanValue preloadChunks;
	public final CachedBooleanValue forcePreloadChunks;

	public MinemaConfig() {
	    ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("General Config.").push("general");

		builder.comment("Encoding Settings").push(ENCODING_CATEGORY);
	    CachedBooleanValue useVideoEncoder = CachedBooleanValue.wrap(this, builder.define("useVideoEncoder", true));
	    CachedStringValue videoEncoderPath = CachedStringValue.wrap(this, builder.define("videoEncoderPath", "videoEncoderPath"));
	    CachedStringValue videoEncoderParams = CachedStringValue.wrap(this, builder.define("videoEncoderParams",
	            "-f rawvideo -pix_fmt bgr24 -s %WIDTH%x%HEIGHT% -r %FPS% -i - -vf vflip -c:v libx264 -preset ultrafast -tune zerolatency -qp 18 -pix_fmt yuv420p %NAME%.mp4"));
	    CachedEnumValue<SnapResolution> snapResolution = CachedEnumValue.wrap(this, builder.defineEnum("snapResolution", SnapResolution.MOD2));
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
	    engineSpeed = CachedDoubleValue.wrap(this, builder.defineInRange("engineSpeed", 1.0, 0.01, 100.0));
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
			width = Display.getWidth();
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
			height = Display.getHeight();
		}

		// snap to nearest
		if (useVideoEncoder.get()) {
			height = snapResolution.get().snap(height);
		}

		return height;
	}

	public boolean useFrameSize() {
		return getFrameWidth() != Display.getWidth() || getFrameHeight() != Display.getHeight();
	}

    @Override
    public String getFileName() {
        return "general";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.SERVER;
    }
}

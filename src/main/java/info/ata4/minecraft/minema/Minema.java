package info.ata4.minecraft.minema;

import org.lwjgl.glfw.GLFW;
import info.ata4.minecraft.minema.client.config.MekanismConfigHelper;
import info.ata4.minecraft.minema.client.config.MinemaConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Most of the files in this repo do have the old copyright notice about
 * Barracuda even though I have touched most of it, in some cases substantially.
 * Few classes do not contain the notice, these are the ones that I have written
 * completely myself or some of the class with substantial changes.
 *
 * @author Gregosteros (minecraftforum) / daipenger (github)
 */
@Mod(Minema.MODID)
@OnlyIn(Dist.CLIENT)
public class Minema {

	public static final String MODID = "minema";

	private static final String category = "key.categories.minema";
	private static final KeyBinding KEY_CAPTURE = new KeyBinding("key.minema.capture", GLFW.GLFW_KEY_F4, category);

	public static Minema instance;
	private MinemaConfig config;

	public Minema() {
		instance = this;
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onPreInit);
        MekanismConfigHelper.registerConfig(ModLoadingContext.get().getActiveContainer(), config);
	}

	public void onPreInit(FMLCommonSetupEvent e) {
		ClientRegistry.registerKeyBinding(KEY_CAPTURE);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void onServerInit(FMLServerStartingEvent e) {
		e.getCommandDispatcher().register(Commands.literal("minema").then(Commands.literal("enable").executes(c -> {
			CaptureSession.singleton.startCapture();
			return 0;
		})).then(Commands.literal("disable").executes(c -> {
			CaptureSession.singleton.stopCapture();
			return 0;
		})));
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (KEY_CAPTURE.isPressed())
			if (!CaptureSession.singleton.startCapture())
				CaptureSession.singleton.stopCapture();
	}

	public MinemaConfig getConfig() {
		return config;
	}

}

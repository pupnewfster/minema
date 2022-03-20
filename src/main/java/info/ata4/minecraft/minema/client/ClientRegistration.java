package info.ata4.minecraft.minema.client;

import info.ata4.minecraft.minema.CaptureSession;
import info.ata4.minecraft.minema.Minema;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Minema.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    private static final String category = "key.categories.minema";
    private static final KeyMapping KEY_CAPTURE = new KeyMapping("key.minema.capture", GLFW.GLFW_KEY_F4, category);

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> ClientRegistry.registerKeyBinding(KEY_CAPTURE));
        MinecraftForge.EVENT_BUS.addListener(ClientRegistration::onKeyInput);
        MinecraftForge.EVENT_BUS.addListener(ClientRegistration::registerCommands);
    }

    private static void onKeyInput(KeyInputEvent event) {
        if (KEY_CAPTURE.consumeClick()) {
            if (!CaptureSession.singleton.startCapture()) {
                CaptureSession.singleton.stopCapture();
            }
        }
    }

    private static void registerCommands(RegisterClientCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("minema").then(Commands.literal("enable").executes(c -> {
            CaptureSession.singleton.startCapture();
            return 0;
        })).then(Commands.literal("disable").executes(c -> {
            CaptureSession.singleton.stopCapture();
            return 0;
        })));
    }
}
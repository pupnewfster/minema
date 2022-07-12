package com.github.pupnewfster.minema_resurrection;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class MinemaKeyBindings {

    private static final String category = "key.categories." + MinemaResurrection.MODID;
    private static final Set<KeyMapping> keys = new HashSet<>();
    public static final KeyMapping KEY_CAPTURE = key("capture", GLFW.GLFW_KEY_F4);
    public static final KeyMapping KEY_POINT = key("add_point", GLFW.GLFW_KEY_P);
    public static final KeyMapping KEY_INCREASE_FOV = key("increase_fov", GLFW.GLFW_KEY_O);
    public static final KeyMapping KEY_DECREASE_FOV = key("decrease_fov", GLFW.GLFW_KEY_U);
    public static final KeyMapping KEY_RESET_FOV = key("reset_fov", GLFW.GLFW_KEY_I);
    public static final KeyMapping KEY_CLOCKWISE_CAMERA = key("clockwise", GLFW.GLFW_KEY_L);
    public static final KeyMapping KEY_COUNTER_CLOCKWISE_CAMERA = key("counter_clockwise", GLFW.GLFW_KEY_J);
    public static final KeyMapping KEY_RESET_CAMERA = key("reset", GLFW.GLFW_KEY_K);

    private static KeyMapping key(String name, int keyCode) {
        KeyMapping keyMapping = new KeyMapping(Util.makeDescriptionId("key", MinemaResurrection.rl(name)), keyCode, category);
        keys.add(keyMapping);
        return keyMapping;
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent evt) {
        for (KeyMapping key : keys) {
            evt.register(key);
        }
    }
}
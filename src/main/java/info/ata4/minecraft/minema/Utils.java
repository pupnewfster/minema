package info.ata4.minecraft.minema;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.opengl.GL15;

public class Utils {

    public static void print(final String msg, final ChatFormatting format) {
        final TextComponent text = new TextComponent(msg == null ? "null" : msg);
        text.getStyle().withColor(format);
        Minecraft.getInstance().player.sendMessage(text, Util.NIL_UUID);
    }

    public static void printError(Throwable throwable) {
        print(throwable.getClass().getName(), ChatFormatting.RED);
        print(throwable.getMessage(), ChatFormatting.RED);
        Throwable cause = throwable.getCause();
        if (cause != null) {
            print("Cause:", ChatFormatting.RED);
            print(cause.getClass().getName(), ChatFormatting.RED);
            print(cause.getMessage(), ChatFormatting.RED);
        }
        throwable.printStackTrace();
        print("See log for full stacktrace", ChatFormatting.RED);
    }

    public static void checkGlError() {
        int err = GL15.glGetError();
        if (err != 0) {
            System.err.println("OpenGL returned non-zero error code: " + err);
            Thread.dumpStack();
            System.exit(0);
        }
    }

}

package info.ata4.minecraft.minema;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.opengl.GL15;

public class Utils {

    public static void print(final String msg, final ChatFormatting format) {
        final MutableComponent text = Component.literal(msg == null ? "null" : msg);
        text.getStyle().withColor(format);
        Minecraft.getInstance().player.sendSystemMessage(text);
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

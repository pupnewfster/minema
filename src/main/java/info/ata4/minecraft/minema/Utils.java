package info.ata4.minecraft.minema;

import org.lwjgl.opengl.GL15;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class Utils {

	public static void print(final String msg, final TextFormatting format) {
		final StringTextComponent text = new StringTextComponent(msg == null ? "null" : msg);
		text.getStyle().func_240712_a_(format);
		Minecraft.getInstance().player.sendMessage(text, Util.field_240973_b_);
	}

	public static void printError(Throwable throwable) {
		print(throwable.getClass().getName(), TextFormatting.RED);
		print(throwable.getMessage(), TextFormatting.RED);
		Throwable cause = throwable.getCause();
		if (cause != null) {
			print("Cause:", TextFormatting.RED);
			print(cause.getClass().getName(), TextFormatting.RED);
			print(cause.getMessage(), TextFormatting.RED);
		}
		throwable.printStackTrace();
		print("See log for full stacktrace", TextFormatting.RED);
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

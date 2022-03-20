package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

public class CachedEnumValue<T extends Enum<T>> extends CachedConfigValue<T> {

    private CachedEnumValue(MinemaConfig config, EnumValue<T> internal) {
        super(config, internal);
    }

    public static <T extends Enum<T>> CachedEnumValue<T> wrap(MinemaConfig config, EnumValue<T> internal) {
        return new CachedEnumValue<>(config, internal);
    }
}
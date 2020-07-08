package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.IMekanismConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedStringValue extends CachedResolvableConfigValue<String, String> {

    private CachedStringValue(IMekanismConfig config, ConfigValue<String> internal) {
        super(config, internal);
    }

    public static CachedStringValue wrap(IMekanismConfig config, ConfigValue<String> internal) {
        return new CachedStringValue(config, internal);
    }

    @Override
    protected String resolve(String encoded) {
        return encoded;
    }

    @Override
    protected String encode(String value) {
        return value;
    }
}

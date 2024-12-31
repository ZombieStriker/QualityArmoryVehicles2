package me.zombie_striker.qav.util;

import com.cryptomorin.xseries.profiles.PlayerProfiles;
import com.cryptomorin.xseries.profiles.builder.ProfileInstruction;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

@SuppressWarnings({"UnstableApiUsage"})
public final class HeadUtil {

    public static String getTexture(ItemStack item) {
        try {
            ProfileInstruction<ItemStack> instruction = XSkull.of(item);
            Class<? extends ProfileInstruction> instructionClass = instruction.getClass();
            Method getProfile = instructionClass.getMethod("getProfile");

            Object profile = getProfile.invoke(instruction);
            if (profile == null) return null;

            Class<PlayerProfiles> clazz = PlayerProfiles.class;
            Class<?> GameProfile = Class.forName("com.mojang.authlib.GameProfile");
            Method getTextureValue = clazz.getMethod("getTextureValue", GameProfile);

            return (String) getTextureValue.invoke(null, profile);
        } catch (Exception | Error ignored) {
            return null;
        }
    }
}

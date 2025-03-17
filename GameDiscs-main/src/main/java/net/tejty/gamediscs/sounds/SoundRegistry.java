package net.tejty.gamediscs.sounds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tejty.gamediscs.GameDiscsMod;

public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GameDiscsMod.MOD_ID);

    public static final RegistryObject<SoundEvent> JUMP = registerSoundEvent("jump");
    public static final RegistryObject<SoundEvent> CLICK = registerSoundEvent("click");
    public static final RegistryObject<SoundEvent> POINT = registerSoundEvent("point");
    public static final RegistryObject<SoundEvent> NEW_BEST = registerSoundEvent("new_best");
    public static final RegistryObject<SoundEvent> GAME_OVER = registerSoundEvent("game_over");
    public static final RegistryObject<SoundEvent> SELECT = registerSoundEvent("select");
    public static final RegistryObject<SoundEvent> CONFIRM = registerSoundEvent("confirm");
    public static final RegistryObject<SoundEvent> EXPLOSION = registerSoundEvent("explosion");
    public static final RegistryObject<SoundEvent> SHOOT = registerSoundEvent("shoot");
    public static final RegistryObject<SoundEvent> SWING = registerSoundEvent("swing");
    public static final RegistryObject<SoundEvent> SWITCH = registerSoundEvent("switch");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(GameDiscsMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

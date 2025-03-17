package net.tejty.gamediscs.util.creativetab;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.tejty.gamediscs.item.ItemRegistry;

public class CreativeTabs {
    public static final CreativeModeTab GAME_DISCS_TAB = new CreativeModeTab("game_discs_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.GAMING_CONSOLE.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            items.add(new ItemStack(ItemRegistry.GAMING_CONSOLE.get()));
            items.add(new ItemStack(ItemRegistry.REDSTONE_CIRCUIT.get()));
            items.add(new ItemStack(ItemRegistry.PROCESSOR.get()));
            items.add(new ItemStack(ItemRegistry.BATTERY.get()));
            items.add(new ItemStack(ItemRegistry.DISPLAY.get()));
            items.add(new ItemStack(ItemRegistry.CONTROL_PAD.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_FLAPPY_BIRD.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_SLIME.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_BLOCKTRIS.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_TNT_SWEEPER.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_PONG.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_FROGGIE.get()));
            items.add(new ItemStack(ItemRegistry.GAME_DISC_RABBIT.get()));
        }
    };

    public static void register(IEventBus eventBus) {

    }
}

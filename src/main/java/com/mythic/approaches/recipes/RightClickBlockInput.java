package com.mythic.approaches.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public record RightClickBlockInput(BlockState state, ItemStack stack) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int index) {
        if (index != 0) throw new IllegalArgumentException("No item for index " + index);
        return this.stack;
    }

    @Override
    public int size() {
        return 1;
    }
}

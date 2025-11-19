package com.mythic.approaches.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public record CauldronInput(IItemHandler handler) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int index) {
        return handler.getStackInSlot(index);
    }

    @Override
    public int size() {
        return handler.getSlots();
    }
}

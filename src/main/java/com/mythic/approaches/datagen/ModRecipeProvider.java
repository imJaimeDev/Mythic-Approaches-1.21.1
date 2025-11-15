package com.mythic.approaches.datagen;

import com.mythic.approaches.recipes.RightClickRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        new RightClickRecipeBuilder(
                new ItemStack(Items.DIAMOND),
                Blocks.DIRT.defaultBlockState(),
                Ingredient.of(Items.APPLE)
        )
                .unlockedBy("has_apple", has(Items.APPLE))
                .save(recipeOutput);
    }
}

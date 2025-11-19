package com.mythic.approaches.datagen;

import com.mythic.approaches.recipes.CauldronRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        CauldronRecipeBuilder.cauldron(Items.DIAMOND)
                .requires(Items.IRON_INGOT)
                .requires(Items.GOLD_INGOT)
                .requires(Items.EMERALD)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput, "diamond_from_gems");

        CauldronRecipeBuilder.cauldron(Items.NETHERITE_INGOT, 2)
                .requires(Items.NETHERITE_SCRAP, 3)
                .requires(Tags.Items.INGOTS_GOLD, 2)
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .save(recipeOutput, "netherite_ingots");
    }
}

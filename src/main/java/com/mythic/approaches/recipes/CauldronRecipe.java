package com.mythic.approaches.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CauldronRecipe implements Recipe<CauldronInput> {
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;

    public CauldronRecipe(NonNullList<Ingredient> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }

    @Override
    public boolean matches(@NotNull CauldronInput input, @NotNull Level level) {
        List<Ingredient> remainingIngredients = new ArrayList<>(this.ingredients);

        // Check each slot in the cauldron
        for (int i = 0; i < input.size(); i++) {
            ItemStack slotStack = input.getItem(i);

            if (slotStack.isEmpty()) continue;

            // Try to match this stack with an ingredient
            boolean matched = false;
            for (int j = 0; j < remainingIngredients.size(); j++) {
                if (remainingIngredients.get(j).test(slotStack)) {
                    remainingIngredients.remove(j);
                    matched = true;
                    break;
                }
            }

            // If we found an item that doesn't match any ingredient, recipe fails
            if (!matched) return false;
        }

        // All ingredients must be used
        return remainingIngredients.isEmpty();
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return this.result;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CauldronInput input, @NotNull HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.CAULDRON_TYPE.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.CAULDRON_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<CauldronRecipe> {
        public static final MapCodec<CauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients),
                ItemStack.CODEC.fieldOf("result").forGetter(CauldronRecipe::getResult)
        ).apply(inst, (ingredients, result) ->
                new CauldronRecipe(NonNullList.copyOf(ingredients), result)));

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.collection(NonNullList::createWithCapacity, Ingredient.CONTENTS_STREAM_CODEC),
                        recipe -> recipe.ingredients,
                        ItemStack.STREAM_CODEC,
                        CauldronRecipe::getResult,
                        CauldronRecipe::new
                );

        @Override
        public @NotNull MapCodec<CauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

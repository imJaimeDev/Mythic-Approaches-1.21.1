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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RightClickBlockRecipe implements Recipe<RightClickBlockInput> {
    private final BlockState inputState;
    private final Ingredient inputItem;
    private final ItemStack result;

    public RightClickBlockRecipe(BlockState inputState, Ingredient inputItem, ItemStack result) {
        this.inputState = inputState;
        this.inputItem = inputItem;
        this.result = result;
    }

    // A list of our ingredients. Does not need to be overridden if you have no ingredients
    // (the default implementation returns an empty list here). It makes sense to cache larger lists in a field.
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(this.inputItem);
    }

    // Grid-based recipes should return whether their recipe can fit in the given dimensions.
    // We don't have a grid, so we just return if any item can be placed in there.
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    // Check whether the given input matches this recipe. The first parameter matches the generic.
    // We check our blockstate and our item stack, and only return true if both match.
    @Override
    public boolean matches(RightClickBlockInput input, @NotNull Level level) {
        return this.inputState == input.state() && this.inputItem.test(input.stack());
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
        return this.result;
    }

    // Return the result of the recipe here, based on the given input. The first parameter matches the generic.
    // IMPORTANT: Always call .copy() if you use an existing result! If you don't, things can and will break,
    // as the result exists once per recipe, but the assembled stack is created each time the recipe is crafted.
    @Override
    public @NotNull ItemStack assemble(@NotNull RightClickBlockInput input, HolderLookup.@NotNull Provider registries) {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.RIGHT_CLICK_BLOCK.get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.RIGHT_CLICK_BLOCK_SERIALIZER.get();
    }

    public BlockState getInputState() {
        return inputState;
    }

    public Ingredient getInputItem() {
        return inputItem;
    }

    public ItemStack getResult() {
        return result;
    }

    public static class RightClickBlockRecipeSerializer implements RecipeSerializer<RightClickBlockRecipe> {
        public static final MapCodec<RightClickBlockRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockState.CODEC.fieldOf("state").forGetter(RightClickBlockRecipe::getInputState),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(RightClickBlockRecipe::getInputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(RightClickBlockRecipe::getResult)
        ).apply(inst, RightClickBlockRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RightClickBlockRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), RightClickBlockRecipe::getInputState,
                        Ingredient.CONTENTS_STREAM_CODEC, RightClickBlockRecipe::getInputItem,
                        ItemStack.STREAM_CODEC, RightClickBlockRecipe::getResult,
                        RightClickBlockRecipe::new
                );

        @Override
        public @NotNull MapCodec<RightClickBlockRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RightClickBlockRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

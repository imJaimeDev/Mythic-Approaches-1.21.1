package com.mythic.approaches.recipes;

import com.mythic.approaches.MythicApproachesMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class CauldronRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public CauldronRecipeBuilder(ItemStack result) {
        this.result = result;
        this.ingredients = NonNullList.create();
    }

    public static CauldronRecipeBuilder cauldron(ItemStack result) {
        return new CauldronRecipeBuilder(result);
    }

    public static CauldronRecipeBuilder cauldron(ItemLike result) {
        return new CauldronRecipeBuilder(new ItemStack(result));
    }

    public static CauldronRecipeBuilder cauldron(ItemLike result, int count) {
        return new CauldronRecipeBuilder(new ItemStack(result, count));
    }

    public CauldronRecipeBuilder requires(TagKey<Item> tag) {
        return requires(Ingredient.of(tag));
    }

    public CauldronRecipeBuilder requires(TagKey<Item> tag, int quantity) {
        for (int i = 0; i < quantity; i++) {
            requires(Ingredient.of(tag));
        }
        return this;
    }

    public CauldronRecipeBuilder requires(ItemLike item) {
        return requires(item, 1);
    }

    public CauldronRecipeBuilder requires(ItemLike item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            requires(Ingredient.of(item));
        }
        return this;
    }

    public CauldronRecipeBuilder requires(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        // Have at least one unlock criterion
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        CauldronRecipe recipe = new CauldronRecipe(this.ingredients, this.result);

        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/cauldron/")));
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull String id) {
        save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MythicApproachesMod.MOD_ID, "cauldron/" + id));
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput) {
        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(this.result.getItem());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                resultId.getNamespace(), "cauldron/" + resultId.getPath()
        );
        save(recipeOutput, id);
    }
}

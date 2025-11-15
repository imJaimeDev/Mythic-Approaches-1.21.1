package com.mythic.approaches.recipes;

import com.mythic.approaches.MythicApproachesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MythicApproachesMod.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MythicApproachesMod.MOD_ID);

    public static final Supplier<RecipeType<RightClickBlockRecipe>> RIGHT_CLICK_BLOCK =
            RECIPE_TYPES.register(
                    "right_click_block",
                    // We need the qualifying generic here due to generics being generics.
                    () -> RecipeType.<RightClickBlockRecipe>simple(ResourceLocation.fromNamespaceAndPath(MythicApproachesMod.MOD_ID, "right_click_block"))
            );

    public static final Supplier<RecipeSerializer<RightClickBlockRecipe>> RIGHT_CLICK_BLOCK_SERIALIZER =
            RECIPE_SERIALIZERS.register("right_click_block", RightClickBlockRecipe.RightClickBlockRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
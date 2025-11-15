package com.mythic.approaches;

import com.mojang.logging.LogUtils;
import com.mythic.approaches.block.ModBlocks;
import com.mythic.approaches.block.entity.ModBlockEntities;
import com.mythic.approaches.item.ModCreativeModeTabs;
import com.mythic.approaches.item.ModItems;
import com.mythic.approaches.recipes.ModRecipes;
import com.mythic.approaches.recipes.RightClickBlockInput;
import com.mythic.approaches.recipes.RightClickBlockRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(MythicApproachesMod.MOD_ID)
public class MythicApproachesMod {
    public static final String MOD_ID = "mythic_approaches";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MythicApproachesMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.BELLADONNA.getId(), ModBlocks.POTTED_BELLADONNA));
        event.enqueueWork(() -> ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.MOLY.getId(), ModBlocks.POTTED_MOLY));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MythicApproachesMod.MOD_ID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void useItemOnBlock(UseItemOnBlockEvent event) {
            if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.BLOCK) return;

            UseOnContext context = event.getUseOnContext();
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);
            ItemStack itemStack = context.getItemInHand();
            RecipeManager recipes = level.getRecipeManager();

            // Create an input and query the recipe.
            RightClickBlockInput input = new RightClickBlockInput(blockState, itemStack);
            Optional<RecipeHolder<RightClickBlockRecipe>> optional = recipes.getRecipeFor(ModRecipes.RIGHT_CLICK_BLOCK.get(), input, level);

            ItemStack result = optional
                    .map(RecipeHolder::value)
                    .map(e -> e.assemble(input, level.registryAccess()))
                    .orElse(ItemStack.EMPTY);

            if (!result.isEmpty()) {
                // TODO: consume item on use
                context.getItemInHand().shrink(1);


                System.out.println(itemStack);
                // If the level is not a server level, don't spawn the entity.
                if (!level.isClientSide()) {
                    ItemEntity entity = new ItemEntity(level,
                            // Center of pos.
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            result);
                    level.addFreshEntity(entity);
                }

                // Cancel the event to stop the interaction pipeline.
                event.cancelWithResult(ItemInteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }
}

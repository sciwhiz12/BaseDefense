package sciwhiz12.basedefense;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sciwhiz12.basedefense.ClientReference.Colors;
import sciwhiz12.basedefense.ClientReference.PropertyOverrides;
import sciwhiz12.basedefense.ClientReference.Textures;
import sciwhiz12.basedefense.client.gui.KeyringScreen;
import sciwhiz12.basedefense.client.gui.KeysmithScreen;
import sciwhiz12.basedefense.client.gui.LocksmithScreen;
import sciwhiz12.basedefense.client.gui.PortableSafeScreen;
import sciwhiz12.basedefense.client.model.ISTERWrapper;
import sciwhiz12.basedefense.client.model.LockedDoorModel;
import sciwhiz12.basedefense.client.render.KeyringLayer;
import sciwhiz12.basedefense.client.render.PadlockedDoorRenderer;
import sciwhiz12.basedefense.client.render.PortableSafeRenderer;

import java.util.Map;
import java.util.function.Function;

import static sciwhiz12.basedefense.BaseDefense.CLIENT;
import static sciwhiz12.basedefense.BaseDefense.LOG;
import static sciwhiz12.basedefense.ClientReference.PropertyOverrides.COLORS;
import static sciwhiz12.basedefense.Reference.*;

/**
 * Class for registering <strong>client-side only</strong> objects of this mod.
 *
 * @author SciWhiz12
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = MODID)
public final class ClientRegistration {
    // Prevent instantiation
    private ClientRegistration() {}

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        LOG.debug(CLIENT, "Setting up on client");
        bindTileEntityRenderers();
        setupRenderLayer();
        event.enqueueWork(ClientRegistration::addCustomLayerRenderers);
        event.enqueueWork(ClientRegistration::registerPropertyOverrides);
        event.enqueueWork(ClientRegistration::registerScreenFactories);
    }

    static void registerPropertyOverrides() {
        LOG.debug(CLIENT, "Registering item property overrides");

        ItemModelsProperties.registerProperty(Items.KEY, COLORS, PropertyOverrides.COLORS_GETTER);
        ItemModelsProperties.registerProperty(Items.LOCK_CORE, COLORS, PropertyOverrides.COLORS_GETTER);
        ItemModelsProperties.registerProperty(Items.PADLOCK, COLORS, PropertyOverrides.COLORS_GETTER);
        ItemModelsProperties.registerProperty(Items.BROKEN_LOCK_PIECES, COLORS, PropertyOverrides.COLORS_GETTER);
    }

    @SubscribeEvent
    static void registerItemColors(ColorHandlerEvent.Item event) {
        LOG.debug(CLIENT, "Registering item colors");
        event.getItemColors()
                .register(Colors.ITEM_COLOR, Items.KEY, Items.LOCK_CORE, Items.PADLOCK, Items.BROKEN_LOCK_PIECES);
    }

    @SubscribeEvent
    static void registerBlockColors(ColorHandlerEvent.Block event) {
        LOG.debug(CLIENT, "Registering block colors");
        event.getBlockColors().register(Colors.LOCKED_DOOR_COLOR, Blocks.LOCKED_IRON_DOOR, Blocks.LOCKED_OAK_DOOR,
                Blocks.LOCKED_BIRCH_DOOR, Blocks.LOCKED_SPRUCE_DOOR, Blocks.LOCKED_JUNGLE_DOOR, Blocks.LOCKED_ACACIA_DOOR,
                Blocks.LOCKED_DARK_OAK_DOOR);
    }

    static void addCustomLayerRenderers() {
        LOG.debug(CLIENT, "Adding custom player layer renderers");
        final Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        final PlayerRenderer defaultRenderer = skinMap.get("default");
        defaultRenderer.addLayer(new KeyringLayer<>(defaultRenderer));
        final PlayerRenderer slimRenderer = skinMap.get("slim");
        slimRenderer.addLayer(new KeyringLayer<>(slimRenderer));
    }

    static void registerScreenFactories() {
        LOG.debug(CLIENT, "Registering screen factories");
        ScreenManager.registerFactory(Containers.KEYSMITH_TABLE, KeysmithScreen::new);
        ScreenManager.registerFactory(Containers.LOCKSMITH_TABLE, LocksmithScreen::new);
        ScreenManager.registerFactory(Containers.KEYRING, KeyringScreen::new);
        ScreenManager.registerFactory(Containers.PORTABLE_SAFE, PortableSafeScreen::new);
    }

    static void bindTileEntityRenderers() {
        LOG.debug(CLIENT, "Binding tile entity renderers");
        ClientRegistry.bindTileEntityRenderer(TileEntities.PADLOCKED_DOOR, PadlockedDoorRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TileEntities.PORTABLE_SAFE, PortableSafeRenderer::new);
    }

    @SubscribeEvent
    static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        ResourceLocation mapLoc = event.getMap().getTextureLocation();
        if (mapLoc.equals(Textures.ATLAS_BLOCKS_TEXTURE)) {
            LOG.debug(CLIENT, "Adding sprites to atlas: {}", mapLoc);
            for (ResourceLocation spriteLoc : Textures.SPRITE_LIST) { event.addSprite(spriteLoc); }
        }
    }

    static void setupRenderLayer() {
        LOG.debug(CLIENT, "Setting up block render layers");
        final RenderType solid = RenderType.getSolid();
        RenderTypeLookup.setRenderLayer(Blocks.KEYSMITH_TABLE, solid);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKSMITH_TABLE, solid);
        final RenderType cutoutMipped = RenderType.getCutoutMipped();
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_IRON_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_BIRCH_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_SPRUCE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_JUNGLE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_ACACIA_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.PADLOCKED_DARK_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_IRON_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_OAK_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_BIRCH_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_SPRUCE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_JUNGLE_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_ACACIA_DOOR, cutoutMipped);
        RenderTypeLookup.setRenderLayer(Blocks.LOCKED_DARK_OAK_DOOR, cutoutMipped);
    }

    @SubscribeEvent
    static void onModelBake(ModelBakeEvent event) {
        LOG.debug(CLIENT, "Overriding models");
        overrideBlockModel(event, Blocks.LOCKED_OAK_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_BIRCH_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_SPRUCE_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_JUNGLE_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_ACACIA_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_DARK_OAK_DOOR, LockedDoorModel::new);
        overrideBlockModel(event, Blocks.LOCKED_IRON_DOOR, LockedDoorModel::new);

        overrideItemModel(event, Items.KEYRING, ISTERWrapper::new);
        overrideItemModel(event, Items.PORTABLE_SAFE, ISTERWrapper::new);
    }

    static void overrideBlockModel(ModelBakeEvent event, Block b, Function<IBakedModel, IBakedModel> transform) {
        for (BlockState blockState : b.getStateContainer().getValidStates()) {
            ModelResourceLocation variantMRL = BlockModelShapes.getModelLocation(blockState);
            overrideModel(event, variantMRL, transform);
        }
    }

    static void overrideItemModel(ModelBakeEvent event, Item i, Function<IBakedModel, IBakedModel> transform) {
        overrideModel(event, new ModelResourceLocation(i.getRegistryName(), "inventory"), transform);
    }

    static void overrideModel(ModelBakeEvent event, ModelResourceLocation mrl,
            Function<IBakedModel, IBakedModel> transform) {
        IBakedModel existingModel = event.getModelRegistry().get(mrl);
        if (existingModel != null) {
            IBakedModel transformedModel = transform.apply(existingModel);
            event.getModelRegistry().put(mrl, transformedModel);
        }
    }
}

package net.beholderface.ephemera.registry;

import at.petrak.hexcasting.common.lib.HexBlocks;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.ephemera.Ephemera;
import net.beholderface.ephemera.items.ConjuredArmorItem;
import net.beholderface.ephemera.items.ConjuredArmorMaterial;
import net.beholderface.ephemera.items.ExtraConnectedSlateItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import static net.beholderface.ephemera.Ephemera.id;

public class EphemeraItemRegistry {
    // Register items through this
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Ephemera.MOD_ID, RegistryKeys.ITEM);
    public static final DeferredRegister<ItemGroup> TABS = DeferredRegister.create(Ephemera.MOD_ID, RegistryKeys.ITEM_GROUP);


    public static void init() {
        ITEMS.register();
        TABS.register();
    }

    public static final ArmorMaterial MEDIA_ARMOR = new ConjuredArmorMaterial();

    public static final RegistrySupplier<ItemGroup> EPHEMERA_STUFF = TABS.register("ephemera", ()->CreativeTabRegistry.create(
            Text.translatable("itemGroup.ephemera.ephemera"), ()->EphemeraItemRegistry.TP_DETECTOR_ITEM.get().getDefaultStack()));

    private static final Item.Settings EPHEMERA_STACKABLE64 = new Item.Settings().arch$tab(EPHEMERA_STUFF).maxCount(64);
    private static final Item.Settings EPHEMERA_STACKABLE16 = new Item.Settings().arch$tab(EPHEMERA_STUFF).maxCount(16);
    private static final Item.Settings EPHEMERA_UNSTACKABLE = new Item.Settings().arch$tab(EPHEMERA_STUFF).maxCount(1);

    public static final RegistrySupplier<ArmorItem> MEDIA_HELMET = ITEMS.register("media_helmet", ()-> new ConjuredArmorItem(MEDIA_ARMOR, ArmorItem.Type.HELMET, new Item.Settings()));
    public static final RegistrySupplier<ArmorItem> MEDIA_CHESTPLATE = ITEMS.register("media_chestplate", ()-> new ConjuredArmorItem(MEDIA_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final RegistrySupplier<ArmorItem> MEDIA_LEGGINGS = ITEMS.register("media_leggings", ()-> new ConjuredArmorItem(MEDIA_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final RegistrySupplier<ArmorItem> MEDIA_BOOTS = ITEMS.register("media_boots", ()-> new ConjuredArmorItem(MEDIA_ARMOR, ArmorItem.Type.BOOTS, new Item.Settings()));
    public static final RegistrySupplier<BlockItem> RELAY_INDEX_ITEM = ITEMS.register("relay_index", ()->new BlockItem(EphemeraBlockRegistry.RELAY_INDEX.get(), EPHEMERA_STACKABLE64));
    public static final RegistrySupplier<BlockItem> TP_DETECTOR_ITEM = ITEMS.register("relay_tp_detector", ()->new BlockItem(EphemeraBlockRegistry.TP_DETECTOR.get(), EPHEMERA_STACKABLE64));

    public static final RegistrySupplier<BlockItem> FAKE_SLATE = ITEMS.register("fakeslate", ()->new BlockItem(EphemeraBlockRegistry.FAKE_SLATE.get(), EPHEMERA_STACKABLE64));
}

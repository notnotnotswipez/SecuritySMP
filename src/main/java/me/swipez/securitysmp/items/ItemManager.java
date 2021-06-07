package me.swipez.securitysmp.items;

import me.swipez.securitysmp.SecuritySMP;
import me.swipez.securitysmp.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class ItemManager {

    public static ItemStack BACK_BED = ItemBuilder.of(Material.RED_BED)
            .name(ChatColor.RED+"Back")
            .build();

    public static ItemStack BLANK_KEY = ItemBuilder.of(Material.TRIPWIRE_HOOK)
            .name(ChatColor.WHITE+"Blank Key")
            .lore(ChatColor.GRAY+"Right click a chest or a door to bind it!")
            .build();

    public static ItemStack ADVANCED_DOOR = ItemBuilder.of(Material.IRON_DOOR)
            .name(ChatColor.WHITE+"Advanced Door")
            .lore(ChatColor.GRAY+"An Advanced door.")
            .build();

    public static ItemStack KEYPAD_DOOR = ItemBuilder.of(Material.IRON_DOOR)
            .name(ChatColor.WHITE+"Keypad Door")
            .enchantment(Enchantment.CHANNELING, 1)
            .lore(ChatColor.GRAY+"Door with a keypad.")
            .build();

    public static ItemStack EYESCANNER_DOOR = ItemBuilder.of(Material.IRON_DOOR)
            .name(ChatColor.WHITE+"Eye Scanner Door")
            .enchantment(Enchantment.CHANNELING, 1)
            .lore(ChatColor.GRAY+"Door with a eyescanner.")
            .build();

    public static ItemStack CELL_TOWER = ItemBuilder.of(Material.SMOOTH_STONE)
            .name(ChatColor.WHITE+"Cell Tower")
            .enchantment(Enchantment.CHANNELING, 1)
            .lore(ChatColor.GRAY+"Protects your base in a 30 block radius. Computers require this.")
            .build();

    public static ItemStack LOCKPICKS = ItemBuilder.of(Material.SHEARS)
            .name(ChatColor.WHITE+"Lockpicks")
            .lore(ChatColor.GRAY+"Can pick normal keyed doors, and keypad doors.")
            .build();

    public static ItemStack ONE_DOLLAR = ItemBuilder.of(Material.PAPER)
            .name(ChatColor.GREEN+"$1")
            .build();

    public static ItemStack NEXT_PAGE = ItemBuilder.of(Material.ARROW)
            .name(ChatColor.RED+"Next Page")
            .build();

    public static ItemStack PREV_PAGE = ItemBuilder.of(Material.ARROW)
            .name(ChatColor.GREEN+"Previous Page")
            .build();

    public static ItemStack SALE_ITEM = ItemBuilder.of(Material.LIME_CONCRETE)
            .name(ChatColor.GREEN+"Put Item Up For Sale")
            .build();

    public static ItemStack SALE_CONFIRM = ItemBuilder.of(Material.LIME_CONCRETE)
            .name(ChatColor.GREEN+"Confirm Sale")
            .build();

    public static ItemStack FLIP_CAMERA = ItemBuilder.of(Material.LIME_CONCRETE)
            .name(ChatColor.GREEN+"Flip 180")
            .build();

    public static ItemStack CAMERA = generateSkull("spectator__dead", ChatColor.YELLOW+"Camera");

    public static ItemStack COMPUTER = generateSkull(UUID.fromString("a02fe839-c21a-40ce-9a29-ade2c97bf770"), ChatColor.YELLOW+"Computer");


    public static void initRecipes(){
        registerGenericSurround(Material.TRIPWIRE_HOOK, Material.COAL, "blank_key", BLANK_KEY);
        registerGenericSurround(Material.SHEARS, Material.IRON_INGOT, "lockpicks", LOCKPICKS);
        registerGenericSurround(Material.IRON_DOOR, Material.REDSTONE, "advanced_door", ADVANCED_DOOR);
        registerLeveledDoor(Material.GOLD_INGOT, Material.STONE_BUTTON, "keypad_door", KEYPAD_DOOR);
        registerLeveledDoor(Material.DIAMOND, Material.FERMENTED_SPIDER_EYE, "eyescanner_door", EYESCANNER_DOOR);
        registerUShape(Material.SMOOTH_STONE, Material.REDSTONE_BLOCK, Material.IRON_BLOCK, "cell_tower", CELL_TOWER);
        registerSideShape(Material.DIAMOND, Material.REDSTONE, Material.IRON_INGOT, Material.CLOCK, "computer", generateSkull(UUID.fromString("a02fe839-c21a-40ce-9a29-ade2c97bf770"), ChatColor.YELLOW+"Computer"));

        registerCameraRecipe();
    }

    private static void registerCameraRecipe(){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(SecuritySMP.plugin, "camera"), CAMERA)
                .shape("IIG","IRG","IIG")
                .setIngredient('I', Material.IRON_INGOT)
                .setIngredient('R', Material.REDSTONE)
                .setIngredient('G', Material.GLASS);
        Bukkit.addRecipe(shapedRecipe);
    }

    private static void registerSideShape(Material centerItem, Material topItem, Material sideItem, Material bottomItem, String key, ItemStack result){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(SecuritySMP.plugin, key), result)
                .shape("ITI","ICI","IBI")
                .setIngredient('I', sideItem)
                .setIngredient('T', topItem)
                .setIngredient('B', bottomItem)
                .setIngredient('C', centerItem);
        Bukkit.addRecipe(shapedRecipe);
    }

    private static void registerUShape(Material centerItem, Material topItem, Material sideItem, String key, ItemStack result){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(SecuritySMP.plugin, key), result)
                .shape("ITI","ICI","III")
                .setIngredient('I', sideItem)
                .setIngredient('T', topItem)
                .setIngredient('C', centerItem);
        Bukkit.addRecipe(shapedRecipe);
    }

    private static void registerLeveledDoor(Material centerItem, Material topItem, String key, ItemStack result){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(SecuritySMP.plugin, key), result)
                .shape("ITI","ICI","III")
                .setIngredient('I', centerItem)
                .setIngredient('T', topItem)
                .setIngredient('C', new RecipeChoice.ExactChoice(ADVANCED_DOOR));
        Bukkit.addRecipe(shapedRecipe);
    }

    private static void registerGenericSurround(Material centerItem, Material surroundingItem, String key, ItemStack result){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(SecuritySMP.plugin, key), result)
                .shape("III","ICI","III")
                .setIngredient('I', surroundingItem)
                .setIngredient('C', centerItem);
        Bukkit.addRecipe(shapedRecipe);
    }

    private static ItemStack generateSkull(UUID player, String itemName){

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setOwner("1758");

        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private static ItemStack generateSkull(String player, String itemName){

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        meta.setOwner(player);

        itemStack.setItemMeta(meta);

        return itemStack;
    }
}

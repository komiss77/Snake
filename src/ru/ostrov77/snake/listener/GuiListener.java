package ru.ostrov77.snake.listener;


import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.ColorUtils;
import ru.komiss77.utils.ItemBuilder;
import ru.ostrov77.snake.Main;
import ru.ostrov77.snake.Manager.AM;




public class GuiListener implements Listener {
    
    public static ItemStack colorChoice;         
    public static ItemStack toLobby;         
    public static ItemStack exitGame;         
    
    
    
    public GuiListener(Main aThis) {
        
        colorChoice = new ItemBuilder(Material.NAME_TAG)
                .name("§aВыбор цвета")
                .build();
        toLobby = new ItemBuilder(Material.MAGMA_CREAM)
                .name("§4Вернуться в лобби")
                .build();
        exitGame = new ItemBuilder(Material.SLIME_BALL)
                .name("§eПокинуть Арену")
                .build();
        
    }


    /*
    private static Entity spawnShepp(final Location location, final DyeColor color, final float yaw) {
        final CraftWorld mcWorld = (CraftWorld) location.getWorld();
        final CustomSheep custom_sheep = new CustomSheep (mcWorld);

        custom_sheep.setLocation(location.getX(), location.getY(), location.getZ(), yaw, location.getPitch());
        ((CraftLivingEntity) custom_sheep.getBukkitEntity()).setRemoveWhenFarAway(false);
        //custom_sheep.setColor(numToEcc(color));
        custom_sheep.setColor(EnumColor.valueOf(color.toString()));
        
        mcWorld.addEntity(custom_sheep, CreatureSpawnEvent.SpawnReason.CUSTOM);
        
        return custom_sheep.getBukkitEntity();
    }
    */
    
    
    
    
    
@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = false)
    public static void onRightClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        final ItemStack item = e.getItem();

        
      /*  if(item!=null && item.getType()==Material.TRIDENT) {
            e.setCancelled(true);
            
            Entity masterSheep = spawnShepp(p.getLocation(), DyeColor.LIME, p.getLocation().getYaw());

            if (masterSheep != null && masterSheep.isValid()) {
                //masterSheep.setPassenger(player);
                //if (!masterSheep.getPassengers().contains(player)) masterSheep.addPassenger(player);
                masterSheep.addPassenger(e.getPlayer());
                ((Sheep) masterSheep).setColor(DyeColor.LIME);

            } else {
                Bukkit.getLogger().info("Unable to spawn first sheep...");
                Bukkit.getLogger().info("The problem is most likely because you have animals disabled, especially if you\'re running Multiverse.");
            }
            
            return;
        }*/
        
        
        
            if (AM.isInGame(e.getPlayer()) ) return;
        
        
            if (item == null || !item.hasItemMeta() ||  !item.getItemMeta().hasDisplayName() ) return;
            
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                
            final String itemName = item.getItemMeta().getDisplayName();

                    
                
                if ( itemName.equals (exitGame.getItemMeta().getDisplayName())) {
                    e.setCancelled(true);
                    AM.GlobalPlayerExit(e.getPlayer());                    
                }
                
               if ( itemName.equals (colorChoice.getItemMeta().getDisplayName())) {
                    e.setCancelled(true);
                    if ( !ApiOstrov.hasGroup(p.getName(),"gamer") ) {
                        p.sendMessage("§cУ вас не куплена привилегия Игроман!");
                        return;
                    }
                    Inventory inventory = Bukkit.createInventory( null, 18, colorChoice.getItemMeta().getDisplayName());
                    
                    for (short col=0; col<=15; col++) {
                        ItemStack is = new ItemStack(Material.WHITE_WOOL);
                        is = ColorUtils.changeColor(is, col);
                        inventory.addItem(is);
                    }

                    p.openInventory(inventory);
                }
                
                
                if ( itemName.equals (toLobby.getItemMeta().getDisplayName())) {
                    e.setCancelled(true);
                    ApiOstrov.sendToServer(e.getPlayer(), "lobby0", "");
                }                    
                
                
                
                 /*   
                case "§bМагазин":
                    e.setCancelled(true);
                    
                    itemstack = new ItemStack(Material.NAME_TAG);
                    itemstack1 = new ItemStack(Material.FEATHER, Files.fastKitBoosts);
                    itemstack2 = new ItemStack(Material.FEATHER, Files.ferrariKitBoosts);
                    ItemMeta itemmeta16 = itemstack.getItemMeta();
                    ItemMeta itemmeta17 = itemstack1.getItemMeta();
                    ItemMeta itemmeta18 = itemstack2.getItemMeta();
                    ArrayList arraylist;
                    
                    if (Shop.findPlayerInColorChooser(p.getName())) {
                        itemmeta16.setDisplayName(ChatColor.GREEN + "Выбор цвета");
                        arraylist = new ArrayList();
                        arraylist.add("§cВыбран");
                        itemmeta16.setLore(arraylist);
                    } else {
                        arraylist = new ArrayList();
                        arraylist.add("" + ChatColor.GREEN + Files.priceColorChooser );
                        itemmeta16.setLore(arraylist);
                        itemmeta16.setDisplayName(ChatColor.RED +"Выбор цвета");
                    }
                    
                    if (Shop.findPlayerInOwned(p.getUniqueId(), "fastsnake")) {
                        itemmeta17.setDisplayName(ChatColor.GREEN + Messages.fastSnakeKitName);
                        arraylist = new ArrayList();
                        arraylist.add(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.itemAlreadyOwned));
                        itemmeta17.setLore(arraylist);
                    } else {
                        arraylist = new ArrayList();
                        //arraylist.add("" + ChatColor.GREEN + Files.fastSnakePrice + " " + EconomyManager.economy.currencyNamePlural());
                        arraylist.add("" + ChatColor.GREEN + Files.fastSnakePrice );
                        itemmeta17.setLore(arraylist);
                        itemmeta17.setDisplayName(ChatColor.RED + Messages.fastSnakeKitName);
                    }
                    
                    if (Shop.findPlayerInOwned(p.getUniqueId(), "ferrarisnake")) {
                        itemmeta18.setDisplayName(ChatColor.GREEN + Messages.ferrariSnakeKitName);
                        arraylist = new ArrayList();
                        arraylist.add(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.itemAlreadyOwned));
                        itemmeta18.setLore(arraylist);
                    } else {
                        arraylist = new ArrayList();
                        arraylist.add("" + ChatColor.GREEN + Files.ferrariSnakePrice );
                        itemmeta18.setLore(arraylist);
                        itemmeta18.setDisplayName(ChatColor.RED + Messages.ferrariSnakeKitName);
                    }
                    
                    itemstack.setItemMeta(itemmeta16);
                    itemstack1.setItemMeta(itemmeta17);
                    itemstack2.setItemMeta(itemmeta18);
                    Inventory shop = Bukkit.createInventory( null, 9, "§5Магазин");
                    shop.setItem(8, itemstack);
                    shop.setItem(0, itemstack1);
                    shop.setItem(1, itemstack2);
                    p.openInventory(shop);
                    break;
                    */
 

            }

    }
    
    
    
    
    
    
    

    /*
    
    public static void givePlayerNametag(Player player) {
      //  if (Shop.findPlayerInColorChooser(player.getName())) {
            ItemStack itemstack = new ItemStack(Material.NAME_TAG);
            ItemMeta itemmeta = itemstack.getItemMeta();
            itemmeta.setDisplayName("§aВыбор цвета");
            itemstack.setItemMeta(itemmeta);
            player.getInventory().setItem(5, itemstack);
      //  }
    }

    public static void giveExitItem(Player player) {
        //player.getInventory().setItem(0, null);
        ItemStack itemstack = new ItemStack( Material.SLIME_BALL, 1 );
        ItemMeta itemmeta = itemstack.getItemMeta();
        itemmeta.setDisplayName("§c§lВыход");
        itemstack.setItemMeta(itemmeta);
        player.getInventory().setItem(8, itemstack);
        //player.updateInventory();
    }

    
    
    public static void givePlayerShop(Player player) {
        ItemStack itemstack = new ItemStack(Material.CHEST, 1);
        ItemMeta itemmeta = itemstack.getItemMeta();
        itemmeta.setDisplayName("§bМагазин");
        itemstack.setItemMeta(itemmeta);
        player.getInventory().setItem(7, itemstack);
       // player.updateInventory();
    }
*/
    
    
    
    
@EventHandler(  priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
            
        if (AM.isInGame(player)) {
            e.setCancelled(true);
            player.closeInventory();
            return;
        }
                
        final ItemStack item = e.getCurrentItem();

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            final String itemName = item.getItemMeta().getDisplayName();
            if ( itemName.equals (exitGame.getItemMeta().getDisplayName()) ||
                    itemName.equals (colorChoice.getItemMeta().getDisplayName()) ||
                    itemName.equals (toLobby.getItemMeta().getDisplayName()) ) {
                e.setCancelled(true);
                return;
            }
        }
        
        
        if (e.getView()==null || e.getView().getTitle()==null || item == null || item.getType() == null ) return;
        
                    
        if ( e.getView().getTitle().equals("§aВыбор цвета") ) {
            e.setCancelled(true);
            if ( !item.getType().toString().endsWith("_WOOL") ) return;

            DyeColor dc=DyeColor.valueOf( item.getType().toString().replace("_WOOL",""));

            AM.getPlayersArena(player).SetSheepColor( player.getName(), dc);

            //player.sendMessage("§fВаши овцы будут "+Main.EnumColor( itemstack.getData().getData() ) +"§lТАКОГО §fцвета!");
            player.sendMessage("§fЦвет ваших овец будет "+ColorUtils.DyeToString(dc));

            player.closeInventory();
        }

        /*
                if (e.getView().getTitle().equals("§5Магазин")) {
                e.setCancelled(true);
                    
                  //  EconomyResponse economyresponse;

                    if (itemstack.getType().equals(Material.FEATHER)) {
                        if (( itemstack.getItemMeta().getLore().get(0)).contains(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.itemAlreadyOwned))) {
                            if (itemstack.getItemMeta().getDisplayName().contains(Messages.fastSnakeKitName)) {
                                Shop.selected.put(player.getUniqueId(), "fastsnake");
                            }

                            if (itemstack.getItemMeta().getDisplayName().contains(Messages.ferrariSnakeKitName)) {
                                Shop.selected.put(player.getUniqueId(), "ferrarisnake");
                            }

                            String s = Messages.selectedKitMessage;

                            s = s.replace("[X]", itemstack.getItemMeta().getDisplayName());
                            s = ChatColor.translateAlternateColorCodes("&".charAt(0), s);
                            player.sendMessage(s);
                            player.closeInventory();
                            return;
                        }

                        String s1;
                        Object object;

                        if (itemstack.getItemMeta().getDisplayName().contains(Messages.fastSnakeKitName)) {
                            
                           // economyresponse = EconomyManager.economy.withdrawPlayer(player, (double) Files.fastSnakePrice);
                           // if (economyresponse.transactionSuccess()) {
                       // if (Ostrov.isPremium(player.getName())) {
                                
                                if (ApiOstrov.moneyGetBalance(player.getName())> (int)Files.fastSnakePrice ) {
                                ApiOstrov.moneyChange(player, -(int)Files.fastSnakePrice, "Покупка в магазине Змейки");
                                
                                object = (List) Shop.owned.get(player.getUniqueId());
                                if (object == null) {
                                    object = new ArrayList();
                                }

                                ((List) object).add("fastsnake");
                                Shop.owned.put(player.getUniqueId(), object);
                                Shop.selected.put(player.getUniqueId(), "fastsnake");
                                s1 = Messages.purchasedAndSelectedMessage;
                                s1 = s1.replace("[X]", itemstack.getItemMeta().getDisplayName());
                                s1 = ChatColor.translateAlternateColorCodes("&".charAt(0), s1);
                                player.sendMessage(s1);
                                
                            } else  player.sendMessage("§cНедостаточно денег на счету");
                      //  } else player.sendMessage("§cУ вас не куплена привилегия!");

                           // inventoryclickevent.setCancelled(true);
                            player.closeInventory();
                            
                            
                            
                            
                            
                            
                        } else  if (itemstack.getItemMeta().getDisplayName().contains(Messages.ferrariSnakeKitName)) {
                           // economyresponse = EconomyManager.economy.withdrawPlayer(player, (double) Files.ferrariSnakePrice);
                            //if (economyresponse.transactionSuccess()) {
                       // if (Ostrov.isPremium(player.getName())) {
                            if (ApiOstrov.moneyGetBalance(player.getName())> (int)Files.ferrariSnakePrice) {
                                ApiOstrov.moneyChange(player, -(int)Files.ferrariSnakePrice, "Покупка в магазине Змейки");
                                
                                object = (List) Shop.owned.get(player.getUniqueId());
                                if (object == null) {
                                    object = new ArrayList();
                                }

                                ((List) object).add("ferrarisnake");
                                Shop.owned.put(player.getUniqueId(), object);
                                Shop.selected.put(player.getUniqueId(), "ferrarisnake");
                                s1 = Messages.purchasedAndSelectedMessage;
                                s1 = s1.replace("[X]", itemstack.getItemMeta().getDisplayName());
                                s1 = ChatColor.translateAlternateColorCodes("&".charAt(0), s1);
                                player.sendMessage(s1);
                            } else  player.sendMessage("§cНедостаточно денег на счету");
                      //  } else player.sendMessage("§cУ вас не куплена привилегия!");

                            player.closeInventory();
                        }
                        
                        
                        
                        
                    } else if (itemstack.getType() == Material.NAME_TAG) {
                        
                        if ( !ApiOstrov.hasGroup(player.getName(),"gamer") ) {
                            player.sendMessage("§cУ вас не куплена привилегия!");
                            return;
                        }

                        if (Shop.findPlayerInColorChooser(player.getName())) {
                            //inventoryclickevent.setCancelled(true);
                            player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.itemAlreadyOwned));
                            return;
                        }

                        if (!( itemstack.getItemMeta().getLore().get(0)).contains(Messages.itemAlreadyOwned) && itemstack.getItemMeta().getDisplayName().contains("Выбор цвета")) {
                            //economyresponse = EconomyManager.economy.withdrawPlayer(player, (double) Files.priceColorChooser);
                           // if (economyresponse.transactionSuccess()) {
                            if (ApiOstrov.moneyGetBalance(player.getName())> (int)Files.priceColorChooser) {
                                ApiOstrov.moneyChange(player, -(int)Files.priceColorChooser, "Покупка в магазине Змейки");
                                
                                Shop.colorChooser.put(player.getUniqueId(), true);
                                player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.purchaseColorChooser));
                                givePlayerNametag(player);
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), Messages.errorInsufficiantFunds));
                            }

                            player.closeInventory();
                        }
                    }*/
                    
                    
                    
                    
    }
   
    
    
    
    

    
    
    
@EventHandler
    public void cancelMove(InventoryDragEvent event) {
        if ( ((Player) event.getWhoClicked()).isOp()) return;
            event.setCancelled(true);
            ((Player) event.getWhoClicked()).updateInventory();
    }
    
    
    
    
}

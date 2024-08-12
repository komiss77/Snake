package ru.ostrov77.snake;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.komiss77.enums.GameState;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.TCUtil;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;

public class ColorChoiceMenu implements InventoryProvider {
    


    @Override
    public void init(final Player p, final InventoryContent content) {
        p.playSound(p.getEyeLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        //content.fillRect(0,0,  5,8, ClickableItem.empty(fill));
        
        for (DyeColor dc : DyeColor.values()) {
            content.add(ClickableItem.of( new ItemBuilder(Material.matchMaterial(dc.name()+"_WOOL"))
                    .name(TCUtil.dyeDisplayName(dc))
                    .build(), e-> {
                        final Arena arena = AM.getArena(p);
                        if (arena!=null && (arena.state==GameState.ОЖИДАНИЕ || arena.state==GameState.СТАРТ)) {
                            arena.players.get(p.getName()).color = dc;
                            p.sendMessage("§6Цвет вашей змейки будет "+TCUtil.dyeDisplayName(dc));
                            p.closeInventory();
                        } else {
                            p.sendMessage("§cВы не на арене!");
                        }
                    }
                )
            );
        }
  
         
    }


    
    
    
    
    
    
    
    
    
}

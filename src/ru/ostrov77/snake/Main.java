package ru.ostrov77.snake;



import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.GameState;
import ru.ostrov77.snake.Manager.AM;
import ru.ostrov77.snake.Manager.Commands;
import ru.ostrov77.snake.Manager.Files;
import ru.ostrov77.snake.Manager.ScoreBoard;
import ru.ostrov77.snake.Manager.Shop;
import ru.ostrov77.snake.listener.GuiListener;
import ru.ostrov77.snake.listener.PlayerListener;






public class Main extends JavaPlugin implements Listener {

private static Main instance;   
    
@Override
    public void onLoad() {
        instance = this;
    }

    public static final Main getInstance() { 
        return Main.instance; 
    }
       
       
@Override
    public void onEnable() {

        log_ok("Super Snake startup....");
        
        AM.Init();
        
        if (!this.getDataFolder().exists())  this.getDataFolder().mkdir();
        else if (this.getConfig() == null)   this.saveDefaultConfig();
        else {
            Files.loadAll();
           // SignsListener.loadFile();
            Messages.loadAll();
        }

        ScoreBoard.StartScore();
        Shop.startup();
        
        Bukkit.getServer().getPluginManager().registerEvents(new GuiListener(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        //EntityTypeRegistry.registerEntities();
        
    //    MinecraftKey minecraftKey = new MinecraftKey("snake_sheep");
        //EntityTypes.b.a(91, minecraftKey, CustomSheep.class);
        //Map<Object, Type<?>> typeMap = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();
        //typeMap.put(minecraftKey.toString(), typeMap.get("minecraft:" + MinecraftKey.a(type.getEntityType().g().getKey()).getKey().split("/")[1]));
        
        //  EntityTypes.a<Entity> entity = EntityTypes.a.a(type.b, EnumCreatureType.CREATURE);
        //   IRegistry.a(IRegistry.ENTITY_TYPE, minecraftKey, entity.a("snake_sheep"));
        
        try {
            //registerCustomEntity(CustomSheep.class, 11);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        Bukkit.getLogger().info("Змейка готова!");
        
    }


    
    
    
    
    
    /*
	public void registerCustomEntity(Class<? extends Entity> entityClass, int id) throws Exception {     

            final ReflectField<RegistryID<EntityTypes<?>>> REGISTRY_ID_FIELD = new ReflectField<>(RegistryMaterials.class, "b");
            final ReflectField<Object[]> ID_TO_CLASS_MAP_FIELD = new ReflectField<>(RegistryID.class, "d");

            // Use reflection to get the RegistryID of entities.
            RegistryID<EntityTypes<?>> registryID = REGISTRY_ID_FIELD.get(IRegistry.ENTITY_TYPE);
            Object[] idToClassMap = ID_TO_CLASS_MAP_FIELD.get(registryID);

            // Save the the ID -> EntityTypes mapping before the registration.
            Object oldValue = idToClassMap[id];

            // Register the EntityTypes object.
            //registryID.a(EntityTypes.a.a(EnumCreatureType.MONSTER).a(sizeWidth, sizeHeight).b().a((String) null), id);
            registryID.a( EntityTypes.a.a(EnumCreatureType.MONSTER).b().a((String) null), id);

            // Restore the ID -> EntityTypes mapping.
            idToClassMap[id] = oldValue;
	}  */  
    
    
    
    
    
    
@Override
    public void onDisable() {
////////////////////////////////////////////////////////////////////////////////

            AM.getAllArenas().values().stream().forEach((ar) -> {
                ApiOstrov.sendArenaData(
                        ar.getName(),
                        GameState.ВЫКЛЮЧЕНА,
                        "§4█████████",
                        "§2§l§oЗмейка",
                        "§5"+ar.getName(),
                        "§4█████████",
                        "выключена",
                        0
                );
            });
                
                 
////////////////////////////////////////////////////////////////////////////////

        Shop.disable();
        Files.saveAll();
        AM.stopAllArena();
      //  EntityTypeRegistry.unregisterEntities();
    }

    
    
    
@Override
    public boolean onCommand(CommandSender commandsender, Command command, String s, String[] astring) {
            boolean flag = Commands.handleCommand(commandsender, command, s, astring);
            return flag;
    }
    
    

    

public static void log_ok(String s) {   Bukkit.getConsoleSender().sendMessage("§fSnake: §2"+ s); }
public static void log_err(String s) {   Bukkit.getConsoleSender().sendMessage("§fSnake: §c"+ s); }
    
    public static void sendBsignMysql(final String name, final String line2, final String line3, final GameState state, final int players) {
        ////////////////////////////////////////////////////////////////////////////////
             ApiOstrov.sendArenaData(
                    name,
                        GameState.РАБОТАЕТ,
                    "§2§l§oЗмейка",
                    "§5"+name,
                    line2,
                    line3,
                    "работает",
                    players
            );
        ////////////////////////////////////////////////////////////////////////////////

    }
    public static void sendBsignChanel(final String name, final String line2, final String line3, final GameState state, final int players) {
        ////////////////////////////////////////////////////////////////////////////////
            ApiOstrov.sendArenaData(
                    name,
                        GameState.РАБОТАЕТ,
                    "§2§l§oЗмейка",
                    "§5"+name,
                    line2,
                    line3,
                    "работает",
                    players
            );
        ////////////////////////////////////////////////////////////////////////////////

    }

    
}

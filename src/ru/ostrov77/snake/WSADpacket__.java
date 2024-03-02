package ru.ostrov77.snake;
/*
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.maed12.rideablemobs.util.Util;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class KeyboardListener extends PacketAdapter {

    public KeyboardListener(Plugin plugin) {
        super(plugin, new PacketType[]{Client.STEER_VEHICLE});
    }

    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();

        if (Util.isWorldEnabled(player)) {
            Entity vehicle = player.getVehicle();

            if (vehicle != null && !(vehicle instanceof Vehicle)) {
                PacketContainer packet = event.getPacket();
                float sideways = (Float) packet.getFloat().read(0);
                float forward = (Float) packet.getFloat().read(1);
                boolean jump = (Boolean) packet.getBooleans().read(0);
                boolean unmount = (Boolean) packet.getBooleans().read(1);

                if (!unmount) {
                    Location playerLocation = player.getLocation();
                    float yaw = playerLocation.getYaw();
                    float pitch = playerLocation.getPitch();

                    vehicle.setRotation(yaw, pitch);
                    double radians = Math.toRadians((double) yaw);
                    double x = (double) (-forward) * Math.sin(radians) + (double) sideways * Math.cos(radians);
                    double z = (double) forward * Math.cos(radians) + (double) sideways * Math.sin(radians);
                    Vector velocity = (new Vector(x, 0.0D, z)).normalize().multiply(0.5D);

                    velocity.setY(vehicle.getVelocity().getY());
                    if (!Double.isFinite(velocity.getX())) {
                        velocity.setX(0);
                    }

                    if (!Double.isFinite(velocity.getZ())) {
                        velocity.setZ(0);
                    }

                    if (vehicle.isInWater() && !Util.canSwim(vehicle) && !vehicle.isOnGround()) {
                        velocity.setY(-0.08D);
                    }

                    if (Util.canFly(vehicle) && !vehicle.isOnGround()) {
                        velocity.setY(-0.08D);
                    }

                    if (jump && (Util.canFly(vehicle) || Util.canSwim(vehicle) && vehicle.isInWater() || vehicle.isOnGround())) {
                        velocity.setY(0.5D);
                    }

                    try {
                        velocity.checkFinite();
                        if (vehicle instanceof EnderDragon) {
                            EnderDragon enderDragon = (EnderDragon) vehicle;

                            enderDragon.setRotation(yaw + 180.0F, pitch);
                        }

                        vehicle.setVelocity(velocity);
                    } catch (Exception exception) {
                        ;
                    }

                }
            }
        }
    }
}
*/
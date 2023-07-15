package ru.ostrov77.snake;

import java.util.Random;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Fireworks {

    public static void genFirework(Location loc) {
        Firework firework = (Firework) loc.getWorld().spawn(loc.clone().add(0,5,0), Firework.class);
        FireworkMeta fireworkmeta = firework.getFireworkMeta();
        Random random = new Random();
        int i = random.nextInt(4);
        Type type = Type.BALL;

        if (i == 0) {
            type = Type.BALL;
        }

        if (i == 1) {
            type = Type.BALL_LARGE;
        }

        if (i == 2) {
            type = Type.BURST;
        }

        if (i == 3) {
            type = Type.CREEPER;
        }

        if (i == 4) {
            type = Type.STAR;
        }

        int j = random.nextInt(17) + 1;
        int k = random.nextInt(17) + 1;
        Color color = getColor(j);
        Color color1 = getColor(k);
        FireworkEffect fireworkeffect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(color).withFade(color1).with(type).trail(random.nextBoolean()).build();

        fireworkmeta.addEffect(fireworkeffect);
        int l = random.nextInt(2) + 1;

        fireworkmeta.setPower(l);
        firework.setFireworkMeta(fireworkmeta);
    }

    private static Color getColor(int i) {
        Color color = null;

        if (i == 1) {
            color = Color.AQUA;
        }

        if (i == 2) {
            color = Color.BLACK;
        }

        if (i == 3) {
            color = Color.BLUE;
        }

        if (i == 4) {
            color = Color.FUCHSIA;
        }

        if (i == 5) {
            color = Color.GRAY;
        }

        if (i == 6) {
            color = Color.GREEN;
        }

        if (i == 7) {
            color = Color.LIME;
        }

        if (i == 8) {
            color = Color.MAROON;
        }

        if (i == 9) {
            color = Color.NAVY;
        }

        if (i == 10) {
            color = Color.OLIVE;
        }

        if (i == 11) {
            color = Color.ORANGE;
        }

        if (i == 12) {
            color = Color.PURPLE;
        }

        if (i == 13) {
            color = Color.RED;
        }

        if (i == 14) {
            color = Color.SILVER;
        }

        if (i == 15) {
            color = Color.TEAL;
        }

        if (i == 16) {
            color = Color.WHITE;
        }

        if (i == 17) {
            color = Color.YELLOW;
        }

        return color;
    }
}

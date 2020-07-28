package me.uquark.realtyprotector.util;

public class LangUtils {
    public static String makePossessionForm(String name) {
        if (name.endsWith("s") || name.endsWith("S"))
            name += "'";
        else
             name += "'s";
        return name;
    }
}

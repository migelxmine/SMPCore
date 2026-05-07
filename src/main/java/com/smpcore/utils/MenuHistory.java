/*
 * Copyright (c) 2025 Miguel Martinho Morbey Rodrigues Moreira (MigelSMP/SMPCore)
 * All Rights Reserved.
 *
 * Este software é propriedade confidencial e proprietária de Miguel Martinho Morbey Rodrigues Moreira.
 * ("Informação Confidencial"). Você não deve divulgar tal Informação
 * Confidencial e deve usá-la apenas de acordo com os termos do
 * contrato de licença que você celebrou com Miguel Martinho Morbey Rodrigues Moreira.
 *
 * É ESTRITAMENTE PROIBIDO DESCOMPILAR, MODIFICAR OU REDISTRIBUIR ESTE SOFTWARE.
 */
package com.smpcore.utils;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuHistory {

    private static final Map<UUID, String> previous = new HashMap<>();

    public static void setPrevious(Player p, String title) {
        if (title == null) return;
        previous.put(p.getUniqueId(), title);
    }

    public static String getPrevious(Player p) {
        return previous.get(p.getUniqueId());
    }

    public static void clearPrevious(Player p) {
        previous.remove(p.getUniqueId());
    }
}
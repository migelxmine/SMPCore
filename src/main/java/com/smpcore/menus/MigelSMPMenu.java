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
/*
 * Copyright (c) 2025 [Seu Nome Aqui] (SMPCore)
 * All Rights Reserved.
 */
package com.smpcore.menus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Interface marcadora para identificar todos os inventários do nosso plugin.
 * Isto impede que o MenuListener afete inventários normais (crafting, baús).
 */
public interface MigelSMPMenu extends InventoryHolder {

    /**
     * Método obrigatório que a Bukkit API exige para qualquer InventoryHolder.
     * @return O inventário associado a este menu.
     */
    @Override
    Inventory getInventory();
}
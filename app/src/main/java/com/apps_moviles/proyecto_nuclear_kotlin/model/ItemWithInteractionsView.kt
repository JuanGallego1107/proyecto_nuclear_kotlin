package com.apps_moviles.proyecto_nuclear_kotlin.model

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation

@DatabaseView("""
    SELECT 
        i.id AS item_id, 
        i.title AS item_title, 
        i.description AS item_description, 
        i.category AS item_category, 
        i.user_id AS item_user_id, 
        i.state_id AS item_state_id, 
        i.publication_type_id AS item_publication_type_id, 
        i.address AS item_address,
        
        st.id AS state_id, 
        st.name AS state_name,
        
        pt.id AS publication_type_id, 
        pt.name AS publication_type_name,
        
        u.id AS user_id, 
        u.fullName AS user_full_name, 
        u.email AS user_email
    FROM items i
    LEFT JOIN users u ON i.user_id = u.id
    LEFT JOIN item_states st ON i.state_id = st.id
    LEFT JOIN publication_types pt ON i.publication_type_id = pt.id
""")
data class ItemWithInteractionsView(

    // Item
    @Embedded(prefix = "item_")
    val item: Item,

    // Usuario del item
    @Embedded(prefix = "user_")
    val user: User,

    // Estado del item
    @Embedded(prefix = "state_")
    val state: ItemState,

    // Tipo de publicación
    @Embedded(prefix = "publication_type_")
    val publicationType: PublicationType,

    // Lista de interacciones básicas (Room solo permite entidades directas)
    @Relation(
        parentColumn = "item_id",
        entityColumn = "item_id",
        entity = Interaction::class
    )
    val interactions: List<InteractionWithRelations>
)

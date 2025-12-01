package com.apps_moviles.proyecto_nuclear_kotlin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apps_moviles.proyecto_nuclear_kotlin.model.Interaction
import com.apps_moviles.proyecto_nuclear_kotlin.model.InteractionState
import com.apps_moviles.proyecto_nuclear_kotlin.model.Item
import com.apps_moviles.proyecto_nuclear_kotlin.model.ItemState
import com.apps_moviles.proyecto_nuclear_kotlin.model.ItemWithInteractionsView
import com.apps_moviles.proyecto_nuclear_kotlin.model.PublicationType
import com.apps_moviles.proyecto_nuclear_kotlin.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, ItemState::class, PublicationType::class, Item::class, InteractionState::class, Interaction::class],
    // views = [ItemWithInteractionsView::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun itemStateDao(): ItemStateDao
    abstract fun publicationTypeDao(): PublicationTypeDao
    abstract fun itemDao(): ItemDao
    abstract fun interactionsStateDao(): InteractionStateDao
    abstract fun interactionDao(): InteractionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proyecto_nuclear_kotlin_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PrepopulateCallback(scope = CoroutineScope(Dispatchers.IO)))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class PrepopulateCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            scope.launch {
                INSTANCE?.userDao()?.insert(
                    User(
                        id = 1,
                        fullName = "Admin",
                        email = "admin@cue.edu.co",
                        password = "123456"
                    )
                )

                INSTANCE?.userDao()?.insert(
                    User(
                        id = 2,
                        fullName = "Juan Perez",
                        email = "juanperez123@cue.edu.co",
                        password = "abc123"
                    )
                )

                INSTANCE?.userDao()?.insert(
                    User(
                        id = 3,
                        fullName = "Mario Salas",
                        email = "mario789@cue.edu.co",
                        password = "abc123"
                    )
                )

                INSTANCE?.userDao()?.insert(
                    User(
                        id = 4,
                        fullName = "Federico Andres Valverde",
                        email = "fedeval123@cue.edu.co",
                        password = "abc123"
                    )
                )

                INSTANCE?.itemStateDao()?.insert(
                    ItemState(
                        id = 1,
                        name = "Publicado"
                    )
                )

                INSTANCE?.itemStateDao()?.insert(
                    ItemState(
                        id = 2,
                        name = "Entregado"
                    )
                )

                INSTANCE?.publicationTypeDao()?.insert(
                    PublicationType(
                        id = 1,
                        name = "Intercambio"
                    )
                )

                INSTANCE?.publicationTypeDao()?.insert(
                    PublicationType(
                        id = 2,
                        name = "Donación"
                    )
                )

                INSTANCE?.publicationTypeDao()?.insert(
                    PublicationType(
                        id = 3,
                        name = "Trueque"
                    )
                )

                INSTANCE?.interactionsStateDao()?.insert(
                    InteractionState(
                        id = 1,
                        name = "En espera"
                    )
                )

                INSTANCE?.interactionsStateDao()?.insert(
                    InteractionState(
                        id = 2,
                        name = "Completado"
                    )
                )

                INSTANCE?.interactionsStateDao()?.insert(
                    InteractionState(
                        id = 3,
                        name = "Sin completar"
                    )
                )
            }
        }
    }
}

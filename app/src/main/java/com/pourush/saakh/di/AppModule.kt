package com.pourush.saakh.di
import android.content.Context
import androidx.room.Room
import com.pourush.saakh.core.crypto.SaakhCryptoManager
import com.pourush.saakh.core.database.ContractorDao
import com.pourush.saakh.core.database.SaakhDatabase
import com.pourush.saakh.core.database.WorkDao
import com.pourush.saakh.core.datastore.UserPreferencesRepository
import com.pourush.saakh.core.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSaakhDatabase(@ApplicationContext context: Context): SaakhDatabase {
        return Room.databaseBuilder(
            context,
            SaakhDatabase::class.java,
            "saakh_database"
        )
            .fallbackToDestructiveMigration(false) //At this early development stage
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkDao(database: SaakhDatabase): WorkDao {
        return database.workDao()
    }

    @Provides
    @Singleton
    fun provideContractorDao(database: SaakhDatabase): ContractorDao {
        return database.contractorDao()
    }
    @Provides
    @Singleton
    fun provideCryptoManager(): SaakhCryptoManager {
        return SaakhCryptoManager()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context.dataStore)
    }
}
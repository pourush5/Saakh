package com.pourush.saakh.di
import android.content.Context
import androidx.room.Room
import com.pourush.saakh.core.database.SaakhDatabase
import com.pourush.saakh.core.database.WorkDao
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
}
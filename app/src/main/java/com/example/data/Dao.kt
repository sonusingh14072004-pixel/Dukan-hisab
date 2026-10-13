package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopProfileDao {
    @Query("SELECT * FROM shop_profile WHERE id = 1 LIMIT 1")
    fun getShopProfile(): Flow<ShopProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveShopProfile(profile: ShopProfile)
}

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents ORDER BY name ASC")
    fun getAllAgents(): Flow<List<Agent>>

    @Query("SELECT * FROM agents WHERE id = :id LIMIT 1")
    suspend fun getAgentById(id: Long): Agent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: Agent): Long

    @Update
    suspend fun updateAgent(agent: Agent)

    @Delete
    suspend fun deleteAgent(agent: Agent)
}

@Dao
interface SettlementDao {
    @Query("SELECT * FROM cash_settlements ORDER BY timestamp DESC")
    fun getAllSettlements(): Flow<List<CashSettlement>>

    @Query("SELECT * FROM cash_settlements WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getSettlementsForDate(dateString: String): Flow<List<CashSettlement>>

    @Query("SELECT * FROM cash_settlements WHERE agentId = :agentId ORDER BY timestamp DESC")
    fun getSettlementsForAgent(agentId: Long): Flow<List<CashSettlement>>

    @Query("SELECT * FROM cash_settlements WHERE dateString = :dateString AND agentId = :agentId ORDER BY timestamp DESC")
    fun getSettlementsForDateAndAgent(dateString: String, agentId: Long): Flow<List<CashSettlement>>

    @Query("SELECT SUM(totalAmount) FROM cash_settlements WHERE dateString = :dateString AND transactionType = :type")
    fun getTotalAmountForDateAndType(dateString: String, type: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: CashSettlement): Long

    @Delete
    suspend fun deleteSettlement(settlement: CashSettlement)

    @Query("DELETE FROM cash_settlements WHERE id = :id")
    suspend fun deleteSettlementById(id: Long)
}

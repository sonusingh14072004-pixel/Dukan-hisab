package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(
    private val shopDao: ShopProfileDao,
    private val agentDao: AgentDao,
    private val settlementDao: SettlementDao
) {
    val shopProfile: Flow<ShopProfile?> = shopDao.getShopProfile()
    val allAgents: Flow<List<Agent>> = agentDao.getAllAgents()
    val allSettlements: Flow<List<CashSettlement>> = settlementDao.getAllSettlements()

    fun getSettlementsForDate(dateString: String): Flow<List<CashSettlement>> {
        return settlementDao.getSettlementsForDate(dateString)
    }

    fun getSettlementsForDateAndAgent(dateString: String, agentId: Long): Flow<List<CashSettlement>> {
        return settlementDao.getSettlementsForDateAndAgent(dateString, agentId)
    }

    suspend fun saveShopProfile(profile: ShopProfile) {
        shopDao.saveShopProfile(profile)
    }

    suspend fun insertAgent(agent: Agent): Long {
        return agentDao.insertAgent(agent)
    }

    suspend fun updateAgent(agent: Agent) {
        agentDao.updateAgent(agent)
    }

    suspend fun deleteAgent(agent: Agent) {
        agentDao.deleteAgent(agent)
    }

    suspend fun saveSettlement(settlement: CashSettlement): Long {
        return settlementDao.insertSettlement(settlement)
    }

    suspend fun deleteSettlement(id: Long) {
        settlementDao.deleteSettlementById(id)
    }

    /**
     * Seeds initial default agents and default shop profile if empty
     */
    suspend fun initializeDefaultsIfNeeded() {
        val currentProfile = shopDao.getShopProfile().firstOrNull()
        if (currentProfile == null) {
            shopDao.saveShopProfile(
                ShopProfile(
                    id = 1,
                    shopName = "Mera Money Transfer & CSC Center",
                    ownerName = "Shopkeeper",
                    phone = "",
                    address = "Main Market",
                    upiId = ""
                )
            )
        }

        val existingAgents = agentDao.getAllAgents().firstOrNull()
        if (existingAgents.isNullOrEmpty()) {
            val defaultAgents = listOf(
                Agent(name = "Airtel Payments Bank", company = "Airtel Bank", notes = "Kiosk Agent Wallet"),
                Agent(name = "Spice Money (Adhikari)", company = "Spice Money", notes = "AEPS & Money Transfer"),
                Agent(name = "PayNearby", company = "Nearby Tech", notes = "DMT & Mini ATM"),
                Agent(name = "SBI Kiosk / CSP", company = "State Bank of India", notes = "Customer deposit & transfer"),
                Agent(name = "Fino Payments Bank", company = "Fino Bank", notes = "Account opening & DMT")
            )
            for (agent in defaultAgents) {
                agentDao.insertAgent(agent)
            }
        }
    }
}

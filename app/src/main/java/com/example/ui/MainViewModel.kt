package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Agent
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.CashSettlement
import com.example.data.DenominationState
import com.example.data.ShopProfile
import com.example.data.TransactionType
import com.example.util.CurrencyFormatter
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(
            shopDao = db.shopProfileDao(),
            agentDao = db.agentDao(),
            settlementDao = db.settlementDao()
        )
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }
    }

    // Shop Profile & Agents
    val shopProfile: StateFlow<ShopProfile?> = repository.shopProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allAgents: StateFlow<List<Agent>> = repository.allAgents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Counter Screen States
    private val _denominationState = MutableStateFlow(DenominationState())
    val denominationState: StateFlow<DenominationState> = _denominationState.asStateFlow()

    private val _selectedAgent = MutableStateFlow<Agent?>(null)
    val selectedAgent: StateFlow<Agent?> = _selectedAgent.asStateFlow()

    private val _transactionType = MutableStateFlow(TransactionType.GIVEN_TO_AGENT)
    val transactionType: StateFlow<TransactionType> = _transactionType.asStateFlow()

    private val _commission = MutableStateFlow(0L)
    val commission: StateFlow<Long> = _commission.asStateFlow()

    private val _remarks = MutableStateFlow("")
    val remarks: StateFlow<String> = _remarks.asStateFlow()

    // Daily Hisab Screen States
    private val _selectedDateString = MutableStateFlow(CurrencyFormatter.getTodayDateString())
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    private val _selectedAgentFilterId = MutableStateFlow<Long?>(null)
    val selectedAgentFilterId: StateFlow<Long?> = _selectedAgentFilterId.asStateFlow()

    // Settlements for selected date and filter
    val dailySettlements: StateFlow<List<CashSettlement>> = combine(
        repository.allSettlements,
        _selectedDateString,
        _selectedAgentFilterId
    ) { all, date, agentFilter ->
        all.filter { item ->
            item.dateString == date && (agentFilter == null || item.agentId == agentFilter)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // WhatsApp Share Dialog State
    private val _activeShareMessage = MutableStateFlow<String?>(null)
    val activeShareMessage: StateFlow<String?> = _activeShareMessage.asStateFlow()

    private val _activeSharePhone = MutableStateFlow<String>("")
    val activeSharePhone: StateFlow<String> = _activeSharePhone.asStateFlow()

    // Counter Actions
    fun updateNoteCount(denomination: Int, count: Int) {
        val safeCount = count.coerceAtLeast(0)
        val current = _denominationState.value
        _denominationState.value = when (denomination) {
            500 -> current.copy(count500 = safeCount)
            200 -> current.copy(count200 = safeCount)
            100 -> current.copy(count100 = safeCount)
            50 -> current.copy(count50 = safeCount)
            20 -> current.copy(count20 = safeCount)
            10 -> current.copy(count10 = safeCount)
            else -> current
        }
    }

    fun updateCoins(coins: Long) {
        _denominationState.value = _denominationState.value.copy(coinsAmount = coins.coerceAtLeast(0L))
    }

    fun selectAgent(agent: Agent?) {
        _selectedAgent.value = agent
    }

    fun setTransactionType(type: TransactionType) {
        _transactionType.value = type
    }

    fun setCommission(amount: Long) {
        _commission.value = amount
    }

    fun setRemarks(text: String) {
        _remarks.value = text
    }

    fun clearCounter() {
        _denominationState.value = DenominationState()
        _commission.value = 0L
        _remarks.value = ""
    }

    fun saveCurrentSettlement(onSuccess: (CashSettlement) -> Unit) {
        val state = _denominationState.value
        if (state.isEmpty) return

        val agent = _selectedAgent.value
        val agentName = agent?.name ?: "General Agent"
        val agentId = agent?.id ?: 0L

        val settlement = CashSettlement(
            agentId = agentId,
            agentName = agentName,
            timestamp = System.currentTimeMillis(),
            dateString = CurrencyFormatter.getTodayDateString(),
            transactionType = _transactionType.value.name,
            count500 = state.count500,
            count200 = state.count200,
            count100 = state.count100,
            count50 = state.count50,
            count20 = state.count20,
            count10 = state.count10,
            coinsAmount = state.coinsAmount,
            totalNotes = state.totalNotes,
            totalAmount = state.grandTotal,
            commission = _commission.value,
            remarks = _remarks.value
        )

        viewModelScope.launch {
            repository.saveSettlement(settlement)
            clearCounter()
            onSuccess(settlement)
        }
    }

    fun saveAndOpenWhatsAppShare(onSaved: () -> Unit = {}) {
        val state = _denominationState.value
        if (state.isEmpty) return

        val agent = _selectedAgent.value
        val agentName = agent?.name ?: "General Agent"
        val agentId = agent?.id ?: 0L

        val settlement = CashSettlement(
            agentId = agentId,
            agentName = agentName,
            timestamp = System.currentTimeMillis(),
            dateString = CurrencyFormatter.getTodayDateString(),
            transactionType = _transactionType.value.name,
            count500 = state.count500,
            count200 = state.count200,
            count100 = state.count100,
            count50 = state.count50,
            count20 = state.count20,
            count10 = state.count10,
            coinsAmount = state.coinsAmount,
            totalNotes = state.totalNotes,
            totalAmount = state.grandTotal,
            commission = _commission.value,
            remarks = _remarks.value
        )

        viewModelScope.launch {
            repository.saveSettlement(settlement)
            val receipt = WhatsAppHelper.buildSettlementReceipt(settlement, shopProfile.value)
            _activeShareMessage.value = receipt
            _activeSharePhone.value = agent?.phone ?: ""
            clearCounter()
            onSaved()
        }
    }

    fun prepareShareForSettlement(settlement: CashSettlement) {
        val receipt = WhatsAppHelper.buildSettlementReceipt(settlement, shopProfile.value)
        val agent = allAgents.value.find { it.id == settlement.agentId }
        _activeShareMessage.value = receipt
        _activeSharePhone.value = agent?.phone ?: ""
    }

    fun prepareShareDaySummary() {
        val list = dailySettlements.value
        if (list.isEmpty()) return
        val summary = WhatsAppHelper.buildDailySummaryReport(
            dateString = _selectedDateString.value,
            settlements = list,
            shopProfile = shopProfile.value
        )
        _activeShareMessage.value = summary
        _activeSharePhone.value = ""
    }

    fun dismissShareDialog() {
        _activeShareMessage.value = null
        _activeSharePhone.value = ""
    }

    // Daily Hisab Actions
    fun setSelectedDate(dateString: String) {
        _selectedDateString.value = dateString
    }

    fun setAgentFilter(agentId: Long?) {
        _selectedAgentFilterId.value = agentId
    }

    fun deleteSettlement(id: Long) {
        viewModelScope.launch {
            repository.deleteSettlement(id)
        }
    }

    // Agent Management Actions
    fun addAgent(name: String, company: String, phone: String, notes: String) {
        viewModelScope.launch {
            val agent = Agent(
                name = name.trim(),
                company = company.trim(),
                phone = phone.trim(),
                notes = notes.trim()
            )
            repository.insertAgent(agent)
        }
    }

    fun updateAgent(agent: Agent) {
        viewModelScope.launch {
            repository.updateAgent(agent)
        }
    }

    fun deleteAgent(agent: Agent) {
        viewModelScope.launch {
            repository.deleteAgent(agent)
        }
    }

    // Shop Profile Actions
    fun updateShopProfile(profile: ShopProfile) {
        viewModelScope.launch {
            repository.saveShopProfile(profile)
        }
    }
}

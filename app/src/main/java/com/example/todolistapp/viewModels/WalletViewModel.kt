package com.example.todolistapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolistapp.BubuApplication
import com.example.todolistapp.models.*
import com.example.todolistapp.repositories.WalletRepositoryInterface
import com.example.todolistapp.repositories.UserRepositoryInterface
import com.example.todolistapp.uiStates.*
import com.example.todolistapp.models.ErrorModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okio.IOException

class WalletViewModel(
    private val walletRepository: WalletRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _listState = MutableStateFlow<WalletListStatusUIState>(WalletListStatusUIState.Start)
    val listState: StateFlow<WalletListStatusUIState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<WalletDetailStatusUIState>(WalletDetailStatusUIState.Start)
    val detailState: StateFlow<WalletDetailStatusUIState> = _detailState.asStateFlow()

    private val _mutationState = MutableStateFlow<WalletMutationStatusUIState>(WalletMutationStatusUIState.Start)
    val mutationState: StateFlow<WalletMutationStatusUIState> = _mutationState.asStateFlow()

    // Summary state for wallet details page
    private val _summaryState = MutableStateFlow<WalletSummaryStatusUIState>(WalletSummaryStatusUIState.Start)
    val summaryState: StateFlow<WalletSummaryStatusUIState> = _summaryState.asStateFlow()

    // GET ALL
    fun fetchWallets() {
        viewModelScope.launch {
            _listState.value = WalletListStatusUIState.Loading

            try {
                walletRepository.getAllWallets().enqueue(object : Callback<GetAllWalletsResponse> {
                    override fun onResponse(call: Call<GetAllWalletsResponse>, res: Response<GetAllWalletsResponse>) {
                        if (res.isSuccessful) {
                            _listState.value = WalletListStatusUIState.Success(res.body()!!.data)
                        } else {
                            val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                            _listState.value = WalletListStatusUIState.Failed(error.errors)
                        }
                    }

                    override fun onFailure(call: Call<GetAllWalletsResponse>, t: Throwable) {
                        _listState.value = WalletListStatusUIState.Failed(t.localizedMessage)
                    }
                })
            } catch (e: IOException) {
                _listState.value = WalletListStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    // GET DETAIL
    fun fetchWalletDetail(walletId: Int) {
        viewModelScope.launch {
            _detailState.value = WalletDetailStatusUIState.Loading

            try {
                walletRepository.getWalletById(walletId)
                    .enqueue(object : Callback<GetWalletResponse> {
                        override fun onResponse(call: Call<GetWalletResponse>, res: Response<GetWalletResponse>) {
                            if (res.isSuccessful) {
                                _detailState.value = WalletDetailStatusUIState.Success(res.body()!!.data)
                            } else {
                                val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                                _detailState.value = WalletDetailStatusUIState.Failed(error.errors)
                            }
                        }

                        override fun onFailure(call: Call<GetWalletResponse>, t: Throwable) {
                            _detailState.value = WalletDetailStatusUIState.Failed(t.localizedMessage)
                        }
                    })

            } catch (e: IOException) {
                _detailState.value = WalletDetailStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    // CREATE
    fun createWallet(name: String, balance: Double) {
        viewModelScope.launch {
            _mutationState.value = WalletMutationStatusUIState.Loading

            try {
                walletRepository.createWallet(WalletRequest(name, balance))
                    .enqueue(object : Callback<PostWalletResponse> {
                        override fun onResponse(call: Call<PostWalletResponse>, res: Response<PostWalletResponse>) {
                            if (res.isSuccessful) {
                                _mutationState.value = WalletMutationStatusUIState.Success("Wallet created")
                                fetchWallets()
                            } else {
                                val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                                _mutationState.value = WalletMutationStatusUIState.Failed(error.errors)
                            }
                        }

                        override fun onFailure(call: Call<PostWalletResponse>, t: Throwable) {
                            _mutationState.value = WalletMutationStatusUIState.Failed(t.localizedMessage)
                        }
                    })

            } catch (e: IOException) {
                _mutationState.value = WalletMutationStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    // UPDATE
    fun updateWallet(walletId: Int, name: String, balance: Double) {
        viewModelScope.launch {
            _mutationState.value = WalletMutationStatusUIState.Loading

            try {
                walletRepository.updateWallet(walletId, WalletRequest(name, balance))
                    .enqueue(object : Callback<PostWalletResponse> {
                        override fun onResponse(call: Call<PostWalletResponse>, res: Response<PostWalletResponse>) {
                            if (res.isSuccessful) {
                                _mutationState.value = WalletMutationStatusUIState.Success("Wallet updated")
                                fetchWallets()
                            } else {
                                val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                                _mutationState.value = WalletMutationStatusUIState.Failed(error.errors)
                            }
                        }

                        override fun onFailure(call: Call<PostWalletResponse>, t: Throwable) {
                            _mutationState.value = WalletMutationStatusUIState.Failed(t.localizedMessage)
                        }
                    })

            } catch (e: IOException) {
                _mutationState.value = WalletMutationStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    // DELETE
    fun deleteWallet(walletId: Int) {
        viewModelScope.launch {
            _mutationState.value = WalletMutationStatusUIState.Loading

            try {
                walletRepository.deleteWallet(walletId)
                    .enqueue(object : Callback<DeleteWalletResponse> {
                        override fun onResponse(call: Call<DeleteWalletResponse>, res: Response<DeleteWalletResponse>) {
                            if (res.isSuccessful) {
                                _mutationState.value = WalletMutationStatusUIState.Success("Wallet deleted")
                                fetchWallets()
                            } else {
                                val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                                _mutationState.value = WalletMutationStatusUIState.Failed(error.errors)
                            }
                        }

                        override fun onFailure(call: Call<DeleteWalletResponse>, t: Throwable) {
                            _mutationState.value = WalletMutationStatusUIState.Failed(t.localizedMessage)
                        }
                    })

            } catch (e: IOException) {
                _mutationState.value = WalletMutationStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    // SET DEFAULT
    fun setDefault(walletId: Int) {
        viewModelScope.launch {
            _mutationState.value = WalletMutationStatusUIState.Loading

            try {
                walletRepository.setDefaultWallet(walletId)
                    .enqueue(object : Callback<SetDefaultWalletResponse> {
                        override fun onResponse(call: Call<SetDefaultWalletResponse>, res: Response<SetDefaultWalletResponse>) {
                            if (res.isSuccessful) {
                                _mutationState.value = WalletMutationStatusUIState.Success("Default wallet set")
                                fetchWallets()
                            } else {
                                val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                                _mutationState.value = WalletMutationStatusUIState.Failed(error.errors)
                            }
                        }

                        override fun onFailure(call: Call<SetDefaultWalletResponse>, t: Throwable) {
                            _mutationState.value = WalletMutationStatusUIState.Failed(t.localizedMessage)
                        }
                    })

            } catch (e: IOException) {
                _mutationState.value = WalletMutationStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    // GET WALLET SUMMARY (with optional date filter)
    fun fetchWalletSummary(walletId: Int, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _summaryState.value = WalletSummaryStatusUIState.Loading

            try {
                walletRepository.getWalletSummary(walletId, startDate, endDate)
                    .enqueue(object : Callback<GetWalletSummaryResponse> {
                        override fun onResponse(call: Call<GetWalletSummaryResponse>, res: Response<GetWalletSummaryResponse>) {
                            if (res.isSuccessful) {
                                _summaryState.value = WalletSummaryStatusUIState.Success(res.body()!!.data)
                            } else {
                                val error = Gson().fromJson(res.errorBody()!!.charStream(), ErrorModel::class.java)
                                _summaryState.value = WalletSummaryStatusUIState.Failed(error.errors)
                            }
                        }

                        override fun onFailure(call: Call<GetWalletSummaryResponse>, t: Throwable) {
                            _summaryState.value = WalletSummaryStatusUIState.Failed(t.localizedMessage)
                        }
                    })

            } catch (e: IOException) {
                _summaryState.value = WalletSummaryStatusUIState.Failed(e.localizedMessage)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as BubuApplication)

                val walletRepository = application.container.walletRepository
                val userRepository = application.container.userRepository

                WalletViewModel(
                    walletRepository = walletRepository,
                    userRepository = userRepository
                )
            }
        }
    }

    fun resetListState() { _listState.value = WalletListStatusUIState.Start }
    fun resetDetailState() { _detailState.value = WalletDetailStatusUIState.Start }
    fun resetMutationState() { _mutationState.value = WalletMutationStatusUIState.Start }
    fun resetSummaryState() { _summaryState.value = WalletSummaryStatusUIState.Start }
}

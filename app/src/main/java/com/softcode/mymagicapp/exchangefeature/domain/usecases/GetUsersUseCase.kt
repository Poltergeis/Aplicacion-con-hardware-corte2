package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.UserEntity
import com.softcode.mymagicapp.core.domain.repository.ExchangeRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class GetUsersUseCase(
    private val exchangeRepository: ExchangeRepository
) {
    suspend operator fun invoke(search: String = ""): OperationResult<List<UserEntity>, String, String> {
        return exchangeRepository.getUsers(search)
    }
}

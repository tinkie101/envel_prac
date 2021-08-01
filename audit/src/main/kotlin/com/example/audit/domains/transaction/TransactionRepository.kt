package com.example.audit.domains.transaction

import org.springframework.data.repository.CrudRepository
import java.util.*


interface TransactionRepository : CrudRepository<Transaction, UUID> {
    fun findAllByAccountId(accountId: UUID): List<Transaction>
}
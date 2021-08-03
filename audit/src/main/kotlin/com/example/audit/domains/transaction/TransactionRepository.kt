package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import org.springframework.data.repository.CrudRepository
import java.util.*


interface TransactionRepository : CrudRepository<Transaction, UUID> {
    fun findAllByAccountId(accountId: UUID): List<Transaction>

    fun findAllByAccountIdAndType(accountId: UUID, type: TransactionTypes): List<Transaction>
}
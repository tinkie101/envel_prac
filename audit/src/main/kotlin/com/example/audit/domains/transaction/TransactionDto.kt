package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class TransactionDto(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: TransactionTypes,
    val createdOn: LocalDateTime,
    val userId: UUID
)
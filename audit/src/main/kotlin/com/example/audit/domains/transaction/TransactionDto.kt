package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionType
import java.math.BigDecimal
import java.util.*

class TransactionDto(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: TransactionType
)
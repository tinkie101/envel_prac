package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionType
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
class Transaction(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    val accountId: UUID,
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    val type: TransactionType
)
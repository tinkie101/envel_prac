package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import com.sun.istack.NotNull
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(indexes = [Index(name = "accountIdIdx", columnList = "accountId", unique = true)])
class Transaction(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    val accountId: UUID,
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    val type: TransactionTypes,

    @CreationTimestamp
    @NotNull
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
    val createdOn: LocalDateTime? = null,

    val userId: UUID
)
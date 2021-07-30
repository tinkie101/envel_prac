package com.example.account.domains.account

import com.sun.istack.NotNull
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Account(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    var balance: BigDecimal = BigDecimal.ZERO,

    @CreationTimestamp
    @NotNull
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
    val createdOn: LocalDateTime? = null
)
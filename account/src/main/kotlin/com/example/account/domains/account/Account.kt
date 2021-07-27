package com.example.account.domains.account

import java.math.BigDecimal
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Account(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    var balance: BigDecimal = BigDecimal.ZERO
)
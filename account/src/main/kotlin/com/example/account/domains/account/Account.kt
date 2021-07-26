package com.example.account.domains.account

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Account(
    @Id
    val id: UUID,
    var balance: BigDecimal
)
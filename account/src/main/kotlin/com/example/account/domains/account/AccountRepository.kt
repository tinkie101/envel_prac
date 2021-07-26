package com.example.account.domains.account

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AccountRepository: CrudRepository<Account, UUID>
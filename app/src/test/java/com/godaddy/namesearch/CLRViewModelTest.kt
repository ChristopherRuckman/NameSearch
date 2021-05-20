package com.godaddy.namesearch

import com.godaddy.namesearch.model.Domain
import com.godaddy.namesearch.model.DomainUser
import com.godaddy.namesearch.viewmodel.DomainViewModel
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CLRViewModelTest {
    lateinit var vm: DomainViewModel
    @MockK
    lateinit var user: DomainUser
    @MockK
    lateinit var dsDomainResults: List<Domain>

    @Before
    fun setup() {
        vm = DomainViewModel()
        dsDomainResults = setupMockDomains()
    }

    // Setup

    private fun setupMockDomains(): List<Domain> {
        return listOf(
                Domain("ebay", "12.99", 2, true),
                Domain("yahoo", "0.33", 99, false),
                Domain("google", "10000.00", 1, false),
                Domain("momNpop", "0.01", 100, false)
        )
    }
    
    // Coroutine tests
    // Login

    @Test
    fun `test perform empty login still logs in`() = runBlocking {
        user = DomainUser("", "")
        assertEquals(Unit, vm.performLogin(user.first, user.last))
    }

    // Domains

    @Test
    fun `test get list of domains`() = runBlocking {
        assertNotNull(dsDomainResults)
    }

    @Test
    fun `test count list of domains`() = runBlocking {
        assertEquals(4, dsDomainResults.count())
    }
}
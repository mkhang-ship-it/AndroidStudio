package com.aistudy.aistudy.domain.rules

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AbuseFilterTest {
    @Test fun normalPass() {
        assertFalse(AbuseFilter.isBlocked("Giải phương trình x^2 + 2x + 1 = 0"))
    }

    @Test fun spamBlocked() {
        assertTrue(AbuseFilter.isBlocked("This is a spam offer, buy now"))
    }

    @Test fun linkBlocked() {
        assertTrue(AbuseFilter.isBlocked("Xem tại https://example.com/abc"))
    }

    @Test fun tooLongBlocked() {
        assertTrue(AbuseFilter.isBlocked("a".repeat(2001)))
    }

    @Test fun emptyPass() {
        assertFalse(AbuseFilter.isBlocked(""))
        assertFalse(AbuseFilter.isBlocked("   "))
    }
}

package com.aistudy.aistudy.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aistudy.aistudy.ui.shell.Routes
import com.aistudy.aistudy.ui.shell.TOP_LEVEL_DESTINATIONS
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke test cho cau truc tab cua [com.aistudy.aistudy.ui.shell.MainShell].
 *
 * NOTE: Khong dung `createAndroidComposeRule<MainActivity>()` boi can
 * AppDependencies/ServiceLocator init phuc tap (Room, EncryptedSharedPreferences,
 * AI client, ...). Vi vay test o muc TOP_LEVEL_DESTINATIONS de dam bao:
 *   - Dong 5 tab top-level
 *   - Route mapping dung thu tu va gia tri mong doi
 *
 * Neu sau nay co the tao AppDependencies gia trong androidTest, co the nang
 * len test UI that voi `createAndroidComposeRule<MainActivity>()`.
 */
@RunWith(AndroidJUnit4::class)
class MainShellSmokeTest {

    @Test
    fun topLevelDestinations_coDung5Tab() {
        assertEquals(
            "Phai co dung 5 tab top-level: Ask, History, Practice, Progress, Profile",
            5,
            TOP_LEVEL_DESTINATIONS.size
        )
    }

    @Test
    fun topLevelDestinations_routeASK() {
        assertEquals(Routes.ASK, TOP_LEVEL_DESTINATIONS[0].route)
    }

    @Test
    fun topLevelDestinations_routeHISTORY() {
        assertEquals(Routes.HISTORY, TOP_LEVEL_DESTINATIONS[1].route)
    }

    @Test
    fun topLevelDestinations_routePRACTICE() {
        assertEquals(Routes.PRACTICE, TOP_LEVEL_DESTINATIONS[2].route)
    }

    @Test
    fun topLevelDestinations_routePROGRESS() {
        assertEquals(Routes.PROGRESS, TOP_LEVEL_DESTINATIONS[3].route)
    }

    @Test
    fun topLevelDestinations_routePROFILE() {
        assertEquals(Routes.PROFILE, TOP_LEVEL_DESTINATIONS[4].route)
    }

    @Test
    fun topLevelDestinations_thuTuDung() {
        val expectedOrder = listOf(
            Routes.ASK,
            Routes.HISTORY,
            Routes.PRACTICE,
            Routes.PROGRESS,
            Routes.PROFILE
        )
        val actualOrder = TOP_LEVEL_DESTINATIONS.map { it.route }
        assertEquals("Thu tu tab phai dung", expectedOrder, actualOrder)
    }

    @Test
    fun topLevelDestinations_labelResKhongTrung() {
        val labelResIds = TOP_LEVEL_DESTINATIONS.map { it.labelRes }
        assertEquals(
            "labelRes khong duoc trung",
            labelResIds.size,
            labelResIds.toSet().size
        )
    }

    @Test
    fun topLevelDestinations_moiTabDeuCoIconVaOutlinedIcon() {
        TOP_LEVEL_DESTINATIONS.forEach { dest ->
            assertEquals(
                "Route ${dest.route} phai co icon filled",
                true,
                dest.icon != null
            )
            assertEquals(
                "Route ${dest.route} phai co icon outlined",
                true,
                dest.outlinedIcon != null
            )
        }
    }
}

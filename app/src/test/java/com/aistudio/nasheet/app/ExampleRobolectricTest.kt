package com.aistudio.nasheet.app

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("نشيط (Nasheet)", appName)
  }

  @Test
  fun `launch MainActivity`() {
    ActivityScenario.launch(MainActivity::class.java).use { scenario ->
      // Succeeded to launch if no exception is thrown
    }
  }
}

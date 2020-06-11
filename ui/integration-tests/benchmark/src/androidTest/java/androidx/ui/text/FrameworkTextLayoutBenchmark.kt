/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.ui.text

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.BoringLayout
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.ui.integration.test.Alphabet
import androidx.ui.integration.test.TextBenchmarkTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.roundToInt

@LargeTest
@RunWith(Parameterized::class)
class FrameworkTextLayoutBenchmark(private val textLength: Int) {
    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "length={0} ")
        fun initParameters() = arrayOf(32, 512)
    }

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @get:Rule
    val textBenchmarkRule = TextBenchmarkTestRule(Alphabet.Latin)

    private fun toDip(value: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        instrumentationContext.resources.displayMetrics
    )

    @Test
    fun staticLayoutCreation() {
        textBenchmarkRule.generator { textGenerator ->
            benchmarkRule.measureRepeated {
                val (text, paint, width) = runWithTimingDisabled {
                    val text = textGenerator.nextParagraph(textLength)
                    val paint = TextPaint().apply {
                        this.typeface = Typeface.DEFAULT
                        this.color = Color.BLACK
                        this.textSize = toDip(8f)
                    }
                    val width = toDip(160f).roundToInt()
                    Triple(text, paint, width)
                }
                StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build()
            }
        }
    }

    @Test
    fun boringLayoutCreation() {
        textBenchmarkRule.generator { textGenerator ->
            benchmarkRule.measureRepeated {
                val (text, paint) = runWithTimingDisabled {
                    val text = textGenerator.nextParagraph(textLength)
                    val paint = TextPaint().apply { this.typeface = Typeface.DEFAULT }
                    Pair(text, paint)
                }
                val metrics = BoringLayout.isBoring(text, paint)
                BoringLayout(
                    text,
                    paint,
                    metrics.width,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0f,
                    metrics,
                    true
                )
            }
        }
    }
}
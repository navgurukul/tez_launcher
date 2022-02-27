package de.mm20.launcher2.ui.launcher.widgets.clock.parts

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import de.mm20.launcher2.preferences.Settings
import de.mm20.launcher2.ui.launcher.widgets.clock.ClockWidgetVM
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class DatePartProvider: PartProvider {
    override fun getRanking(context: Context): Flow<Int> = flow {
        emit(1)
    }

    private val time = MutableStateFlow(System.currentTimeMillis())

    override fun setTime(time: Long) {
        this.time.value = time
    }

    @Composable
    override fun Component(layout: Settings.ClockWidgetSettings.ClockWidgetLayout) {
        val time by this.time.collectAsState(System.currentTimeMillis())
        val verticalLayout = layout == Settings.ClockWidgetSettings.ClockWidgetLayout.Vertical
        val context = LocalContext.current
        TextButton(onClick = {
            val startMillis = System.currentTimeMillis()
            val builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            ContentUris.appendId(builder, startMillis)
            val intent = Intent(Intent.ACTION_VIEW)
                .setData(builder.build())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(intent)
        }) {
            if (verticalLayout) {
                Text(
                    text = DateUtils.formatDateTime(
                        context,
                        time,
                        DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            } else {
                val line1Format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE")
                val line2Format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM dd yyyy")
                val format = SimpleDateFormat("$line1Format\n$line2Format")
                Text(
                    text = format.format(time),
                    lineHeight = 1.2.em,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White
                )
            }
        }
    }

}
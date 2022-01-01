/*
 * Copyright (c) 2019 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.orientation.view.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import net.mm2d.orientation.control.Orientation
import net.mm2d.orientation.settings.DesignPreference
import net.mm2d.orientation.settings.OrientationPreference
import net.mm2d.orientation.settings.PreferenceRepository

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAppWidget(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        private val preferenceFlow = combine(
            PreferenceRepository.get().orientationPreferenceFlow,
            PreferenceRepository.get().designPreferenceRepository.flow,
        ) { orientation, desing ->
            val o = if (orientation.enabled) orientation.orientation else Orientation.INVALID
            orientation.copy(orientation = o) to desing
        }

        fun initialize(context: Context) {
            val widgetManager: AppWidgetManager = context.getSystemService()!!
            scope.launch {
                preferenceFlow.collect { (orientation, design) ->
                    widgetManager.getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))?.forEach {
                        updateAppWidget(context, widgetManager, it, orientation, design)
                    }
                }
            }
        }

        private fun updateAppWidget(
            context: Context,
            widgetManager: AppWidgetManager,
            widgetIds: IntArray
        ) {
            scope.launch {
                preferenceFlow.take(1).collect { (orientation, design) ->
                    widgetIds.forEach {
                        updateAppWidget(context, widgetManager, it, orientation, design)
                    }
                }
            }
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            id: Int,
            orientation: OrientationPreference,
            design: DesignPreference
        ) {
            val views = RemoteViewsCreator.create(context, orientation, design, true)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}

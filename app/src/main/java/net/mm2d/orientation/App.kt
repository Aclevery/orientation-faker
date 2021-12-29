/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.orientation

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import net.mm2d.orientation.control.ForegroundPackageSettings
import net.mm2d.orientation.control.OrientationHelper
import net.mm2d.orientation.service.MainController
import net.mm2d.orientation.settings.PreferenceRepository
import net.mm2d.orientation.settings.Settings
import net.mm2d.orientation.tabs.CustomTabsHelper
import net.mm2d.orientation.view.notification.NotificationHelper
import net.mm2d.orientation.view.widget.WidgetProvider

@Suppress("unused")
open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeOverrideWhenDebug()
        Settings.initialize(this)
        PreferenceRepository.initialize(this)
        MainController.initialize(this)
        NotificationHelper.createChannel(this)
        ForegroundPackageSettings.initialize(this)
        CustomTabsHelper.initialize(this)
        OrientationHelper.initialize(this)
        WidgetProvider.initialize(this)
    }

    protected open fun initializeOverrideWhenDebug() {
        setUpStrictMode()
    }

    private fun setUpStrictMode() {
        StrictMode.setThreadPolicy(ThreadPolicy.LAX)
        StrictMode.setVmPolicy(VmPolicy.LAX)
    }
}

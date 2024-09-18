package com.standalone.migrationwithcompose

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun AppViewController(): UIViewController = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) { IosApp() }
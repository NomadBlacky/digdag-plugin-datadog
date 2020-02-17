package dev.nomadblacky.digdag.plugin.datadog.util

import com.google.common.base.Optional

trait GoogleOptionalOps {
  implicit class RichGoogleOptional[A](gOpt: Optional[A]) {
    def asScala: Option[A] = Option(gOpt.orNull())
  }
}

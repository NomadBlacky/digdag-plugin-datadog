package dev.nomadblacky.digdag.plugin.datadog.operator

import io.digdag.client.config.Config

import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

trait DigdagConfigOps {
  implicit class RichConfig(config: Config) {
    def apply[A](key: String)(implicit tag: ClassTag[A]): A =
      config.get(key, tag.runtimeClass).asInstanceOf[A]

    def getSeqOrEmpty[A](key: String)(implicit tag: ClassTag[A]): Seq[A] =
      config.getListOrEmpty(key, tag.runtimeClass).asScala.toSeq.asInstanceOf[Seq[A]]

    def getOption[A](key: String)(implicit tag: ClassTag[A]): Option[A] = {
      val opt = config.getOptional(key, tag.runtimeClass)
      if (opt.isPresent) Option(opt.get().asInstanceOf[A]) else None
    }
  }
}

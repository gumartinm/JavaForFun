// Author: Gustavo Martin Morcuende
package de.example.spark.datadog.sink.infrastructure.filesystem
import java.net.URI

import org.apache.hadoop.fs.permission.FsPermission
import org.apache.hadoop.fs.{FSDataInputStream, FSDataOutputStream, FileStatus, FileSystem, Path}
import org.apache.hadoop.util.Progressable

object ResourceFileSystem {
  val Uri: URI = URI.create("classpath:/")
}

class ResourceFileSystem extends FileSystem {
  override def getScheme: String = "classpath"

  override def getUri: URI = ResourceFileSystem.Uri

  override def open(f: Path, bufferSize: Int): FSDataInputStream = {
    val inputStream = Thread.currentThread()
      .getContextClassLoader
      .getResourceAsStream(f.toUri.getPath)

    new FSDataInputStream(inputStream)
  }

  override def create(f: Path, permission: FsPermission, overwrite: Boolean, bufferSize: Int, replication: Short,
                      blockSize: Long, progress: Progressable): FSDataOutputStream = ???
  
  override def append(f: Path, bufferSize: Int, progress: Progressable): FSDataOutputStream = ???

  override def rename(src: Path, dst: Path): Boolean = ???

  override def delete(f: Path, recursive: Boolean): Boolean = ???

  override def listStatus(f: Path): Array[FileStatus] = ???

  override def setWorkingDirectory(new_dir: Path): Unit = ???

  override def getWorkingDirectory: Path = ???
  override def mkdirs(f: Path, permission: FsPermission): Boolean = ???
  override def getFileStatus(f: Path): FileStatus = ???
}

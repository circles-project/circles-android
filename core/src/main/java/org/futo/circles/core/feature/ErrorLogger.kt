package org.futo.circles.core.feature

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ErrorLogger {
    private const val FolderName = "log"
    private const val logFileName = "log.txt"
    fun appendLog(error: String) {
        try {
            val logFolder = File("/data/user/0/org.futo.circles/files", FolderName)
            if (!logFolder.exists()) {
                logFolder.mkdirs()
            }
            val logFile = File(logFolder, logFileName)
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            val calendar: Calendar = Calendar.getInstance()
            buf.append(
                SimpleDateFormat(
                    "MMM dd yyyy, h:mm a",
                    Locale.getDefault()
                ).format(calendar.time)
            )
            buf.append(" - ")
            buf.append(error)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
package com.goodpharm.gppandagentlog.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class FileUtils {

    companion object {

        fun openFileChooser(): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
            }
        }

        fun getFileName(context: Context, data: Intent) : String {
            val uri = data.data ?: return ""
            val df = DocumentFile.fromSingleUri(context, data.data!!)
            return df?.name ?: ""
        }

        fun readFile(context: Context, data: Intent): String? {
            data.data?.also { uri ->
//                DLog.d("uri : ${uri}")
                // 권한 유지
                val contentResolver = context.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                val txt = readTextFromUri(context, uri)
                return txt
            }

            return null
        }

        @Throws(IOException::class)
        private fun readTextFromUri(context: Context, uri: Uri): String {
            val contentResolver = context.contentResolver
            val stringBuilder = StringBuilder()
            Log.d("####", "uri ${uri.toString()}")
            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "euc-kr")).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line + "\n")
//                        Log.d("####", line)
                        line = reader.readLine()
                    }
                    inputStream.close()
                }
            }
            return stringBuilder.toString()
        }
    }


}
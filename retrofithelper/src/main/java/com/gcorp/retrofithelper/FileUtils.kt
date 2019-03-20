package com.gcorp.retrofithelper

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.DrawableRes

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object FileUtils {

    fun fileToPart(file: File): MultipartBody.Part {
        return FileUtils.fileToPart(file,"file")
    }

    fun fileToPart(file: File,name:String): MultipartBody.Part {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return MultipartBody.Part.createFormData(name, file.name, requestFile)
    }

    fun bitmapToPart(activity: Activity, bitmap: Bitmap, name: String): MultipartBody.Part {
        return fileToPart(bitmapToFile(activity, bitmap, name),name)
    }

    private fun bitmapToFile(activity: Activity, bitmap: Bitmap, name: String): File {
        val filesDir = activity.filesDir
        val imageFile = File(filesDir, "$name.jpg")

        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e("RetrofitHelper", "FileUtils::bitmapToFile error -> " + e.message)
        }

        return imageFile
    }


    fun fileToBitmap(file: File): Bitmap {
        return BitmapFactory.decodeFile(file.path)
    }

}

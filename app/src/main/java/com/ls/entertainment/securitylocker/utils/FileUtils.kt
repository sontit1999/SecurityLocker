package com.ls.entertainment.securitylocker.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.ls.entertainment.securitylocker.App
import com.ls.entertainment.securitylocker.adapter.WallpaperModel
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*


object FileUtils {
	@SuppressLint("LogNotTimber")
	fun writeToDisk(body: ResponseBody, folderToSave: String): Pair<Boolean, String?> {
		try {
			// Link đến forder muốn lưu
			val mediaStorageDir = File(
				Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				folderToSave
			)
			
			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.e(
						"ahihi", "Oops! Failed create "
								+ folderToSave + " directory"
					)
				}
			}
			
			// tạo file trong folder
			val futureStudioIconFile = File(
				mediaStorageDir.path + File.separator
					.toString() + System.currentTimeMillis() + ".png"
			)
			
			var inputStream: InputStream? = null
			var outputStream: OutputStream? = null
			try {
				val fileReader = ByteArray(4096)
				var fileSizeDownloaded: Long = 0
				inputStream = body.byteStream()
				outputStream = FileOutputStream(futureStudioIconFile)
				while (true) {
					val read = inputStream.read(fileReader)
					if (read == -1) {
						break
					}
					outputStream.write(fileReader, 0, read)
					fileSizeDownloaded += read.toLong()
				}
				outputStream.flush()
				Log.d(
					"ahihi",
					"file download success and path: " + futureStudioIconFile.path
				)
				return Pair(true, futureStudioIconFile.path)
			} catch (e: IOException) {
				return Pair(false, null)
			} finally {
				inputStream?.close()
				outputStream?.close()
			}
		} catch (e: IOException) {
			return Pair(false, null)
		}
	}
	
	fun localStorageQuery(): MutableList<WallpaperModel> {
		val results = mutableListOf<WallpaperModel>()
		val projection = arrayOf(
			MediaStore.Files.FileColumns._ID,
			MediaStore.Files.FileColumns.DISPLAY_NAME,
			MediaStore.Files.FileColumns.MEDIA_TYPE
		)
		val selection =
			"${MediaStore.Files.FileColumns.MEDIA_TYPE} = " + "${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}"
		
		val order = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
		val destination = MediaStore.Files.getContentUri("external")
		App.instance.contentResolver.query(
			destination, projection, selection, null, order
		)?.use {
			while (it.moveToNext()) {
				val id = it.getLong(
					it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
				)
				val isVideo = it.getInt(
					it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
				) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
				
				val name = it.getString(
					it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
				)
				val url = ContentUris.withAppendedId(
					if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI
					else MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
				).toString()
				results.add(WallpaperModel(System.currentTimeMillis().toInt(), name, url, true))
			}
		}
		return results
	}
	
	fun getImageDownloaded(): MutableList<WallpaperModel> {
		val results = mutableListOf<WallpaperModel>()
		val folderDownloaded = File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
			Constant.nameFolderDownloadImage
		)
		
		val listFile = folderDownloaded.listFiles()
		listFile.forEach {
			results.add(WallpaperModel(System.currentTimeMillis().toInt(), it.name, it.path, true))
		}
		return results
	}
	
	
	fun updateFileToGallery(path: String) {
		val file = File(path)
		if (!file.exists()) return
		MediaScannerConnection.scanFile(
			App.instance.applicationContext, arrayOf(file.toString()), null, null
		)
		Timber.d("Scan file ok!")
	}
	
	
	fun getConfigLocal(): String {
		return try {
			val fileName = "config_default"
			val jsonString =
				App.instance.applicationContext.assets.open(fileName).bufferedReader().use {
					it.readText()
				}
			jsonString
		} catch (e: Exception) {
			""
		}
	}
	
	fun getMinDataLocal(): String {
		return try {
			val fileName = "mindata"
			val jsonString =
				App.instance.applicationContext.assets.open(fileName).bufferedReader().use {
					it.readText()
				}
			jsonString
		} catch (e: Exception) {
			""
		}
	}
	
	fun readFileText(fileName: String): String {
		var string: String = ""
		try {
			val inputStream: InputStream = App.instance.applicationContext.assets.open(fileName)
			val size = inputStream.available()
			val buffer = ByteArray(size)
			inputStream.read(buffer)
			string = String(buffer)
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return string
	}
}
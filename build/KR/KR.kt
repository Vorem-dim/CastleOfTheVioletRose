import korlibs.audio.sound.*
import korlibs.io.file.*
import korlibs.io.file.std.*
import korlibs.image.bitmap.*
import korlibs.image.atlas.*
import korlibs.image.font.*
import korlibs.image.format.*

// AUTO-GENERATED FILE! DO NOT MODIFY!

@Retention(AnnotationRetention.BINARY) annotation class ResourceVfsPath(val path: String)
inline class TypedVfsFile(val __file: VfsFile)
inline class TypedVfsFileTTF(val __file: VfsFile) {
  suspend fun read(): korlibs.image.font.TtfFont = this.__file.readTtfFont()
}
inline class TypedVfsFileBitmap(val __file: VfsFile) {
  suspend fun read(): korlibs.image.bitmap.Bitmap = this.__file.readBitmap()
  suspend fun readSlice(atlas: MutableAtlasUnit? = null, name: String? = null): BmpSlice = this.__file.readBitmapSlice(name, atlas)
}
inline class TypedVfsFileSound(val __file: VfsFile) {
  suspend fun read(): korlibs.audio.sound.Sound = this.__file.readSound()
}
interface TypedAtlas<T>

object KR : __KR.KR

object __KR {
	
	interface KR {
		val __file get() = resourcesVfs[""]
		@ResourceVfsPath("audio") val `audio` get() = __KR.KRAudio
		@ResourceVfsPath("fonts") val `fonts` get() = __KR.KRFonts
		@ResourceVfsPath("images") val `images` get() = __KR.KRImages
	}
	
	object KRAudio {
		val __file get() = resourcesVfs["audio"]
		@ResourceVfsPath("audio/main_sound.mp3") val `mainSound` get() = TypedVfsFileSound(resourcesVfs["audio/main_sound.mp3"])
	}
	
	object KRFonts {
		val __file get() = resourcesVfs["fonts"]
		@ResourceVfsPath("fonts/primary.ttf") val `primary` get() = TypedVfsFileTTF(resourcesVfs["fonts/primary.ttf"])
		@ResourceVfsPath("fonts/secondary.ttf") val `secondary` get() = TypedVfsFileTTF(resourcesVfs["fonts/secondary.ttf"])
	}
	
	object KRImages {
		val __file get() = resourcesVfs["images"]
		@ResourceVfsPath("images/icon_rose.png") val `iconRose` get() = TypedVfsFileBitmap(resourcesVfs["images/icon_rose.png"])
		@ResourceVfsPath("images/main_screen.jpg") val `mainScreen` get() = TypedVfsFileBitmap(resourcesVfs["images/main_screen.jpg"])
	}
}
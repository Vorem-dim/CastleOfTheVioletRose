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
		@ResourceVfsPath("map") val `map` get() = __KR.KRMap
		@ResourceVfsPath("sprites") val `sprites` get() = __KR.KRSprites
	}
	
	object KRAudio {
		val __file get() = resourcesVfs["audio"]
		@ResourceVfsPath("audio/main_sound.mp3") val `mainSound` get() = TypedVfsFileSound(resourcesVfs["audio/main_sound.mp3"])
		@ResourceVfsPath("audio/select.mp3") val `select` get() = TypedVfsFileSound(resourcesVfs["audio/select.mp3"])
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
	
	object KRMap {
		val __file get() = resourcesVfs["map"]
		@ResourceVfsPath("map/simplified") val `simplified` get() = __KR.KRMapSimplified
	}
	
	object KRSprites {
		val __file get() = resourcesVfs["sprites"]
		@ResourceVfsPath("sprites/bosses") val `bosses` get() = __KR.KRSpritesBosses
		@ResourceVfsPath("sprites/heroes") val `heroes` get() = __KR.KRSpritesHeroes
	}
	
	object KRMapSimplified {
		val __file get() = resourcesVfs["map/simplified"]
		@ResourceVfsPath("map/simplified/AutoLayer") val `autolayer` get() = __KR.KRMapSimplifiedAutolayer
	}
	
	object KRSpritesBosses {
		val __file get() = resourcesVfs["sprites/bosses"]
	}
	
	object KRSpritesHeroes {
		val __file get() = resourcesVfs["sprites/heroes"]
		@ResourceVfsPath("sprites/heroes/anomaly.png") val `anomaly` get() = TypedVfsFileBitmap(resourcesVfs["sprites/heroes/anomaly.png"])
		@ResourceVfsPath("sprites/heroes/knight.png") val `knight` get() = TypedVfsFileBitmap(resourcesVfs["sprites/heroes/knight.png"])
		@ResourceVfsPath("sprites/heroes/thief.png") val `thief` get() = TypedVfsFileBitmap(resourcesVfs["sprites/heroes/thief.png"])
	}
	
	object KRMapSimplifiedAutolayer {
		val __file get() = resourcesVfs["map/simplified/AutoLayer"]
		@ResourceVfsPath("map/simplified/AutoLayer/IntGrid_layer-int.png") val `intgridLayerInt` get() = TypedVfsFileBitmap(resourcesVfs["map/simplified/AutoLayer/IntGrid_layer-int.png"])
		@ResourceVfsPath("map/simplified/AutoLayer/IntGrid_layer.csv") val `intgridLayer` get() = TypedVfsFile(resourcesVfs["map/simplified/AutoLayer/IntGrid_layer.csv"])
		@ResourceVfsPath("map/simplified/AutoLayer/_bg.png") val `bg` get() = TypedVfsFileBitmap(resourcesVfs["map/simplified/AutoLayer/_bg.png"])
		@ResourceVfsPath("map/simplified/AutoLayer/_composite.png") val `composite` get() = TypedVfsFileBitmap(resourcesVfs["map/simplified/AutoLayer/_composite.png"])
		@ResourceVfsPath("map/simplified/AutoLayer/data.json") val `data` get() = TypedVfsFile(resourcesVfs["map/simplified/AutoLayer/data.json"])
	}
}

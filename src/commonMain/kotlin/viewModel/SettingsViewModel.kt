package viewModel

import korlibs.audio.sound.*
import korlibs.image.color.*
import korlibs.image.font.*
import korlibs.korge.service.storage.*
import model.*
import model.heroes.*

class SettingsViewModel(private val storage: NativeStorage, private val settings: Settings) {
    private val soundChannels: MutableMap<String, SoundChannel> = mutableMapOf()

    init {
        settings.apply {
            music.values.forEach { music -> music.volume = musicVolume }
            sound.values.forEach { sound -> sound.volume = soundVolume }
        }
    }

    suspend fun playSound(name: String, playback: PlaybackTimes = PlaybackTimes.ONE) {
        if (playback == PlaybackTimes.ONE)
            settings.sound[name]?.play()
        else
            soundChannels[name]?.resume() ?: settings.music[name]?.also {
                soundChannels[name] = it.play(playback)
            }
    }

    fun writeStorage(tag: String, value: String) { storage[tag] = value }

    fun resetCurrentLevel() { settings.currentLevel = 0 }

    fun setTurbo() { settings.apply { turboMode = !turboMode } }

    fun setMusicVolume(tag: String, volume: Double) {
        settings.apply {
            musicVolume = volume
            music[tag]?.volume = volume
        }
        soundChannels[tag]?.volume = volume
    }

    fun setSoundVolume(tag: String, volume: Double) {
        settings.apply {
            soundVolume = volume
            sound[tag]?.volume = volume
        }
    }

    fun getHero(tag: String): Hero = settings.heroes[tag] ?: settings.heroes.values.first()

    fun getSelectedHeroes(): MutableList<Boolean> = settings.selectedHeroes

    fun getTextSize(tag: String): Double = settings.textSizes[tag] ?: 50.0

    fun getColor(tag: String): RGBA = settings.colors[tag] ?: RGBA(0)

    fun getHeroes(): Map<String, Hero> = settings.heroes

    fun getMusicVolume(): Double = settings.musicVolume

    fun getSoundVolume(): Double = settings.soundVolume

    fun getCurrentLevel(): Int = settings.currentLevel

    fun getTurbo(): Boolean = settings.turboMode

    fun violetRose(): Int = settings.violetRose

    fun getFont(): Font = settings.textFont
}

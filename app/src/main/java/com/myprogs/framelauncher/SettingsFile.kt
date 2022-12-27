package com.myprogs.framelauncher

import java.io.File

class SettingsFile {
    private val defaultSettings = "" +
            "   ###############################\n" +
            "   # FrameLauncher settings file #\n" +
            "   ###############################\n" +
            "Brightness: 255        #system brightness (0-255)\n" +
            "Folder: 0      #index of folder in /sdcard/FrameFolder\n" +
            "Delay: 60      #delay after slide change (sec.)\n" +
            "Scale: 1       #image resize method (0-none, 1-fit, 2-crop)\n"

    private var settings = ""
    private lateinit var names: Sequence<String>
    private lateinit var values: Sequence<Int>
    private lateinit var settingsMap: MutableMap<String, Int>
    private val sdPath = "/storage/sdcard1"
    fun start() {
        val setFolder = File(sdPath, "FrameFolder")
        if (!setFolder.exists()) setFolder.mkdir()
        val settingsFile = File(setFolder, "Settings.txt")
        if (!settingsFile.exists()) {
            settingsFile.createNewFile()
            settingsFile.appendText(defaultSettings)
        }
        settings = Regex("(?:#.*)").replace(settingsFile.readText(), "")
        names = Regex("[A-Za-z|_]+").findAll(settings).map { it.value }
        values = Regex("\\d+").findAll(settings).map { it.value.toInt() }
        settingsMap = names.zip(values).toMap().toMutableMap()

    }

    fun read(name: String): Int {
        return settingsMap.getOrElse(key = name) {
            throw Exception("unable to read '$name'. name must be in ${settingsMap.keys}")
        }
    }

    fun update(name: String, value: Int) { settingsMap[name] = value }

    fun save() {
        val file = File("$sdPath/FrameFolder", "Settings.txt")
        var text = file.readText()
        settingsMap.forEach {
            val regex = "${it.key}+: \\d+".toRegex()
            text = Regex(regex.pattern).replace(text, "${it.key}: ${it.value}")
        }
        file.writeText(text)
    }

    fun getFolders(): Map<Int, String> {
        val file = File("$sdPath/FrameFolder/")
        val names = file.walk().filter { it.name != "FrameFolder" && it.isDirectory }
        val folderMap = mutableMapOf<Int, String>()
        var i = 0
        names.forEach { folderMap[i] = it.absolutePath;i++ }
        if (folderMap.isEmpty()) folderMap[0] = "none"
        return folderMap
    }
}
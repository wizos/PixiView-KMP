package me.matsumo.fanbox.core.logs.category

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// This class is automatically generated by generate-log-classes.
sealed class SettingsLog : LogCategory {

    class Init internal constructor(
        private val settings: String,
    ) : SettingsLog() {
        override val properties: JsonObject = buildJsonObject {
            put("event_category", "settings")
            put("event_name", "init")
            put("settings", settings)
        }
    }

    class Update internal constructor(
        private val propertyName: String,
        private val oldValue: String,
        private val newValue: String,
    ) : SettingsLog() {
        override val properties: JsonObject = buildJsonObject {
            put("event_category", "settings")
            put("event_name", "update")
            put("property_name", propertyName)
            put("old_value", oldValue)
            put("new_value", newValue)
        }
    }

    companion object {
        // アプリの設定が初期化された際に出力されるログ
        fun init(
            settings: String,
        ) = Init(settings)

        // アプリの設定が更新された際に出力されるログ
        fun update(
            propertyName: String,
            oldValue: String,
            newValue: String,
        ) = Update(propertyName, oldValue, newValue)
    }
}

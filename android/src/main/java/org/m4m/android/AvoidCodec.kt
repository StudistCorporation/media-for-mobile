package org.m4m.android

import android.media.MediaCodec
import android.media.MediaCodecList
import jp.studist.teachme_biz.controller.util.LogUtil
import java.io.IOException

/**
 * 一部のコーデックにおいてエンコードまたはデコード、その他の処理が
 * 正常に動作しない事象を確認したため、そのコーデックをブラックリストとして定義。
 * 現在はSamsung製のコーデックで確認したが、他にもベンダー独自のコーデックを仕込んでる可能性があるため
 * enumで列挙する
 */
enum class AvoidCodec(var codecPartialName: String) {
    // Samsung社製のコーデック
    EXYNOS("Exynos");

    companion object {
        fun avoidBlackListCodec(mimeType: String): MediaCodec? {

            MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos.filter { codecInfo ->
                codecInfo.supportedTypes.contains(mimeType)
            }.first {
                !containsShouldAvoidCodec(it.name)
            }.let { codecInfo ->
                try {
                    return MediaCodec.createByCodecName(codecInfo.name)
                } catch (e: IOException) {
                    LogUtil.stackTrace(e)
                    e.printStackTrace()
                }
            }

            return null
        }

        private fun containsShouldAvoidCodec(codecName: String): Boolean {
            values().forEach { avoidCodec ->
                if (codecName.contains(avoidCodec.codecPartialName, ignoreCase = true)) {
                    return true
                }
            }

            return false
        }
    }

}
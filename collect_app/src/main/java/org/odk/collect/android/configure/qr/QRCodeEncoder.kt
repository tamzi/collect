package org.odk.collect.android.configure.qr

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.odk.collect.android.R
import org.odk.collect.android.application.Collect
import org.odk.collect.android.utilities.CompressionUtils
import org.odk.collect.strings.localization.getLocalizedString
import java.io.IOException

class QRCodeEncoderImpl : QRCodeEncoder {
    override fun encode(data: String): Bitmap {
        val compressedData = CompressionUtils.compress(data)

        // Maximum capacity for QR Codes is 4,296 characters (Alphanumeric)
        if (compressedData.length > 4000) {
            throw IOException(Collect.getInstance().getLocalizedString(R.string.encoding_max_limit))
        }

        val hints: Map<EncodeHintType, ErrorCorrectionLevel> = mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L)

        val bitMatrix = QRCodeWriter().encode(
            compressedData,
            BarcodeFormat.QR_CODE,
            QR_CODE_SIDE_LENGTH,
            QR_CODE_SIDE_LENGTH,
            hints
        )

        val bmp = Bitmap.createBitmap(
            QR_CODE_SIDE_LENGTH,
            QR_CODE_SIDE_LENGTH,
            Bitmap.Config.RGB_565
        )

        for (x in 0 until QR_CODE_SIDE_LENGTH) {
            for (y in 0 until QR_CODE_SIDE_LENGTH) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bmp
    }

    private companion object {
        private const val QR_CODE_SIDE_LENGTH = 400 // in pixels
    }
}

interface QRCodeEncoder {
    fun encode(data: String): Bitmap
}

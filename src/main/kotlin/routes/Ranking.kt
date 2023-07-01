/*
 * Copyright 2023 Avery Carroll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package routes

import constants.Colors
import constants.Fonts
import exceptions.BadGatewayResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Ranking
import utils.HTTP_CLIENT
import utils.useResourceStream
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.min
import kotlin.math.roundToInt

fun Route.rankingRouting() {
    route("/ranking") {
        post {
            val rankingModel = call.receive<Ranking>()
            call.respondBytes { drawRankingImage(rankingModel) }
        }
    }
}

suspend fun drawRankingImage(rankingModel: Ranking): ByteArray = with(rankingModel) {
    val image = BufferedImage(/* width = */ 3438, /* height = */ 1146, /* imageType = */ BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()

    // enable antialiasing for text because it looks nice
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

    graphics.color = Colors.DARK_GREY
    // use these bounds to fill a dark grey background to support transparent pixels later on
    val avatarBounds = Rectangle(/* x = */ 2317, /* y = */ 119, /* width = */ 908, /* height = */ 908)
    graphics.fill(avatarBounds)
    val progressBarBounds = Rectangle(/* x = */ 212, /* y = */ 168, /* width = */ 1916, /* height = */ 232)
    graphics.fill(progressBarBounds)

    // compute xp progress as a float percentage between 0 and 1
    val percentage = experience.toFloat() / goal.toFloat()
    // multiply the bound width and percentage to find the actual width
    val barWidth = (progressBarBounds.width * percentage).roundToInt()
    graphics.color = Colors.BLURPLE
    // draw the progress bar as a simple rectangle
    graphics.fillRect(progressBarBounds.x, progressBarBounds.y, barWidth, progressBarBounds.height)

    // download the user's avatar and draw it next
    val avatar = HTTP_CLIENT.get("$avatarUrl?size=2048").body<InputStream>().let { ImageIO.read(it) }
        ?: throw BadGatewayResponse("Failed to load avatar from url")
    graphics.drawImage(avatar, avatarBounds.x, avatarBounds.y, avatarBounds.width, avatarBounds.height, null)

    // draw the ranking template next to smooth out corners
    val templateStream = useResourceStream("images/ranking_template.png") { ImageIO.read(this) }
    graphics.drawImage(templateStream, 0, 0, image.width, image.height, null)

    graphics.color = Colors.WHITE
    // calculate the necessary fonts and locations for the text
    graphics.font = Fonts.ALBA.deriveFont(/* size = */ 156f)
    var metrics = graphics.fontMetrics

    // draw the exp text centered within the progress bar bounds
    val formatLocale = Locale.forLanguageTag(locale) ?: Locale.ENGLISH
    val progressBarText = "${"%,d".format(formatLocale, experience)} / ${"%,d".format(formatLocale, goal)} xp"
    val barTextX = progressBarBounds.x + (progressBarBounds.width - metrics.stringWidth(progressBarText)) / 2
    graphics.drawString(/* str = */ progressBarText, /* x = */ barTextX, /* y = */ 336)

    // resize the font and metrics for the next line of text
    graphics.font = graphics.font.deriveFont(/* size = */ 196f)
    metrics = graphics.fontMetrics

    // draw the lvl statement left aligned
    graphics.drawString(/* str = */ "lvl $level", /* x = */ 181, /* y = */ 650)
    val percentageText = "${(percentage * 100).roundToInt()}%"
    // draw the percentage statement right aligned on the same line
    graphics.drawString(/* str = */ percentageText, /* x = */ 2158 - metrics.stringWidth(percentageText), /* y = */ 650)

    // calculate the maximum font size for the username to fill the given bounds
    val largestFont = graphics.font.deriveFont(/* size = */ 256f)
    val usernameWidth = graphics.getFontMetrics(largestFont).stringWidth(username)
    val usernameBounds = Rectangle(181, 690, 1978, 375)
    val adjustedSize = (usernameBounds.width / usernameWidth.toFloat()) * 256f

    // resize the font and metrics for the next line of text
    graphics.font = graphics.font.deriveFont(min(adjustedSize, 256f))
    metrics = graphics.fontMetrics

    // draw the username centered vertically and left aligned
    val usernameY = usernameBounds.y + ((usernameBounds.height - metrics.height) / 2) + metrics.ascent
    graphics.drawString(username, usernameBounds.x, usernameY)

    // encode the image as a stream
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(image, "png", outputStream)
    return outputStream.toByteArray()
}

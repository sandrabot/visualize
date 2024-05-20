/*
 * Copyright 2024 Avery Carroll and contributors
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

package plugins

import BuildInfo
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import kotlin.time.Duration.Companion.milliseconds

val LOGGER: Logger = LoggerFactory.getLogger("Visualize")

fun Application.configureLogging() {
    LOGGER.info("Configuring Visualize ${BuildInfo.DETAILED_VERSION}")
    install(CallLogging) {
        level = Level.INFO
        logger = LOGGER
        format { call ->
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val status = call.response.status()
            val processingTime = call.processingTimeMillis().milliseconds
            val userAgent = call.request.userAgent()

            "$method $uri | $status | $processingTime | $userAgent"
        }
    }
}

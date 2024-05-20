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

import exceptions.CallResponseException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import utils.respondJson

fun Application.configureStatusPages() {
    install(StatusPages) {
        val failures = HttpStatusCode.allStatusCodes.filter { it.value in 400 until 600 }.toTypedArray()
        status(*failures) { call, status ->
            call.respondJson(status, success = false, "message" to status.description)
        }
        exception<CallResponseException> { call, cause ->
            call.respondJson(cause.status, success = false, "message" to cause.message)
        }
        exception<Throwable> { call, cause ->
            val status = HttpStatusCode.InternalServerError
            LOGGER.error("An uncaught exception was thrown while processing a request", cause)
            call.respondJson(status, success = false, "message" to status.description)
        }
    }
}

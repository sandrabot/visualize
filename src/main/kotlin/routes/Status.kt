/*
 * Copyright 2023 Avery Carroll and contributors
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

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import utils.respondJson

private val START_TIME = getTimeMillis()

fun Route.statusRouting() {
    route("/status") {
        get {
            val uptime = (getTimeMillis() - START_TIME) / 1000
            call.respondJson(data = arrayOf("uptime" to uptime))
        }
    }
}

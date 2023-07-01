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

package utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.*

suspend fun ApplicationCall.respondJson(
    status: HttpStatusCode = HttpStatusCode.OK, success: Boolean = true, vararg data: Pair<String, Any?>
) {
    val dataMap = mapOf(*data, "status" to status.value, "success" to success, "version" to BuildInfo.VERSION)
    respond(status = status, dataMap.toJsonObject())
}

fun Map<*, *>.toJsonObject() = buildJsonObject {
    forEach { (key, value) -> put(key.toString(), value.toJsonElement()) }
}

fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Map<*, *> -> toJsonObject()
    is Iterable<*> -> toJsonArray()
    is Array<*> -> toJsonArray()
    else -> JsonPrimitive(toString())
}

fun Iterable<*>.toJsonArray() = JsonArray(map { it.toJsonElement() })
fun Array<*>.toJsonArray() = JsonArray(map { it.toJsonElement() })

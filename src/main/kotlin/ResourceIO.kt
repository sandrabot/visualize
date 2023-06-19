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

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import java.io.InputStream

val HTTP_CLIENT = HttpClient(Java) {
    install(UserAgent) {
        agent = "Sandra/Visualize/${BuildInfo.VERSION} (+https://sandrabot.com)"
    }
}

fun <T> useResourceStream(path: String, block: InputStream.() -> T): T =
    object {}.javaClass.classLoader.getResourceAsStream(path)?.use(block)
        ?: throw IllegalArgumentException("Unable to load resource: $path")

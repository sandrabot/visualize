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

package models

import kotlinx.serialization.Serializable
import utils.LocaleSerializer
import java.util.*

@Serializable
data class RankingContext(
    val name: String,
    val avatarUrl: String,
    val experience: Int = 0,
    val goal: Int = 100,
    val level: Int = 0,
    @Serializable(with = LocaleSerializer::class) val locale: Locale = Locale.US
)

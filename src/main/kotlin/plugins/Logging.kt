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

package plugins

import BuildInfo
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.date.*
import io.ktor.util.pipeline.*

internal val CALL_START_TIME = AttributeKey<Long>("CallStartTime")

fun Application.configureLogging() {
    log.info("Configuring Visualize ${BuildInfo.DETAILED_VERSION}")
    install(CallLoggingOnce)
}

fun ApplicationCall.processingTime() = getTimeMillis() - attributes[CALL_START_TIME]

val CallLoggingOnce = createApplicationPlugin("CallLoggingOnce") {
    on(CallSetup) { call ->
        call.attributes.put(CALL_START_TIME, getTimeMillis())
    }

    on(BeforeEngine) {
        if (isHandled) application.log.info(
            "${response.status()} | ${processingTime()}ms | ${request.httpMethod.value} ${request.uri}"
        )
    }
}

internal object BeforeEngine : Hook<ApplicationCall.() -> Unit> {
    override fun install(pipeline: ApplicationCallPipeline, handler: ApplicationCall.() -> Unit) {
        val phase = PipelinePhase("BeforeEngine")
        pipeline.sendPipeline.insertPhaseBefore(ApplicationSendPipeline.Engine, phase)
        pipeline.sendPipeline.intercept(phase) {
            // some calls may only have a status after they are sent
            if (call.response.status() == null) proceed()
            handler(call)
        }
    }
}

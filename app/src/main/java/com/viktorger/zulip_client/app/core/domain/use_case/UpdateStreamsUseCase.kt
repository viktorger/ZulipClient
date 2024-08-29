package com.viktorger.zulip_client.app.core.domain.use_case

import com.viktorger.zulip_client.app.core.domain.repository.StreamsRepository
import javax.inject.Inject

class UpdateStreamsUseCase @Inject constructor(private val streamsRepository: StreamsRepository) {
    operator fun invoke(isSubscribed: Boolean) =
        if (isSubscribed) {
            streamsRepository.updateSubscribedStreams()
        } else {
            streamsRepository.updateAllStreams()
        }
}
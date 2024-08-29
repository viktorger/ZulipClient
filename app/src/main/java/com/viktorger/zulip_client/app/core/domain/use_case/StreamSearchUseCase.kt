package com.viktorger.zulip_client.app.core.domain.use_case

import com.viktorger.zulip_client.app.core.domain.repository.StreamsRepository
import com.viktorger.zulip_client.app.core.model.StreamModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamSearchUseCase @Inject constructor(private val streamsRepository: StreamsRepository) {
    operator fun invoke(
        query: String,
        isSubscribed: Boolean
    ): Flow<List<StreamModel>> = if (isSubscribed) {
        streamsRepository.getSubscribedStreams(query)
    } else {
        streamsRepository.getAllStreams(query)
    }

}
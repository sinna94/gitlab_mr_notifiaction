package me.chung.gitlab

import com.fasterxml.jackson.core.type.TypeReference
import me.chung.utils.ObjectMapperBuilder
import me.chung.utils.SystemEnvironmentVariableProvider
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GitLabMergeRequestFetcher {

    companion object {
        private const val PRIVATE_TOKEN = "PRIVATE-TOKEN"
        private const val BASE_URL = "https://gitlab.com/api/v4"
    }

    fun fetchMergeRequests(): List<MergeRequest> {
        val gitlabToken = SystemEnvironmentVariableProvider.getEnvVariable("GITLAB_TOKEN")
        val gitlabGroupId = SystemEnvironmentVariableProvider.getEnvVariable("GITLAB_GROUP_ID")

        val response = sendHttpRequest(gitlabToken, gitlabGroupId)
        return parseResponse(response)
    }

    private fun sendHttpRequest(gitlabToken: String, gitlabGroupId: String): HttpResponse<String> {
        val httpClient = HttpClient.newHttpClient()
        return httpClient.send(
            HttpRequest.newBuilder(URI("$BASE_URL/groups/${gitlabGroupId}/merge_requests?state=opened")).GET()
                .header(PRIVATE_TOKEN, gitlabToken)
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
    }

    private fun parseResponse(response: HttpResponse<String>): List<MergeRequest> {
        val objectMapper = ObjectMapperBuilder.build()
        return objectMapper.readValue(response.body(), object : TypeReference<List<MergeRequest>>() {})
    }
}
package me.chung.gitlab

class GitLabMergeRequestFetchException(errorResponse: ErrorResponse) :
    RuntimeException("Failed to fetch merge requests from GitLab. Error: ${errorResponse.error}, Description: ${errorResponse.errorDescription}")
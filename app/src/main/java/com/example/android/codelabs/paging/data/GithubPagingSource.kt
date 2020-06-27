package com.example.android.codelabs.paging.data

import androidx.paging.PagingSource
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.api.IN_QUALIFIER
import com.example.android.codelabs.paging.model.Repo
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val GITHUB_STARTING_PAGE_INDEX = 1

class GithubPagingSource(
        private val service: GithubService,
        private val query: String
) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val currentPage = params.key ?: GITHUB_STARTING_PAGE_INDEX
        val apiQuery = query + IN_QUALIFIER
        return try {
            val response = service.searchRepos(apiQuery, currentPage, params.loadSize)
            val repos = response.items
            LoadResult.Page(
                    data = repos,
                    prevKey = if (currentPage > 1) currentPage - 1 else null,
                    nextKey = if (repos.any()) currentPage + 1 else null
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}

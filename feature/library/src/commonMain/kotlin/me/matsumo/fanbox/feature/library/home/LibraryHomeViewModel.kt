package me.matsumo.fanbox.feature.library.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.matsumo.fanbox.core.common.util.suspendRunCatching
import me.matsumo.fanbox.core.model.UserData
import me.matsumo.fanbox.core.repository.FanboxRepository
import me.matsumo.fanbox.core.repository.UserDataRepository
import me.matsumo.fanbox.core.ui.extensition.emptyPaging
import me.matsumo.fankt.fanbox.domain.model.FanboxPost
import me.matsumo.fankt.fanbox.domain.model.id.FanboxPostId

class LibraryHomeViewModel(
    private val userDataRepository: UserDataRepository,
    private val fanboxRepository: FanboxRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LibraryUiState(
            userData = UserData.default(),
            bookmarkedPostsIds = emptyList(),
            homePaging = emptyPaging(),
            supportedPaging = emptyPaging(),
        ),
    )

    val uiState = _uiState.asStateFlow()
    val updatePlusTrigger = userDataRepository.updatePlusMode

    init {
        viewModelScope.launch {
            userDataRepository.userData.collectLatest { userData ->
                val loadSize = if (userData.isHideRestricted || userData.isUseGridMode) 20 else 10
                val isHideRestricted = userData.isHideRestricted

                _uiState.value = uiState.value.copy(
                    userData = userData,
                    homePaging = fanboxRepository.getHomePostsPager(loadSize, isHideRestricted),
                    supportedPaging = fanboxRepository.getSupportedPostsPager(loadSize, isHideRestricted),
                )
            }
        }

        viewModelScope.launch {
            fanboxRepository.bookmarkedPostsIds.collectLatest {
                _uiState.value = uiState.value.copy(bookmarkedPostsIds = it)
            }
        }
    }

    fun postLike(postId: FanboxPostId) {
        viewModelScope.launch {
            suspendRunCatching {
                fanboxRepository.likePost(postId)
            }
        }
    }

    fun postBookmark(post: FanboxPost, isBookmarked: Boolean) {
        viewModelScope.launch {
            suspendRunCatching {
                if (isBookmarked) {
                    fanboxRepository.bookmarkPost(post)
                } else {
                    fanboxRepository.unbookmarkPost(post)
                }
            }
        }
    }
}

@Stable
data class LibraryUiState(
    val userData: UserData,
    val bookmarkedPostsIds: List<FanboxPostId>,
    val homePaging: Flow<PagingData<FanboxPost>>,
    val supportedPaging: Flow<PagingData<FanboxPost>>,
)

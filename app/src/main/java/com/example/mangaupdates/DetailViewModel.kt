import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangaupdates.MangaUpdatesApi.*
import com.example.mangaupdates.MangaUpdatesApi
import com.example.mangaupdates.RetrofitInstance
import com.example.mangaupdates.MangaUpdatesApi.SeriesListStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


class DetailViewModel : ViewModel() {

    private val _seriesListStatus = MutableStateFlow<SeriesListStatus?>(null)
    val seriesListStatus: StateFlow<SeriesListStatus?> = _seriesListStatus

    private val api: MangaUpdatesApi = RetrofitInstance.api

    fun fetchSeriesListStatus(seriesId: Long, token: String) {
        viewModelScope.launch {
            try {
                val bearer = "Bearer $token"
                Log.d("DetailViewModel", "→ GET /lists/series/$seriesId with auth='$bearer'")
                val result = api.getSeriesListStatus(bearer, seriesId)
                Log.d("DetailViewModel", "← 200 OK, payload=$result")
                _seriesListStatus.value = result
            } catch (e: HttpException) {
                Log.e("DetailViewModel", "← HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}", e)
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error fetching list status", e)
            }
        }
    }

    private val _userLists = MutableStateFlow<List<ListInfo>>(emptyList())
    val userLists: StateFlow<List<ListInfo>> = _userLists

    private val _addResult = MutableStateFlow<Boolean?>(null)
    val addResult: StateFlow<Boolean?> = _addResult

    /** call once when DetailScreen appears */
    fun fetchUserLists(userId: Long, token: String) {
        viewModelScope.launch {
            try {
                val lists = api.getUserLists("Bearer $token", userId).results
                    .map { it.record }
                    .map { ListInfo(it.series.id, it.series.title) }
                Log.d("DetailViewModel", "Fetched userLists: $lists")

                _userLists.value = lists
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error loading userLists", e)

                _userLists.value = emptyList()
            }
        }
    }

    /** call when user picks one */
    fun addToList(seriesInfo: SeriesInfo, listId: Long, token: String) {
        viewModelScope.launch {
            try {
                val req = AddSeriesRequest(
                    series = SeriesRef(
                        id    = seriesInfo.id,
                        url   = "",
                        title = seriesInfo.title
                    ),
                    list_id = listId
                )
                api.addSeriesToList("Bearer $token", listOf(req))
                _addResult.value = true
            } catch (e: Exception) {
                _addResult.value = false
            }
        }
    }
}

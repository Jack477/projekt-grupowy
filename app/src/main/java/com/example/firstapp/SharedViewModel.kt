import androidx.lifecycle.ViewModel
import com.example.firstapp.RunSession
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedViewModel : ViewModel() {
    var runSession: MutableList<RunSession> = mutableListOf()
    private val _sessionStoppedLiveData = MutableLiveData<Boolean>()
    val sessionStoppedLiveData: LiveData<Boolean>
        get() = _sessionStoppedLiveData

    fun setSessionStopped(sessionStopped: Boolean) {
        _sessionStoppedLiveData.value = sessionStopped
    }
}